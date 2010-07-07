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
package gil.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 * Generic helpers for XML DOM processing.
 * @author Göran Larsson @ LearningWell AB
 */
public class XMLHelpers {
    
    public static String DOMTreeToString(Document doc) throws Exception {

        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "no");
        DOMSource source = new DOMSource(doc);

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);        
        trans.transform(source, result);
        return sw.toString();
    }
    
    public static InputStream DOMTreeToStream(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        transformer.transform(source, result);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static Document createDOMDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    public static Document createDOMDocument(InputStream stream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(stream);
    }

    public static String removeDTDDocType(String xml) {       

        String ux = xml.toUpperCase();
        int startIndex = ux.indexOf("DOCTYPE");
        do {
            if (ux.charAt(startIndex) == '<')
                break;
        } while (--startIndex > -1);

        if (startIndex == -1 || (startIndex + 9 >= xml.length()))
            return xml;

        int startCount = 1;
        for (int i = startIndex + 9; i < xml.length(); i++) {
            if (xml.charAt(i) == '<')
                ++startCount;
            else if (xml.charAt(i) == '>') {
                if (--startCount == 0) {
                    StringBuilder sb = new StringBuilder(xml.length());
                    sb.append(xml.substring(0, startIndex));
                    sb.append(xml.substring(i + 1, xml.length()));
                    return sb.toString();
                }
            }                        
        }
        return xml;
    }
}
