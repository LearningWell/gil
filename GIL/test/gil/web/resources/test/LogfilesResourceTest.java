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
package gil.web.resources.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.test.framework.JerseyTest;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import gil.GIL;
import gil.IGIL;
import gil.core.LogfileMetadata;
import gil.web.WebContext;
import gil.web.jaxb.Logfile;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LogfilesResourceTest extends JerseyTest {
        
    public LogfilesResourceTest()throws Exception {
        super("gil.web.resources");
        WebContext.instance().setStaticFilesFolder("files");
    }

    @Test
    public void expect_logfiles_metadata_to_be_returned_as_xml() {
        IGIL gilMock = mock(IGIL.class);
        GIL.injectInstance(gilMock);

        when(gilMock.getLogfileInfo()).thenReturn(new LogfileMetadata[]
                {new LogfileMetadata("n1", new Date(), 100), new LogfileMetadata("n2", new Date(), 200)});

        WebResource webResource = resource();
        Logfile[] lfs = webResource.path("/logfiles.xml").accept(MediaType.APPLICATION_XML).get(Logfile[].class);
        assertEquals(2, lfs.length);
        assertEquals(100, lfs[0].sizeInBytes);
        assertEquals(200, lfs[1].sizeInBytes);
    }

    @Test
    public void expect_logfiles_metadata_to_be_returned_as_json() {
        IGIL gilMock = mock(IGIL.class);
        GIL.injectInstance(gilMock);

        when(gilMock.getLogfileInfo()).thenReturn(new LogfileMetadata[]
                {new LogfileMetadata("n1", new Date(), 100), new LogfileMetadata("n2", new Date(), 200)});

        WebResource webResource = resource();
        Logfile[] lfs = webResource.path("/logfiles.json").accept(MediaType.APPLICATION_JSON).get(Logfile[].class);
        assertEquals(2, lfs.length);
        assertEquals(100, lfs[0].sizeInBytes);
        assertEquals(200, lfs[1].sizeInBytes);
    }

    @Test
    public void expect_logfiles_metadata_to_be_returned_as_html() {
        IGIL gilMock = mock(IGIL.class);
        GIL.injectInstance(gilMock);

        when(gilMock.getLogfileInfo()).thenReturn(new LogfileMetadata[]
                {new LogfileMetadata("n1", new Date(), 100), new LogfileMetadata("n2", new Date(), 200)});

        WebResource webResource = resource();
        String s = webResource.path("/logfiles.html").accept(MediaType.TEXT_HTML).get(String.class);
        assertTrue(s.contains("html"));
        assertTrue(s.contains("body"));
        assertTrue(s.contains("table"));
    }

    @Test
    public void expect_404_when_unavailable_format_is_requested() {
        IGIL gilMock = mock(IGIL.class);
        GIL.injectInstance(gilMock);

        when(gilMock.getLogfileInfo()).thenReturn(new LogfileMetadata[]
                {new LogfileMetadata("n1", new Date(), 100), new LogfileMetadata("n2", new Date(), 200)});

        WebResource r = resource();
        ClientResponse resp = r.path("/logfiles.dummy").get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    @Test
    public void expect_file_data_when_file_is_downloaded() throws Exception {
        IGIL gilMock = mock(IGIL.class);
        GIL.injectInstance(gilMock);

        when(gilMock.getLogfile("test.log")).thenReturn(new ByteArrayInputStream(new byte[100]));

        WebResource r = resource();
        byte[] resp = r.path("/logfiles/test.log").get(byte[].class);
        assertEquals(100, resp.length);
    }

    @Test
    public void expect_404_when_file_is_unavailable_when_trying_to_download() throws Exception {
        IGIL gilMock = mock(IGIL.class);
        GIL.injectInstance(gilMock);

        when(gilMock.getLogfile("test.log")).thenThrow(new FileNotFoundException());

        WebResource r = resource();
        ClientResponse resp = r.path("/logfiles/test.log").get(ClientResponse.class);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    @Test
    public void expect_wadl_to_be_available() {
        WebResource webResource = resource();
        String serviceWadl = webResource.path("application.wadl").
                accept(MediaTypes.WADL).get(String.class);

        assertTrue(serviceWadl.length() > 0);
    } 
}