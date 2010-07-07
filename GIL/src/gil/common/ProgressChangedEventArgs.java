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
 * Provides data for a progressChanged event.
 * @author Göran Larsson @ LearningWell AB
 */
public class ProgressChangedEventArgs {
    
    private final int _progressPercentage;
    private final String _activityDescription;
    private final boolean _isDone;

    
    /**
     * 
     * @param progressPercentage The percentage of an asynchronous task that has been completed. 
     * @param activityDescription A descriptive text of the current activity on the asynchronous task.
     * @param isDone Indicates if the asynchronous task has been finished.
     */
    public ProgressChangedEventArgs(int progressPercentage, String activityDescription, boolean isDone) {
        _activityDescription = activityDescription;
        _progressPercentage = progressPercentage;
        _isDone = isDone;
    }

    /**
     * Determines what percentage (0-100) of an asynchronous task that has been completed. 
     */
    public int progressPercentage() {
        return _progressPercentage;
    }

    /**
     * Gives a descriptive text of the current activity on the asynchronous task
     */
    public String activityDescription() {
        return _activityDescription;
    }

    /**
     * An indication if the asynchronous task has been finished. 
     */
    public boolean isDone() {
        return _isDone;
    }

}
