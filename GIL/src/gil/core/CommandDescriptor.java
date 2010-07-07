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
 * Contains the specification of a command. Specifies a command to be invoked by using the {@link Command} class.
 * @author Göran Larsson @ LearningWell AB
 */
public class CommandDescriptor {

    private final String _commandID;
    private final String _documentation;
    private final ParameterDescriptor[] _params;

    /**
     * Constructs a command descriptor.
     * @param commandID A unique identifier for the command.
     * @param documentation An arbitrary text describing the command.
     * @param params An array of objects describing the possible parameters.
     */
    public CommandDescriptor(String commandID, String documentation, ParameterDescriptor[] params) {
        _commandID = commandID;
        _params = params;
        _documentation = documentation;
    }

    /**
     * Constructs a command descriptor with no parameter descriptors.
     * @param commandID A unique identifier for the command.
     * @param documentation An arbitrary text describing the command.
     */
    public CommandDescriptor(String commandID, String documentation) {
        _commandID = commandID;
        _params = new ParameterDescriptor[0];
        _documentation = documentation;
    }
    
    /**
     * Returns the unique id for the command.
     */
    public String getCommandID() {
        return _commandID;
    }

    /**
     * Returns optional and mandatory parameters to use when invoking the command.
     */
    public ParameterDescriptor[] getParameters() {
        return _params;
    }
    
    /**
     * Returns the documentation of the command.
     */
    public String getDocumentation() {
        return _documentation;
    }
}
