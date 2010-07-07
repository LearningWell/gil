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
package gil.web.resources;

import com.sun.jersey.api.NotFoundException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import gil.web.html.page.LogfilesPage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import gil.GIL;
import gil.core.LogfileMetadata;
import gil.web.FormatSpecifier;
import gil.web.jaxb.Transformer;

/**
 * REST resource class for retrieving a list of logfiles as well as downloading individual logfiles.
 * @author Göran Larsson @ LearningWell AB
 */
@Path("/logfiles{format:(\\..+)?}")
public class LogfilesResource {

    private static final FormatChecker _availableFormats = new FormatChecker(new String[]
        {FormatSpecifier.HTML, "", FormatSpecifier.XML, FormatSpecifier.JSON});

    /**
     * Returns a list of available logfiles in either json, xml or html format.
     */
    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})    
    public Response getLogfiles(@PathParam("format") String format, @Context UriInfo uriInfo) throws Exception {
        LogfileMetadata[] md = GIL.instance().getLogfileInfo();
        _availableFormats.checkFormatExists(format);

        if (FormatSpecifier.XML.compareToIgnoreCase(format) == 0) {            
            return Response.ok(Transformer.transformLogfileMetadataArray(md, uriInfo.getAbsolutePath()), MediaType.APPLICATION_XML).build();
        }
        if (FormatSpecifier.JSON.compareToIgnoreCase(format) == 0) {
            return Response.ok(Transformer.transformLogfileMetadataArray(md, uriInfo.getAbsolutePath()), MediaType.APPLICATION_JSON).build();
        }
        LogfilesPage page = new LogfilesPage(md, uriInfo.getAbsolutePath());
        return Response.ok(page.asStream(), MediaType.TEXT_HTML).build();
    }

    /**
     * Returns the requested logfile as binary data stream.
     */
    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getLogfile(@PathParam("name") String name) {
        try {
            InputStream f = GIL.instance().getLogfile(name);
            return Response.ok(f).build();
        } catch (FileNotFoundException ex) {
            throw new NotFoundException("Requested file '" + name + "' does not exist");
        }
    }
}