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
package gil.web.html.page;

import gil.web.html.Div;
import gil.web.html.Heading;
import gil.web.html.PlainText;
import gil.web.jaxb.StatusJAXB;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class StatusPage extends MasterPage {
    static final String NO_ACTIVITY_STR = "None";

    public StatusPage(StatusJAXB status) {
        String activityS;

        Div div = new Div("status");
        div.addContent(new Heading("System Status", Heading.H2));
       
        div.addContent(new Heading("External System", Heading.H3));
        div.addContent(new Heading("Current status", Heading.H4)).addContent(new PlainText(status.externalSystem.status + " : " + status.externalSystem.statusDescription));
        div.addContent(new Heading("Current operating state", Heading.H4)).addContent(new PlainText(status.externalSystem.operatingState));
        if (status.externalSystem.isActivityDone) {
            activityS = NO_ACTIVITY_STR;
        }
        else {
            activityS = status.externalSystem.currentActivity + "  Completed: " + status.externalSystem.activityCompletion + "%";
        }
        div.addContent(new Heading("Current activity", Heading.H4)).addContent(new PlainText(activityS));


        div.addContent(new Heading("Process Model", Heading.H3));
        div.addContent(new Heading("Current status", Heading.H4)).addContent(new PlainText(status.processModel.status + " : " + status.processModel.statusDescription));
        div.addContent(new Heading("Current operating state", Heading.H4)).addContent(new PlainText(status.processModel.operatingState));

        if (status.processModel.isActivityDone) {
            activityS = NO_ACTIVITY_STR;
        }
        else {
            activityS = status.processModel.currentActivity + "  Completed: " + status.processModel.activityCompletion + "%";
        }        
        div.addContent(new Heading("Current activity", Heading.H4)).addContent(new PlainText(activityS));
        
        this.setSectionContent("content", div);
    }
}
