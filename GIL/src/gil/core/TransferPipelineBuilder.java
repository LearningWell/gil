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
package gil.core;

import gil.io.IPipelineStage;
import gil.common.InvalidFileFormatException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.*;
import gil.common.ConfigFile;
import gil.common.Parameters;

/**
 * The intent of this class is interpret xml data and construct a
 * transfer pipeline dynamically from that xml data.
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class TransferPipelineBuilder {

    private static Logger _logger = Logger.getLogger(TransferPipelineBuilder.class);

    /**
     * Creates a TransferPipeline dynamically from the given config.
     */
    public static TransferPipeline buildPipeline(SignalMetadata[] sm, ConfigFile config) throws ParserConfigurationException,
            IOException, XPathExpressionException, SAXException, InvalidFileFormatException,
            InstantiationException, IllegalAccessException {

        Element elm = config.getSection("transferPipeline");        
        NodeList nodes = elm.getChildNodes();        

        ArrayList<IPipelineStage> stages = new ArrayList<IPipelineStage>();

        int i = 0;
        try
        {            
            for (i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
                    continue;
                NamedNodeMap attr = nodes.item(i).getAttributes();
                Node idNode = attr.getNamedItem("id");
                if (idNode == null) {
                    throw new InvalidFileFormatException("Missing attribute 'id' in pipeline stage configuration " +
                            ". Expected <stage id=\"aStageId\">. Please review " +
                            "the configuration file.");
                }
                String id = idNode.getNodeValue();
                Class cl = Class.forName(nodes.item(i).getTextContent());
                _logger.debug("Adding stage to transfer pipeline: " + cl.getName());
                IPipelineStage stage = (IPipelineStage)cl.newInstance();
                stage.setup(id, SignalMetadata.getSignalsToProcessModel(sm), SignalMetadata.getSignalsToExternalSystem(sm),
                        getPipelieneStageSettings(config, id));
                
                stages.add(stage);
            }
        }
        catch(ClassNotFoundException e)
        {
            throw new InvalidFileFormatException("Can not find a pipeline stage with the given name " +  nodes.item(i).getTextContent()
                    + ". Please review the configuration file");
        }
        catch (ClassCastException e)
        {
            throw new InvalidFileFormatException("The given class name " +  nodes.item(i).getTextContent()
                    + " is not a valid transfer pipeline stage. Please review the configuration file.");
        }        
        return new TransferPipeline(stages);
    }

    /**
     * Returns a parameters object containing the settings for the stage with the given id. If no settings exists, an empty
     * parameters object is returned.
     */
    private static Parameters getPipelieneStageSettings(ConfigFile config, String stageId)
            throws XPathExpressionException, InvalidFileFormatException {

        String settingPath = "pipelineStageSettings/" + stageId;
        if (config.sectionExists(settingPath)) {
            return new Parameters(config.getPropertiesSection(settingPath));
        }
        return new Parameters();
    }

}
