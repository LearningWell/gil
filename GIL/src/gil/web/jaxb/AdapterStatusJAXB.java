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
package gil.web.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class for adapter status information. Used to represent status information for the external system adapter as
 * well as the process model adapter.
 * @author Göran Larsson @ LearningWell AB
 */
@XmlRootElement
public class AdapterStatusJAXB {

    /**
     * The status discriping the current status.
     */
    public String status;

    /**
     * A arbritrary description of the current status, presumably the cause.
     */
    public String statusDescription;

    /**
     * Describes the current simulator operating state.
     */
    public String operatingState;

    /**
     * true if the there is no activity in-progress.
     */
    public boolean isActivityDone;

    /**
     * A descriptive text of the current activity in-progress.
     */
    public String currentActivity;

    /**
     * The current percentage of completion for the activity in progress. If {@link #isActivityDone isActivityDone} is
     * true the value is undefined.
     */
    public int activityCompletion;

    public AdapterStatusJAXB() { }
    
    public AdapterStatusJAXB(String status, String statusDescription, String operatingState, boolean activityDone, String currentActivity, int activityCompletion) {
        this.status = status;
        this.statusDescription = statusDescription;
        this.operatingState = operatingState;
        this.isActivityDone = activityDone;
        this.currentActivity = currentActivity;
        this.activityCompletion = activityCompletion;
    }
}
