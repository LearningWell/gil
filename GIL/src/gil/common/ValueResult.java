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

/**
 * Encapsulates the object given by the generic type together with a boolean result code for success or failure.
 * In the latter case, probably also a description of the cause of failure.
 * @author Göran Larsson @ LearningWell AB
 */
public class ValueResult<T> extends Result {

    private T _returnValue = null;

    /**
     * Constructs a ValueResult object indicating success.
     * @param returnValue The ecapsulated value object.
     */
    public ValueResult(T returnValue) {
        super(true);
        _returnValue = returnValue;
    }

    
    /**
     * Constructs a ValueResult object indicating failure.
     * <p>
     * @param errorDescription The cause of failure.
     */
    public ValueResult(String errorDescription) {
        super(false, errorDescription);
    }

    /**
     * Returns the encapsulated value object.
     * @return The actual object expected as return value. This call is only valid if success is indicated
     * by this object.
     */
    public T getReturnValue() {
        return _returnValue;
    }

    /**
     * Encapsulates the given value object.
     * @param returnValue The value object to be encapsulated.
     */
    public void setReturnValue(T returnValue) {
        _returnValue = returnValue;
    }
}
