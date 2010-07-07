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
 * Encapsulates a success or failure indication. In the case of failure it also contains text describing the cause
 * of the failure.
 * @author Göran Larsson @ LearningWell AB
 */
public class Result {

    private boolean _success;
    private String _errorDescription = "";

    /**
     * Constructs a Result object with an error decription.
     */
    public Result(boolean isSuccess, String errorDescription) {
        _success = false;
        _errorDescription = errorDescription;
    }

    /**
     * Constructs a Result object. Mainly used for success. Use {@link #Result(boolean, java.lang.String)} for failures
     * to be able to give an error description.
     * @param isSuccess
     */
    public Result(boolean isSuccess) {
        _success = isSuccess;
    }

    /**
     * Returns true if this result object indicates success. False is returned on failures and the cause 
     * can then found out by calling {@link #getErrorDescription()}.
     */
    public boolean isSuccess() {
        return _success;
    }

    /**
     * Sets the success flag to either true or false.
     */
    public void setSuccess(boolean success) {
        _success = success;
    }

    /**
     * Returns the cause of failure when {@link #isSuccess()} returns false.
     * <p>
     * An empty string is returned as default.
     */
    public String getErrorDescription() {
        return _errorDescription;
    }

    /**
     * Sets the cause of failure.
     */
    public void setErrorDescription(String errorDescription) {
        _errorDescription = errorDescription;
    }
}
