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
package gil.common.test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import org.junit.*;
import gil.common.ConfigFile;
import gil.common.InvalidFileFormatException;
import static org.junit.Assert.*;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class ConfigFileTest {

    static final String XML_TEST_DATA_FILE_NAME = "testdataXML.xml";
    
    @Before
    public void setUp() throws Exception {

        String xml = "<?xml version=\"1.0\"?>"
          +          "<configuration>"
          +            "<appSettings>"
          +              "<add key=\"testSetting\" value=\"testValue\" />"
          +            "</appSettings>"
          +            "<transferPipeline>"
          +              "<stage>gil.core.test.AddStageStub </stage>"
          +              "<stage>gil.core.test.MultiplyStageStub </stage>"
          +            "</transferPipeline>"
          +            "<propertySection1>"
          +              "<add key=\"prop1\" value=\"value1\" />"
          +              "<add key=\"prop2\" value=\"value2\" />"
          +            "</propertySection1>"
          +          "</configuration>";

        PrintWriter out = new PrintWriter(XML_TEST_DATA_FILE_NAME);
        out.print(xml);
        out.close();
    }

    @After
    public void tearDown() {
        File file = new File(XML_TEST_DATA_FILE_NAME);
        file.delete();
    }

    @Test
    public void expect_property_section_to_be_parsed_and_returned_as_Map() throws Exception {
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        Map<String, String> props = config.getPropertiesSection("propertySection1");

        assertEquals("value1", props.get("prop1"));
        assertEquals("value2", props.get("prop2"));
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_requesting_non_existing_property_section() throws Exception {
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        config.getPropertiesSection("nonExisting");
    }

    @Test
    public void expect_single_appSetting_to_be_parsed_and_returned_as_String() throws Exception {
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        assertEquals("testValue", config.getAppSetting("testSetting"));
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_requesting_non_existing_appSetting() throws Exception {
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        config.getAppSetting("nonExisting");
    }

    @Test
    public void expect_true_on_sectionExists_query_when_section_exists() throws Exception {
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        assertTrue(config.sectionExists("propertySection1"));
    }

    @Test
    public void expect_false_on_sectionExists_query_when_section_missing() throws Exception {
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        assertFalse(config.sectionExists("nonExisting"));
    }

}