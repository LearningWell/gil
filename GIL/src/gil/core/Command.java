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
import java.util.Map;

/**
 * A object carrying the data neccessary to invoke a command.
 * @author Göran Larsson @ LearningWell AB
 */
public class Command {

    private final String _id;
    private final Map<String, String> _parameters;

    /**
     * Constructs a command with a unique string identifying the command and a collection of parameters as key-value pairs.
     */
    public Command(String id, Map<String, String> parameters) {
        _id = id;
        _parameters = parameters;
    }

    /**
     * Constructs a command with a unique string identifying the command. The command will have no parameters.
     */
    public Command(String command) {
        this(command, new HashMap<String, String>());
    }    

    /**
     * Returns true if this command is the command with the given unique id.
     * <p>
     * The id is case sensitive.
     */
    public boolean isCommand(String id) {
        return _id.equals(id);
    }
    
    /**
     * Returns the unique id for this command.
     * @return A string identifying the actual command. 
     */
    public String getID() {
        return _id;
    }

    /**
     * Returns a list of the parameters for this command.
     * @return The parameters as key-value pairs.
     */
    public Map<String, String> getParameters() {
        return _parameters;
    }

    /**
     * Returns the value for the parameter with the given name.
     * <p>
     * Is case sensitive.
     */
    public String getParameter(String name) {
        if (!parameterExists(name)) {
            throw new RuntimeException("Expected parameter " + name + " in command " + getID());
        }
        return _parameters.get(name);
    }

    /**
     * Returns true if a parameter with the given name exists.
     * <p>
     * Is case sensitive.
     */
    public boolean parameterExists(String name) {
        return _parameters.containsKey(name);
    }

    /**
     * Adds a parameter to this command. 
     * <p>
     * The parameter name is case sensitive and must be unique.
     */
    public Command addParameter(String name, String value) {
        _parameters.put(name, value);
        return this;
    }

    /**
     * Composes a readable string containing the id and all parameters.
     */
    @Override
    public String toString() {
        return String.format("id=%s parameters=%s", _id, _parameters);
    }

    /**
     * Returns true if the unique id and all parameters are equal.
     */
    @Override
    public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;
        if (!(obj instanceof Command))
            return false;

       final Command other = (Command) obj;
       if (other._id == null ? _id != null : !other._id.equals(_id))
           return false;
       if(!other.getParameters().equals(this.getParameters()))
           return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this._id != null ? this._id.hashCode() : 0);
        hash = 71 * hash + (this._parameters != null ? this._parameters.hashCode() : 0);
        return hash;
    }
}
