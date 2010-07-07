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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import gil.GIL;
import gil.web.FormatSpecifier;
import gil.web.html.page.StatisticsPage;
import gil.web.jaxb.StatisticsJAXB;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
@Path("statistics{format:(\\..+)?}")
public class StatisticsResource {
    private static final FormatChecker _availableFormats = new FormatChecker(new String[]
        {FormatSpecifier.HTML, "", FormatSpecifier.XML, FormatSpecifier.JSON});

    @GET
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStatistics(@PathParam("format") String format) throws Exception {

        _availableFormats.checkFormatExists(format);

        if (FormatSpecifier.XML.compareToIgnoreCase(format) == 0) {
            return Response.ok(new StatisticsJAXB(GIL.instance().getExecutive().getExternalSystemStatistics(),
                    GIL.instance().getExecutive().getProcessModelStatistics()), MediaType.APPLICATION_XML).build();
        }
        if (FormatSpecifier.JSON.compareToIgnoreCase(format) == 0) {
            return Response.ok(new StatisticsJAXB(GIL.instance().getExecutive().getExternalSystemStatistics(),
                    GIL.instance().getExecutive().getProcessModelStatistics()), MediaType.APPLICATION_JSON).build();
        }       
        StatisticsPage page = new StatisticsPage();
        return Response.ok(page.asStream()).build();
    }
}