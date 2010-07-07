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
package gil.web.jaxb;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import gil.core.LogfileMetadata;
import gil.core.CommandDescriptor;
import gil.core.ParameterDescriptor;
import gil.core.SoftwareInfo;
import gil.core.StageValueObject;
import gil.common.Helpers;

/**
 * Class to transform domain classes to/from data transfer objects to be serialized/deserialized using JAXB.
 * @author Göran Larsson @ LearningWell AB
 */
public class Transformer {


    public static Command[] transformCommandDescriptors(CommandDescriptor[] cds) {

        Command[] jaxbCommands = new Command[cds.length];

        int cmdCount = 0;
        for (CommandDescriptor cd : cds) {
            ParameterDescriptor[] params = cd.getParameters();
            //ParameterDescriptorJAXB[] jaxbParams = new ParameterDescriptorJAXB[params.length];
            Command.Parameters jaxbParams = new Command.Parameters();

            for (ParameterDescriptor param : params) {
                Command.Parameters.Parameter jaxbParam = new Command.Parameters.Parameter();
                jaxbParam.setDocumentation(param.getDocumentation());
                jaxbParam.setMandatory(param.mandatory());
                jaxbParam.setName(param.getName());                
                jaxbParams.getParameter().add(jaxbParam);
            }

            Command cmd = new Command();
            cmd.setName(cd.getCommandID());
            cmd.setDocumentation(cd.getDocumentation());
            cmd.setParameters(jaxbParams);
            jaxbCommands[cmdCount++] = cmd;
        }
        return jaxbCommands;
    }

    /**
     * @param uri The base uri for the logfiles resources. The qualified name for a logfile is "baseUri/name".
     */
    public static Logfile[] transformLogfileMetadataArray(LogfileMetadata[] mds, URI baseUri) {
        Logfile[] logfiles = new Logfile[mds.length];
        int i = 0;

        for (LogfileMetadata md : mds) {
            UriBuilder builder = UriBuilder.fromUri(baseUri);
            String logfileURI = Helpers.removeFormatSpecifierFromUri(builder.path(md.getName()).build()).toString();
            logfiles[i++] = new Logfile(md.getSizeInBytes(), logfileURI, md.getLastModified());
        }
        return logfiles;
    }

    /**
     * @param uri The base uri for the pipeline stages resources. The qualified name for a stage is "baseUri/name".
     */
    public static PipelineStage[] transformStageArray(StageValueObject[] svos, URI baseUri) {
        PipelineStage[] stages = new PipelineStage[svos.length];
        int i = 0;

        for (StageValueObject svo : svos) {
            UriBuilder builder = UriBuilder.fromUri(baseUri);
            String format = Helpers.retrieveFormatSpecifierFromUri(baseUri);
            String logfileURI = Helpers.removeFormatSpecifierFromUri(builder.path(Integer.toString(svo.seqNo())).path("commands").build()).toString() + format;
            stages[i++] = new PipelineStage(svo.seqNo(), svo.name(), logfileURI);
        }
        return stages;
    }

    
    public static AboutJAXB transformSoftwareInfo(SoftwareInfo gil, SoftwareInfo[] externalSystem, SoftwareInfo[] processModel) {
        return new AboutJAXB(transformVersionInfo(gil), transformVersionInfo(externalSystem), transformVersionInfo(processModel));
    }

    private static VersionInfoJAXB[] transformVersionInfo(SoftwareInfo[] si) {
        VersionInfoJAXB[] vi = new VersionInfoJAXB[si.length];
        for (int i = 0; i < vi.length; i++) {
            vi[i] = transformVersionInfo(si[i]);
        }
        return vi;
    }

    private static VersionInfoJAXB transformVersionInfo(SoftwareInfo source) {
        return new VersionInfoJAXB(source.getName(), source.getVersion(), source.getCompany(), source.getDescription());
    }

}
