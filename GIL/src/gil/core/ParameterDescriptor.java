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

/**
 * Contains the specification of a parameter to be part of a command specification.
 * @author Göran Larsson @ LearningWell AB
 */
public class ParameterDescriptor {

    private final String _name;
    private final String _documentation;
    private final boolean _mandatory;

    public ParameterDescriptor(String name, String documentation, boolean mandatory) {
        _name = name;
        _documentation = documentation;
        _mandatory = mandatory;
    }

    /**
     * Returns the unique parameter name to be used when invoking a {@link Command}
     */
    public String getName() {
        return _name;
    }
    
    /**
     * Returns the text describing the command.
     */
    public String getDocumentation() {
        return _documentation;
    }

    /**
     * Indicates if this parameter must be supplied or not.
     * <p>
     * @return true if this paranmeter must be supplied when invoking a command or false if it may be left out.
     */
    public boolean mandatory() {
        return _mandatory;
    }
}
