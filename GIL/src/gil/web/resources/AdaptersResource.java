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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import gil.GIL;
import gil.core.AdapterValueObject;
import gil.core.IntegrationExecutive;
import gil.common.Helpers;
import gil.web.FormatSpecifier;
import gil.web.html.page.CommandsPage;
import gil.web.html.page.ControlCommandResultPage;
import gil.web.jaxb.Command;
import gil.web.jaxb.CommandResult;
import gil.web.jaxb.Parameter;
import gil.web.jaxb.Transformer;

/**
 * @author Göran Larsson @ LearningWell AB
 */
@Path("/adapters")
public class AdaptersResource {
   
    public static final String ES_ADAPTER = "externalsystem";
    public static final String PM_ADAPTER = "processmodel";
    
    private static final FormatChecker _availableFormats = new FormatChecker(new String[] 
        {FormatSpecifier.HTML, "", FormatSpecifier.XML, FormatSpecifier.JSON});

    @GET
    @Path("/{adapterName}/commands{format:(\\..+)?}")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAdapter(@Context UriInfo uriInfo, @PathParam("adapterName") String adapterName, @PathParam("format") String format,
            @DefaultValue("") @QueryParam("command") String command) {

        _availableFormats.checkFormatExists(format);
        
        IntegrationExecutive ie = GIL.instance().getExecutive();
        AdapterValueObject adapter;
        if (adapterName.equalsIgnoreCase(ES_ADAPTER)) {
            adapter = ie.getExternalSystemAdapter();
            
        }
        else if (adapterName.equalsIgnoreCase(PM_ADAPTER)) {
            adapter = ie.getProcessModelAdapter();
        }
        else {
            throw new NotFoundException("The requested adapter '" + adapterName + "' does not exist");                        
        }

        Command[] cmds = Transformer.transformCommandDescriptors(adapter.availableCommands());

        if (FormatSpecifier.XML.compareToIgnoreCase(format) == 0) {
            return Response.ok(cmds, MediaType.APPLICATION_XML).build();
        }
        if (FormatSpecifier.JSON.compareToIgnoreCase(format) == 0) {
            return Response.ok(cmds, MediaType.APPLICATION_JSON).build();
        }
        
        String path = uriInfo.getAbsolutePathBuilder().build().getPath();
        String title = "Adapter: " + adapter.name();
        CommandsPage page = new CommandsPage(title, Helpers.removeFormatSpecifierFromUriPath(path), cmds, command);
        return Response.ok(page.asStream()).build();
    }

    /**
     * Invoke command for HTML form requests
     */
    @POST
    @Path("/{adapterName}/commands/{command}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response invokeCommand(MultivaluedMap<String, String> form,
        @PathParam("adapterName") String adapterName, @PathParam("command") String command) {

        HashMap<String, String> params = new HashMap<String, String>();

        for (Entry<String, List<String>> entry : form.entrySet()) {
            if (entry.getValue().size() > 0) {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        Map<String, String> result = invokeCommandOnAddressedAdapter(adapterName, command, params);
        return Response.ok(new ControlCommandResultPage(result).asStream()).build();
    }

    /**
     * Invoke command for XML and JSON requests
     */
    @POST
    @Path("/{adapterName}/commands/{command}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response invokeCommand(@PathParam("adapterName") String adapterName, @PathParam("command") String command, Parameter[] params) {
        HashMap<String, String> pmap = new HashMap<String, String>();
        for (Parameter p : params) {
            pmap.put(p.name, p.value);
        }
        Map<String, String> result = invokeCommandOnAddressedAdapter(adapterName, command, pmap);        
        return Response.ok(new CommandResult(result)).build();
    }

    private Map<String, String> invokeCommandOnAddressedAdapter(String adapterName, String command, HashMap<String, String> params) {
        try 
        {
            if (adapterName.equals(ES_ADAPTER)) {
                return GIL.instance().getExecutive().invokeExternalSystemCommand(command, params);
            } else if (adapterName.equals(PM_ADAPTER)) {
                return GIL.instance().getExecutive().invokeProcessModelCommand(command, params);
            } else {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity("The requested adapter '" + adapterName + "' does not exist.").build());
            }            
        } catch(Exception ex) {
            throw new WebApplicationException(ex);
        }
    }
}