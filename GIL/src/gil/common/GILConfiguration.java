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
package gil.common;

import java.io.File;
import java.nio.ByteOrder;
import javax.xml.xpath.XPathExpressionException;
import gil.io.IExternalSystemAdapter;
import gil.io.IProcessModelAdapter;

/**
 * This is a convenient class to read and validate all GIL application settings.
 * @author Göran Larsson @ LearningWell AB
 */
public class GILConfiguration {

    private static final String PARAM_PM_ADAPTER = "processModelAdapter";
    private static final String PARAM_ES_ADAPTER = "externalSystemAdapter";
    private static final String PARAM_SEL_PATH = "signalExchangeList";
    private static final String PARAM_IDLE_PROCESS_DATA_TRANSFER_INTERVAL = "idleProcessDataTransferInterval";
    private static final String PARAM_ES_ADAPTER_POLL_RATE = "esAdapterReadPollRate";
    private static final String PARAM_ES_ADAPTER_BYTE_ORDER = "esAdapterByteOrder";
    private static final String PARAM_PM_ADAPTER_BYTE_ORDER = "pmAdapterByteOrder";

    ConfigFile _config;
    String _currentParmeter = "";

    private String _esAdapterClassName;
    private String _pmAdapterClassName;
    private String _signalExchangeListPath;
    private int _esAdapterReadPollRate;
    private int _idleProcessDataTransferInterval;
    private ByteOrder _esAdapterByteOrder;
    private ByteOrder _pmAdapterByteOrder;

    public GILConfiguration(ConfigFile config) throws InvalidFileFormatException {
        _config = config;
        
        try {
            _esAdapterClassName = parseAdapterClass(getParameter(PARAM_ES_ADAPTER), IExternalSystemAdapter.class);
            _pmAdapterClassName = parseAdapterClass(getParameter(PARAM_PM_ADAPTER), IProcessModelAdapter.class);
            
            if (!new File(getParameter(PARAM_SEL_PATH)).exists()) {
                throw new InvalidFileFormatException("The given path '" + getParameter(PARAM_SEL_PATH) 
                        + "' does not point to an existing file.");
            }
            _signalExchangeListPath = getParameter(PARAM_SEL_PATH);

            _idleProcessDataTransferInterval = parseInt(getParameter(PARAM_IDLE_PROCESS_DATA_TRANSFER_INTERVAL));
            _esAdapterReadPollRate = parseInt(getParameter(PARAM_ES_ADAPTER_POLL_RATE));           
            _esAdapterByteOrder = parseByteOrder(getParameter(PARAM_ES_ADAPTER_BYTE_ORDER));
            _pmAdapterByteOrder = parseByteOrder(getParameter(PARAM_PM_ADAPTER_BYTE_ORDER));                      

        } catch (InvalidFileFormatException ex) {
           throw new InvalidFileFormatException("When reading config parameter '" + _currentParmeter + "': " + ex.getMessage());           
        }
        catch (XPathExpressionException ex) {
            throw new InvalidFileFormatException("Invalid xml in configuration file.", ex);
        }
    }

    private String getParameter(String paramName) throws XPathExpressionException, InvalidFileFormatException {
        _currentParmeter = paramName;
        return _config.getAppSetting(paramName);
    }

    private ByteOrder parseByteOrder(String orderStr) throws InvalidFileFormatException {

        if (orderStr.equals("LITTLE_ENDIAN"))
            return  ByteOrder.LITTLE_ENDIAN;
        if (orderStr.equals("BIG_ENDIAN"))
            return ByteOrder.BIG_ENDIAN;
        
        throw new InvalidFileFormatException("'" + orderStr + "' is not a valid order string. "
                + "Valid order strings are {LITTLE_ENDIAN | BIG_ENDIAN}.");
    }

    private int parseInt(String intStr) throws InvalidFileFormatException {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException ex) {
            throw new InvalidFileFormatException("The given value '" + intStr
                    + "' is not a valid integer.");
        }
    }

    private String parseAdapterClass(String adapterClassName, Class superInterface) throws InvalidFileFormatException {
        try {
            Class cl = Class.forName(adapterClassName);
            if (!superInterface.isAssignableFrom(cl)) {
                throw new InvalidFileFormatException("'" + adapterClassName + "' does not implement the '"
                        + superInterface.getName() + "' interface.");
            }
            return adapterClassName;
        } catch (ClassNotFoundException ex) {
            throw new InvalidFileFormatException("'" + adapterClassName + "' does not name a valid class.", ex);
        }
    }

    public String getESAdapterClassName() {
        return _esAdapterClassName;
    }

    public String getPMAdapterClassName() {
        return _pmAdapterClassName;
    }

    public String getSignalExchangeListPath() {
        return _signalExchangeListPath;
    }

    public int getESAdapterReadPollRate() {
        return _esAdapterReadPollRate;
    }

    public ByteOrder getESAdapterByteOrder() {
        return _esAdapterByteOrder;
    }

    public ByteOrder getPMAdapterByteOrder() {
        return _pmAdapterByteOrder;
    }

    public int getIdleProcessDataTransferInterval() {
        return _idleProcessDataTransferInterval;
    }
}
