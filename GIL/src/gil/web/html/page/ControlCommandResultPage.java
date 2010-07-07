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

import java.util.Map;
import java.util.Map.Entry;
import gil.web.html.Div;
import gil.web.html.Heading;
import gil.web.html.INodeableContent;
import gil.web.html.PlainText;
import gil.web.html.Table;

/**
 * Html page viewing result parameters in a table.
 * @author Göran Larsson @ LearningWell AB
 */
public class ControlCommandResultPage extends MasterPage {

    public ControlCommandResultPage(Map<String, String> results) {
        
        Table table = new Table(new INodeableContent[] {new PlainText("Parameter"), new PlainText("Value")});
        for (Entry<String, String> e : results.entrySet()) {
            table.addRow(new INodeableContent[] {new PlainText(e.getKey()), new PlainText(e.getValue())});
        }
        
        Div div = new Div("");
        div.addContent(new Heading("Result", Heading.H2));

        if (results.size() > 0)
            div.addContent(table);
        else
            div.addContent(new PlainText("No result parameters returned."));

        this.setSectionContent("content", div);
    }
}
