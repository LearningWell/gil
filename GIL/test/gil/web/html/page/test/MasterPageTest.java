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
package gil.web.html.page.test;

import org.junit.Test;
import org.w3c.dom.Document;
import gil.common.XMLHelpers;
import gil.web.WebContext;
import static org.junit.Assert.*;
import gil.web.html.page.MasterPage;

public class MasterPageTest {

    public MasterPageTest() {
    }

    @Test
    public void expect_MasterPage_to_be_loaded_from_html_template() throws Exception {
        WebContext.instance().setStaticFilesFolder("files");
        MasterPage p = new MasterPage();


        Document doc = XMLHelpers.createDOMDocument(p.asStream());
        String s = XMLHelpers.DOMTreeToString(doc);
        assertTrue(s.contains("<div id=\"content\">"));
    }
}