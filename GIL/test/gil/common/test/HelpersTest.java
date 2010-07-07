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

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Test;
import static org.junit.Assert.*;
import gil.common.Helpers;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class HelpersTest {

    public HelpersTest() {
    }

    @Test
    public void expect_xml_to_be_removed_from_path_when_removeFormatSpecifierFromUriPath_is_called() {
        String path = "/resources/loggfiles.xml/";
        String result = Helpers.removeFormatSpecifierFromUriPath(path);
        assertEquals("/resources/loggfiles/", result);
    }

    @Test
    public void expect_json_to_be_removed_from_path_when_removeFormatSpecifierFromUriPath_is_called() {
        String path = "/resources/loggfiles.json/test.bin";
        String result = Helpers.removeFormatSpecifierFromUriPath(path);
        assertEquals("/resources/loggfiles/test.bin", result);
    }

    @Test
    public void expect_html_to_be_removed_from_path_when_removeFormatSpecifierFromUriPath_is_called() {
        String path = "/resources/loggfiles.html/test.bin";
        String result = Helpers.removeFormatSpecifierFromUriPath(path);
        assertEquals("/resources/loggfiles/test.bin", result);
    }
    
    @Test
    public void expect_multiple_format_specifiers_to_be_removed_from_path_when_removeFormatSpecifierFromUriPath_is_called() {
        String path = "/resources/loggfiles.xml/second.xml/";
        String result = Helpers.removeFormatSpecifierFromUriPath(path);
        assertEquals("/resources/loggfiles/second/", result);
    }

    @Test
    public void expect_format_specifiers_to_be_removedfrom_uri_when_removeFormatSpecifierFromUri_is_called() 
            throws URISyntaxException {

        URI uri = new URI("http://www.ksu.se:9980/resources/loggfiles.xml");
        assertEquals("http://www.ksu.se:9980/resources/loggfiles", Helpers.removeFormatSpecifierFromUri(uri).toString());
    }

    @Test
    public void expect_format_specifiers_to_be_retrieved_when_retrieveFormatSpecifierFromUri_is_called()
            throws URISyntaxException {

        URI uri = new URI("http://www.ksu.se:9980/resources/loggfiles.xml");
        assertEquals(".xml", Helpers.retrieveFormatSpecifierFromUri(uri));
    }

    @Test
    public void expect_empty_format_specifiers_to_be_retrieved_when_retrieveFormatSpecifierFromUri_is_called()
            throws URISyntaxException {

        URI uri = new URI("http://www.ksu.se:9980/resources/loggfiles");
        assertEquals("", Helpers.retrieveFormatSpecifierFromUri(uri));
    }
}