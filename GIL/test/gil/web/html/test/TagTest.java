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
import gil.common.XMLHelpers;
import gil.web.html.PlainText;
import gil.web.html.Tag;

public class TagTest {

    public TagTest() {
    }

    @Test
    public void expect_html_with_given_tag_to_be_created() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr").asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr/>", s);
    }

    @Test
    public void expect_html_with_given_tag_and_id_to_be_created() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr", "anID").asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr id=\"anID\"/>", s);
    }

    @Test
    public void expect_html_with_given_tag_id_and_class_to_be_created() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr", "anID", "aClass").asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr class=\"aClass\" id=\"anID\"/>", s);
    }

    @Test
    public void expect_html_with_given_tag_and_content_to_be_created() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr").setContent(new PlainText("some content")).asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr>some content</hr>", s);
    }

    @Test
    public void expect_existing_content_to_be_cleared_when_setContent_is_called() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr").setContent(new PlainText("some content")).setContent(new PlainText("new content")).asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr>new content</hr>", s);
    }

    @Test
    public void expect_content_to_be_appended_when_addContent_is_called() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr").setContent(new PlainText("some content ")).addContent(new PlainText("new content")).asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr>some content new content</hr>", s);
    }

    @Test
    public void expect_html_with_attributes_to_be_created() throws Exception {
        Document doc = XMLHelpers.createDOMDocument();
        doc.appendChild(new Tag("hr").addAttribute("attr1", "value1").asNode(doc));
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<hr attr1=\"value1\"/>", s);
    }
}