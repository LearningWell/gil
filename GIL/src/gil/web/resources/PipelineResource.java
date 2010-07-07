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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import gil.GIL;
import gil.core.IntegrationExecutive;
import gil.core.StageValueObject;
import gil.common.Helpers;
import gil.web.FormatSpecifier;
import gil.web.html.page.CommandsPage;
import gil.web.html.page.ControlCommandResultPage;
import gil.web.html.page.PipelineStagesPage;
import gil.web.jaxb.Command;
import gil.web.jaxb.CommandResult;
import gil.web.jaxb.Parameter;
import gil.web.jaxb.Transformer;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
@Path("/pipeline")
public class PipelineResource {

    private static final FormatChecker _availableFormats = new FormatChecker(new String[]
        {FormatSpecifier.HTML, "", FormatSpecifier.XML, FormatSpecifier.JSON});

    @GET
    @Path("/stages{format:(\\..+)?}")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPipelineStages(@PathParam("format") String format, @Context UriInfo uriInfo) {
        StageValueObject[] stages = GIL.instance().getExecutive().getTransferPipelineStages();
        _availableFormats.checkFormatExists(format);

        if (FormatSpecifier.XML.compareToIgnoreCase(format) == 0) {
            return Response.ok(Transformer.transformStageArray(stages, uriInfo.getAbsolutePath()), MediaType.APPLICATION_XML).build();
        }
        if (FormatSpecifier.JSON.compareToIgnoreCase(format) == 0) {
            return Response.ok(Transformer.transformStageArray(stages, uriInfo.getAbsolutePath()), MediaType.APPLICATION_JSON).build();
        }        
        PipelineStagesPage page = new PipelineStagesPage(Transformer.transformStageArray(stages, uriInfo.getAbsolutePath()));
        return Response.ok(page.asStream(), MediaType.TEXT_HTML).build();        
    }

    @GET
    @Path("/stages/{seqNo}/commands{format:(\\..+)?}")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getPipelineStage(@PathParam("format") String format, @PathParam("seqNo") int seqNo,
            @DefaultValue("") @QueryParam("command") String command, @Context UriInfo uriInfo) {
        _availableFormats.checkFormatExists(format);
        
        IntegrationExecutive ie = GIL.instance().getExecutive();
        StageValueObject stage = getStageBySeqNo(ie.getTransferPipelineStages(), seqNo);
        Command cmds[] = Transformer.transformCommandDescriptors(stage.availableCommands());

        if (FormatSpecifier.XML.compareToIgnoreCase(format) == 0) {
            return Response.ok(cmds, MediaType.APPLICATION_XML).build();
        }
        if (FormatSpecifier.JSON.compareToIgnoreCase(format) == 0) {
            return Response.ok(cmds, MediaType.APPLICATION_JSON).build();
        }

        String path = uriInfo.getAbsolutePathBuilder().build().getPath();
        String title = String.format("Pipeline stage #%d: %s", stage.seqNo(), stage.name());
        CommandsPage page = new CommandsPage(title, Helpers.removeFormatSpecifierFromUriPath(path), cmds, command);
        return Response.ok(page.asStream()).build();
    }

    /**
     * Invoke command from HTML form requests
     */
    @POST
    @Path("/stages/{seqNo}/commands/{command}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response invokeCommand(MultivaluedMap<String, String> form,
        @PathParam("seqNo") int seqNo, @PathParam("command") String command) {

        HashMap<String, String> params = new HashMap<String, String>();

        for (Entry<String, List<String>> entry : form.entrySet()) {
            if (entry.getValue().size() > 0) {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        try {
            Map<String, String> result = GIL.instance().getExecutive().invokePipelineStageCommand(seqNo, command, params);
            return Response.ok(new ControlCommandResultPage(result).asStream()).build();

        } catch(Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    /**
     * Invoke command for XML and JSON requests
     */
    @POST
    @Path("/stages/{seqNo}/commands/{command}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response invokeCommand(@PathParam("seqNo") int seqNo, @PathParam("command") String command, Parameter[] params) {
        HashMap<String, String> pmap = new HashMap<String, String>();
        for (Parameter p : params) {
            pmap.put(p.name, p.value);
        }
        try
        {
            Map<String, String> result = GIL.instance().getExecutive().invokePipelineStageCommand(seqNo, command, pmap);
            return Response.ok(new CommandResult(result)).build();

        } catch(Exception ex) {
            throw new WebApplicationException(ex);
        }
    }

    private StageValueObject getStageBySeqNo(StageValueObject[] stages, int seqNo) {
        for (StageValueObject stage : stages) {

            if (seqNo == stage.seqNo()) {
                return stage;
            }
        }
        throw new NotFoundException("Stage with seqNo '" + seqNo + "' does not exists");
    }
}