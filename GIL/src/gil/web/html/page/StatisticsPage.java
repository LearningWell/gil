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

import gil.GIL;
import gil.core.Statistics;
import gil.web.html.Div;
import gil.web.html.Heading;
import gil.web.html.PlainText;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class StatisticsPage extends MasterPage {
    
    public StatisticsPage() {
        
        Div div = new Div("statistics");        
        div.addContent(new Heading("System Statistics", Heading.H2));
        
        addStatSection(div, GIL.instance().getExecutive().getExternalSystemStatistics(), "External system statistics");
        addStatSection(div, GIL.instance().getExecutive().getProcessModelStatistics(), "Process model statistics");

        this.setSectionContent("content", div);
    }

    private void addStatSection(Div div, Statistics s, String header) {
        div.addContent(new Heading(header, Heading.H3));
        div.addContent(new Heading("Dropped frames", Heading.H4)).addContent(new PlainText(Integer.toString(s.droppedFrames)));
        div.addContent(new Heading("Data write failures", Heading.H4)).addContent(new PlainText(Integer.toString(s.dataWriteFailureCount)));
        div.addContent(new Heading("Data read failures", Heading.H4)).addContent(new PlainText(Integer.toString(s.dataReadFailureCount)));
        div.addContent(new Heading("Command failures", Heading.H4)).addContent(new PlainText(Integer.toString(s.commandFailureCount)));
        div.addContent(new Heading("Successful frames read", Heading.H4)).addContent(new PlainText(Integer.toString(s.dataReadCount)));
        div.addContent(new Heading("Successful frames written", Heading.H4)).addContent(new PlainText(Integer.toString(s.dataWriteCount)));
    }
}
