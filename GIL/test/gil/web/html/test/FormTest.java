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
import gil.web.html.forms.*;

public class FormTest {

    public FormTest() {
    }

    @Test
    public void expect_form_with_single_input_to_be_created() throws Exception {

        Form form = new Form("/test", Form.Method.post);
        form.addContent((Input)new Input(Input.Type.text, "1").addAttribute("name", "aName"));

        Document doc = XMLHelpers.createDOMDocument();
        Node formNode = form.asNode(doc);
        doc.appendChild(formNode);
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<form action=\"/test\" method=\"post\">" +
                        "<input id=\"1\" name=\"aName\" type=\"text\"/>" +
                     "</form>", s);
    }

    @Test
    public void expect_form_with_two_inputs_to_be_created() throws Exception {

        Form form = new Form("/test", Form.Method.post);
        form.addContent((Input)new Input(Input.Type.text, "1").addAttribute("name", "aName"));
        form.addContent((Input)new Input(Input.Type.submit, "2").addAttribute("value", "send"));

        Document doc = XMLHelpers.createDOMDocument();
        Node formNode = form.asNode(doc);
        doc.appendChild(formNode);
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<form action=\"/test\" method=\"post\">" +
                        "<input id=\"1\" name=\"aName\" type=\"text\"/>" +
                        "<input id=\"2\" type=\"submit\" value=\"send\"/>" +
                     "</form>", s);
    }
    @Test
    public void expect_form_with_label_and_input_to_be_created() throws Exception {

        Form form = new Form("/test", Form.Method.post);
        form.addContent(new Label("NAME:", "1"));
        form.addContent((Input)new Input(Input.Type.text, "1").addAttribute("name", "aName"));
        
        Document doc = XMLHelpers.createDOMDocument();
        Node formNode = form.asNode(doc);
        doc.appendChild(formNode);
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<form action=\"/test\" method=\"post\">" +
                        "<label for=\"1\">NAME:</label>" +
                        "<input id=\"1\" name=\"aName\" type=\"text\"/>" +
                    "</form>", s);
    }

    @Test
    public void expect_form_with_Select_element_to_be_created() throws Exception {
        
        Form form = new Form("/test", Form.Method.post);
        Select select = (Select)new Select("1").addAttribute("name", "aName");
        select.addOption("value1", "first", false).addOption("value2", "second", true);
        form.addContent(select);

        Document doc = XMLHelpers.createDOMDocument();
        Node formNode = form.asNode(doc);
        doc.appendChild(formNode);
        String s = XMLHelpers.DOMTreeToString(doc);
        assertEquals("<form action=\"/test\" method=\"post\">" +
                        "<select id=\"1\" name=\"aName\">" +
                            "<option value=\"value1\">first</option>" +
                            "<option selected=\"\" value=\"value2\">second</option>" +
                        "</select>" +
                     "</form>", s);
    }
}