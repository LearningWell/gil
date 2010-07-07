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

import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import gil.common.XMLHelpers;
import gil.web.html.Div;
import gil.web.html.Heading;

public class DivTest {

    public DivTest() {
    }

    @Test
    public void expect_div_element_with_content_to_be_created() throws Exception {
        Div div = new Div("anID");

        div.setContent(new Heading("test1", Heading.H1));

        Document doc = XMLHelpers.createDOMDocument();
        Node divNode = div.asNode(doc);
        doc.appendChild(divNode);
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<div id=\"anID\">" +
                        "<h1>test1</h1>" +
                     "</div>", s);
    }
}