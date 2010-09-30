/*
    Copyright (C) 2010 LearningWell AB (www.learningwell.com), Kärnkraftsäkerhet och Utbildning AB (www.ksu.se)

    This file is part of GIL (Generic Integration Layer).

    GIL is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GIL is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with GIL.  If not, see <http://www.gnu.org/licenses/>.
*/
package gil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import javax.xml.xpath.XPathExpressionException;
import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;
import gil.core.*;
import gil.io.*;
import gil.common.*;
import gil.web.WebContext;
import gil.web.WebServer;

/**
 * This is the main entry class for the GIL.
 * @author Göran Larsson @ LearningWell AB
 */
public class GIL implements IGIL {
    private static final String CURRENT_VERSION = "0.1.1";
    private static final String CONFIG_PATH = "gil.config.xml";

    public class VersionInfo {
        public VersionInfo(SoftwareInfo[] externalSystemInfo, SoftwareInfo[] processmodelInfo, SoftwareInfo gilInfo) {
            this.externalSystemInfo = externalSystemInfo;
            this.processModelInfo = processmodelInfo;
            this.gilInfo = gilInfo;
        }
        public SoftwareInfo[] externalSystemInfo;
        public SoftwareInfo[] processModelInfo;
        public SoftwareInfo gilInfo;
    }

    private VersionInfo _versionInfo;

    private static final String LOGFILES_PATH = "logfiles" + File.separator;
    private static final String STATIC_WEB_FILES_PATH = "." + File.separator + "files";

    private Logger _logger = Logger.getLogger(GIL.class);
    private IntegrationExecutive _executive = null;
    private WebServer _webServer;
    private static IGIL _instance = null;
    


    // Prevent creation of GIL instances not using the factory method.
    private GIL() {
        WebContext.instance().setStaticFilesFolder(STATIC_WEB_FILES_PATH);
    }

    /**
     * Returns an instance to the only GIL object instance (Singelton pattern).
     */
    public static IGIL instance() {
        if (_instance == null)
            _instance = new GIL();
        return _instance;
    }

    /**
     * To make it possible to inject mocks for unit tests.
     * @param newInstance The new instance to be rturned when {@link #instance()} is called.
     */
    public static void injectInstance(IGIL newInstance) {
        _instance = newInstance;
    }

    /**
     * Starts the GIL runtime.
     */
    public void start(int port) throws Exception {

        if (_executive != null)
            throw new IllegalStateException("Executive allready started");
        
        try  {
            // Configure log4j with for basic console output
            BasicConfigurator.configure();            

            // Read this applications configuration file
            ConfigFile config = ConfigFile.read(CONFIG_PATH);
            GILConfiguration gconfig = new GILConfiguration(config);

            // Reconfigure log4j according to this applications configuration file
            DOMConfigurator.configure(config.getSection("configuration"));
            _logger.info("Starting GIL " + CURRENT_VERSION);
            
            // Read and parse the signal exchange list
            _logger.info("Parses signal exhange list: " + gconfig.getSignalExchangeListPath());
            SignalMetadata[] smd = SignalExchangeList.parse(gconfig.getSignalExchangeListPath());

            // Instantiate the external system adapter object from class name read from the configuration file.
            String className = gconfig.getESAdapterClassName();
            _logger.info("Creating external system adapter: " + className);
            Class cl = Class.forName(className);
            ExternalSystemAdapter es = (ExternalSystemAdapter)cl.newInstance();
            es.setup(SignalMetadata.getSignalsToExternalSystem(smd), SignalMetadata.getSignalsToProcessModel(smd),
                    getAdapterSettings(config, className));

            // Instantiate the process model adapter object from class name read from the configuration file.
            className = gconfig.getPMAdapterClassName();
            _logger.info("Creating process model adapter: " + className);
            cl = Class.forName(className);
            IProcessModelAdapter pm = (IProcessModelAdapter)cl.newInstance();
            pm.setup(SignalMetadata.getSignalsToProcessModel(smd), SignalMetadata.getSignalsToExternalSystem(smd),
                    getAdapterSettings(config, className));

            // Use the settings in the configuration file to dynamically construct the transfer pipeline.
            _logger.info("Building transfer pipeline");
            TransferPipeline pipe = TransferPipelineBuilder.buildPipeline(smd, config);

            _executive = new IntegrationExecutive(pm, es, pipe, smd, gconfig);
                    

            _versionInfo = new VersionInfo(es.getInfo(), pm.getInfo(), new SoftwareInfo(GIL.class.getSimpleName(),
                    "GIL (Generic Integration Layer)", CURRENT_VERSION, "LearningWell"));
            
            _logger.info("Starting integration executive...");
            _executive.start();
            _logger.info("Integration executive started OK");
            _logger.info("Starting GIL web server...");
            _webServer = new WebServer();
            _webServer.start(port, "./files", "/api");
            _logger.info("Web server started OK");

        }
        catch (Exception e) {
            _logger.fatal(e.getMessage(), e);
            throw e;
        }
    }

    public void stop() throws Exception
    {
        if (_executive == null)
            throw new IllegalStateException("Executive not started");
        try {
            _logger.info("Stopping integration executive");
            _executive.stop();
            _executive = null;
            _logger.info("Stopping web server");
            _webServer.stop();
            _webServer = null;
        }
        catch (Exception e) {
            _logger.fatal(e.getMessage(), e);
            throw e;
        }
    }

    public LogfileMetadata[] getLogfileInfo()
    {
        FileFilter onlyFiles = new FileFilter() {
            public boolean accept(File file) { 
                return file.isFile();
            } 
        };

        File dir = new File(LOGFILES_PATH);
        File[] logFiles = dir.listFiles(onlyFiles);
        

        if (logFiles == null)
            return new LogfileMetadata[0];

        ArrayList<LogfileMetadata> mds = new ArrayList<LogfileMetadata>();

        for (File f : logFiles) {
            String s = f.getName();
            mds.add(new LogfileMetadata(f.getName(), new Date(f.lastModified()), f.length()));
        }
        return mds.toArray(new LogfileMetadata[0]);
    }

    public InputStream getLogfile(String name) throws FileNotFoundException
    {
        return new FileInputStream(LOGFILES_PATH + name);
    }

    public VersionInfo getVersionInfo() {
        return _versionInfo;
    }

    public IntegrationExecutive getExecutive() {
        return _executive;
    }

    /**
     * Returns a parameters object containing the settings for the adapter with the given name. If no settings exists, an empty
     * parameters object is returned.
     */
    private Parameters getAdapterSettings(ConfigFile config, String name)
            throws XPathExpressionException, InvalidFileFormatException {

        String settingPath = "adapterSettings/" + name;
        if (config.sectionExists(settingPath)) {
            return new Parameters(config.getPropertiesSection(settingPath));
        }
        return new Parameters();
    }
}
