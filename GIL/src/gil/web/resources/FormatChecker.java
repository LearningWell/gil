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
package gil.web.resources;

import com.sun.jersey.api.NotFoundException;

/**
 * Convenient class used to check if a certain format is available.
 * @author Göran Larsson @ LearningWell AB
 */
public class FormatChecker {

    private final String[] _availableFormats;

    public FormatChecker(String[] availableFormats) {
        _availableFormats = availableFormats;
    }

    /**
     * Throws an exception is the given format does not exist.
     */
    public void checkFormatExists(String format) throws NotFoundException {
        for (String s : _availableFormats) {
            if (s.compareToIgnoreCase(format) == 0) {
                return;
            }
        }
        throw new NotFoundException("Non existing format '" + format + "' requested");
    }
}
