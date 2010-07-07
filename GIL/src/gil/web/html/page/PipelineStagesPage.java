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
import gil.web.html.INodeableContent;
import gil.web.html.Link;
import gil.web.html.PlainText;
import gil.web.html.Table;
import gil.web.jaxb.PipelineStage;

/**
 * Page that list all available transfer pipeline stages as hyperlinks
 * @author Göran Larsson @ LearningWell AB
 */
public class PipelineStagesPage extends MasterPage {
    
    public PipelineStagesPage(PipelineStage[] stages) {

        Table table = new Table(new INodeableContent[] {new PlainText("SeqNo"), new PlainText("Name"), new PlainText("Link")});
        int seqNo = 0;
        for (PipelineStage stage : stages) {
            table.addRow(new INodeableContent[] {new PlainText(Integer.toString(seqNo++)), new PlainText(stage.getName()),
                new Link(stage.getCommandsLink(), stage.getCommandsLink())});
        }
        Div div = new Div("");
        div.addContent(new Heading("Transfer pipeline stages", Heading.H2)).addContent(table);
        this.setSectionContent("content", div);
    }
}
