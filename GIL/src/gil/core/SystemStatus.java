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
package gil.core;

import java.util.HashMap;

/**
 * @author Göran Larsson @ LearningWell AB
 */
final public class SystemStatus {

    private final String _description;
    private final int _statusCode;

    public SystemStatus(int statusCode, String description) {
        _description = description;
        _statusCode = statusCode;
    }

    public String getDescription() {
        return _description;
    }

    public int getStatusCode() {
        return _statusCode;
    }

    /**
     * Indicates that the status is not yet known.
     */
    public static final int UNKNOWN = 1;

    /**
     * Indicates that the status is not known and will never be.
     */
    public static final int NOT_AVAILABLE = 2;

    /**
     * This is an initial status that is expected to change to either {@link #OK} or {@link #NOK}.
     */
    public static final int INIT = 3;
    
    /**
     * Indicates success.
     */
    public static final int OK = 4;

    /**
     * Indicates failure.
     */
    public static final int NOK = 5;

    private static HashMap<Integer, String> _names = new HashMap<Integer, String>() {
        {
            put(UNKNOWN, "Unknown");
            put(NOT_AVAILABLE, "Not available");
            put(INIT, "Initializing");
            put(OK, "OK");
            put(NOK, "NOK");
        }
    };

    /**
     * Returns a readable name for the given status constant.
     */
    public static String getName(int state) {
        if (!_names.containsKey(state))
            throw new IllegalArgumentException("state");

        return _names.get(state);
    }
}