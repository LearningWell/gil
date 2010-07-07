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
package gil.web.html.test;

import gil.web.html.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import gil.common.XMLHelpers;
import static org.junit.Assert.*;

public class TableTest {

    public TableTest() {
    }

    @Test
    public void expect_table_to_be_created() throws Exception {
        INodeableContent[] headers = {new PlainText("Col1"), new PlainText("Col2")};

        Table t = new Table(headers);
        t.addRow(new INodeableContent[] {new PlainText("val11"), new PlainText("val12")});
        t.addRow(new INodeableContent[] {new PlainText("val21"), new PlainText("val22")});

        Document doc = XMLHelpers.createDOMDocument();
        Node tableNode = t.asNode(doc);
        doc.appendChild(tableNode);
        String s = XMLHelpers.DOMTreeToString(doc);
        assertTrue(s.contains("<table>"));
        assertTrue(s.contains("</table>"));
        assertTrue(s.contains("<th>Col1</th>"));
        assertTrue(s.contains("<th>Col2</th>"));
        assertTrue(s.contains("<td>val11</td>"));
        assertTrue(s.contains("<td>val22</td>"));
    }
}