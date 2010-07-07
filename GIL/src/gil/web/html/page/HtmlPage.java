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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import gil.common.XMLHelpers;
import gil.web.html.INodeableContent;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class HtmlPage {

    private Document _document;

    protected HtmlPage() {}
    
    protected HtmlPage(Document document) {
        _document = document;
    }
    
    public String uniqueID() {
        return UUID.randomUUID().toString();
    }

    public void load(InputStream htmlStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            _document = builder.parse(htmlStream);
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    public static HtmlPage createFromHtml(InputStream htmlStream) {
        HtmlPage page = new HtmlPage();
        page.load(htmlStream);
        return page;
    }

    public Node loadHtml(InputStream htmlStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Node n = builder.parse(htmlStream);
            if (n.getNodeType() == Node.DOCUMENT_NODE) {
                n = ((Document) n).getDocumentElement();
            }
            _document.adoptNode(n);
            return n;
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    public Node loadHtml(String html) {
        ByteArrayInputStream stream;
        try {
            stream = new ByteArrayInputStream(html.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new WebApplicationException(ex);
        }
        return loadHtml(stream);
    }

    public InputStream asStream() 
    {
        try {
            return XMLHelpers.DOMTreeToStream(_document);
        } catch (Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Replaces the inner html of the &lt;div&gt; with the given id.
     * @param sectionID The id for the &lt;div&gt i.e. &lt;div id="sectionID"&gt;.
     * @param newContent The new content to be set as the inner html of the section.
     */
    public void setSectionContent(String sectionID, Node content)
    {
        XPath path = XPathFactory.newInstance().newXPath();

        NodeList nodes = null;
        try {
            nodes = (NodeList) path.evaluate("//div[@id='"+ sectionID + "']", _document, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            throw new WebApplicationException(ex);
        }

        if (nodes.getLength() < 1) {
            throw new IllegalArgumentException(
                    String.format("<div> tag with id=%s is not present in the html content.", sectionID));
        }        
        NodeList sectionChilds = nodes.item(0).getChildNodes();

        for (int i = 0; i < sectionChilds.getLength(); i++) {
            removeAll(sectionChilds.item(i), Node.ELEMENT_NODE, null);
        }        
        nodes.item(0).appendChild(content);
    }

    /**
     * Replaces the inner html of the &lt;div&gt; with the given id.
     * @param sectionID The id for the &lt;div&gt i.e. &lt;div id="sectionID"&gt;.
     * @param newContent The new content to be set as the inner html of the section.
     */
    public void setSectionContent(String sectionID, INodeableContent newContent) 
    {
        this.setSectionContent(sectionID, newContent.asNode(_document));
    }

    private static void removeAll(Node node, short nodeType, String name) {
        if (node.getNodeType() == nodeType && (name == null || node.getNodeName().equals(name))) {
            node.getParentNode().removeChild(node);
        } else {
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                removeAll(list.item(i), nodeType, name);
            }
        }
    }
}
