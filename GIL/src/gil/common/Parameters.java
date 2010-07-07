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

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a wrapper around a key-value set of parameters, supplying getters that validates parameters
 * depending on their data type.
 * @author Göran Larsson @ LearningWell AB
 */
public class Parameters {

    private Map<String, String> _parameters = new HashMap<String, String>();
    private String _errorMsg = "";

    /**
     * Constructs a parameter object with no parameters.
     */
    public Parameters() {
    }

    public Parameters(Map<String, String> params) {
        _parameters = params;
    }

    /**
     * Returns all parameters as a map with key-value pairs.
     */
    public Map<String, String> getParameters() {
        return _parameters;
    }

    /**
     * @return The parameter value as a string.
     * @throws InvalidParameterException If the parameter does not exist.
     */
    public String getParam(String paramName) throws InvalidParameterException {
        _errorMsg = "When reading parameter '" + paramName + "':";
        String value = _parameters.get(paramName);
        if (value == null)
            throw new InvalidParameterException("Parameter '" + paramName + "' does not exist.");
        return value;
    }

    /**
     * @return The parameter value as a integer.
     * @throws InvalidParameterException If the parameter does not exist or the parameter value is not a valid integer.
     */
    public int getIntParam(String paramName) throws InvalidParameterException {
        String value = getParam(paramName);
        try
        {
            return Integer.parseInt(value);
        } catch(NumberFormatException ex) {
            throw new InvalidParameterException(_errorMsg +  " '" + value + "' is not a valid integer.");
        }
    }

    /**
     * @return The parameter value as a double.
     * @throws InvalidParameterException If the parameter does not exist or the parameter value is not a valid double.
     */
    public double getDoubleParam(String paramName) throws InvalidParameterException {
        String value = getParam(paramName);
        try
        {
            return Double.parseDouble(value);
        } catch(NumberFormatException ex) {
            throw new InvalidParameterException(_errorMsg +  " '" + value + "' is not a valid double.");
        }        
    }
}
