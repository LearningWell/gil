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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * Parses a xml configuration file with the following format:
 *
 * <configuration>
 *   <appSettings>
 *       <add key="appSetting1" value="value1" />
 *       <add key="appSetting_n" value="value_n" />
 *   </appSettings>
 *   <freeFormatSection1>
 *         <!-- arbritary content -->
 *   </freeFormatSection1>
 *   <propertiesSection1>
 *      <!-- nodes must be in a key-value format -->
 *      <add key="key1" value="value1" />
 *      <add key="key_n" value="value_n" />
 *   </propertiesSection1>
 * </configuration>
 *
 * appSetting is mandatory. Additional free format sections and properties sections are not.
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class ConfigFile {
    
    // The Document object representing the entire xml configuration file.
    Document _xmlDoc;

    // The filename of the configuration file
    String _filename;

    protected ConfigFile(Document xmlDoc, String filename) {
        _xmlDoc = xmlDoc;
        _filename = filename;
    }

    /**
     * Reads the xml configuration file with the given name and constructs a ConfigFile object.
     */
    public static ConfigFile read(String filename) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream is = new FileInputStream(filename);
        try {
            return new ConfigFile(builder.parse(is), filename);
        } finally {
            is.close();
        }        
    }

    /**
     * Returns a Map of settings for the properties section with the given name. An InvalidFileFormatException
     * is thrown if the requested section does not exist or if multiple sections exists with the same name.
     */
    public Map<String, String> getPropertiesSection(String name) throws XPathExpressionException, 
            InvalidFileFormatException {
        
        Map<String, String> props = new HashMap<String, String>();

        Element e = getSection(name);

        XPath path = XPathFactory.newInstance().newXPath();

        NodeList nodes = (NodeList)path.evaluate("add", e, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {            
            if (nodes.item(i) instanceof Element) {
                Element elem = (Element)nodes.item(i);
                props.put(elem.getAttribute("key"), elem.getAttribute("value"));
            }
        }
        return props;
    }

    /**
     * Returns a xml element for the free format section with the given name. Further xml parsing of the returned
     * element is done by the caller. A InvalidFileFormatException is thrown if the requested section does not exist
     * or the section exists more than once.
     */
    public Element getSection(String name) throws XPathExpressionException, InvalidFileFormatException {
        XPath path = XPathFactory.newInstance().newXPath();

        NodeList nodes = (NodeList)path.evaluate("/configuration/" + name, _xmlDoc, XPathConstants.NODESET);

        if (nodes.getLength() > 1) {
            throw new InvalidFileFormatException("Expected just a single section 'configuration/" + name + "' in " + _filename);
        }
        if (nodes.getLength() > 0) {
            if (nodes.item(0) instanceof Element) {
                return (Element)nodes.item(0);
            }
        }
        throw new InvalidFileFormatException("Missing section 'configuration/" + name + "' in " + _filename);
    }

    /**
     * Returns the setting with the given name in the mandatory "appSettings" section. If a setting with the name
     * does not exist a InvalidFileFormatException is thrown.
     */
    public String getAppSetting(String name) throws XPathExpressionException, InvalidFileFormatException {

        Map<String, String> props = getPropertiesSection("appSettings");
        if (!props.containsKey(name)) {
            throw new InvalidFileFormatException("Missing appSetting '" + name + "' in " + _filename);
        }
        return props.get(name);
    }

    public boolean sectionExists(String name) throws XPathExpressionException {
        XPath path = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList)path.evaluate("/configuration/" + name, _xmlDoc, XPathConstants.NODESET);

        if (nodes.getLength() >= 1) {
            if (nodes.item(0) instanceof Element) {
                return true;
            }
        }
        return false;
    }
}
