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
package gil.core.test;

import gil.core.TransferPipeline;
import gil.core.TransferPipelineBuilder;
import gil.common.InvalidFileFormatException;
import org.junit.*;
import java.io.PrintWriter;
import java.io.File;
import gil.common.ConfigFile;
import gil.core.SignalMetadata;
import gil.io.IPipelineStage;
import static org.junit.Assert.*;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class TransferPipelineBuilderTest {

    static final String XML_TEST_DATA_FILE_NAME = "testdataXML.xml";
    
    public TransferPipelineBuilderTest() {
    }

    @After
    public void tearDown() {
        File file = new File(XML_TEST_DATA_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void expect_transfer_pipeline_to_be_created() throws Exception {

        String xml = "<?xml version=\"1.0\"?>"
          +          "<configuration>"
          +            "<transferPipeline>"
          +              "<stage id=\"stage1\">gil.core.test.AddStageStub</stage>"
          +              "<stage id=\"stage2\">gil.core.test.MultiplyStageStub</stage>"
          +            "</transferPipeline>"
          +          "</configuration>";

        PrintWriter out = new PrintWriter(XML_TEST_DATA_FILE_NAME);
        out.print(xml);
        out.close();
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        SignalMetadata[] smd = new SignalMetadata[0];

        TransferPipeline pipeline = TransferPipelineBuilder.buildPipeline(smd, config);

        assertTrue(pipeline.getStages().get(0) instanceof AddStageStub);
        assertEquals("stage1", pipeline.getStages().get(0).getId());
        assertTrue(pipeline.getStages().get(1) instanceof MultiplyStageStub);
        assertEquals("stage2", pipeline.getStages().get(1).getId());
    }

    @Test (expected = InvalidFileFormatException.class)
    public void expect_exception_when_illegal_pipeline_stage_is_added() throws Exception {

        String xml = "<?xml version=\"1.0\"?>"
          +          "<configuration>"
          +            "<transferPipeline>"
          +              "<stage id=\"stage1\">gil.core.test.AddStageStub</stage>"
          +              "<stage id=\"stage2\">None_existing</stage>"
          +            "</transferPipeline>"
          +          "</configuration>";

        PrintWriter out = new PrintWriter(XML_TEST_DATA_FILE_NAME);
        out.print(xml);
        out.close();

        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);
        SignalMetadata[] smd = new SignalMetadata[0];

        TransferPipeline pipeline = TransferPipelineBuilder.buildPipeline(smd, config);
        assertTrue(pipeline.getStages().get(0) instanceof AddStageStub);
        assertTrue(pipeline.getStages().get(1) instanceof MultiplyStageStub);

        File file = new File(XML_TEST_DATA_FILE_NAME);
        file.delete();
    }

    @Test
    public void expect_stages_to_be_configured() throws Exception {
        SignalMetadata[] metadata = new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                SignalMetadata.DataflowDirection.ToPM, null, null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                SignalMetadata.DataflowDirection.ToES, null, null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                SignalMetadata.DataflowDirection.ToPM, null, null, null, null)
        };
        
        String xml = "<?xml version=\"1.0\"?>"
          +          "<configuration>"
          +            "<transferPipeline>"
          +              "<stage id=\"stage1\">gil.core.test.AddStageStub</stage>"
          +              "<stage id=\"stage2\">gil.core.test.MultiplyStageStub</stage>"
          +            "</transferPipeline>"
          +            "<pipelineStageSettings>"
          +              "<stage1>"
          +                  "<add key=\"param1\" value=\"value1\" />"
          +                  "<add key=\"param2\" value=\"value2\" />"
          +              "</stage1>"
          +              "<stage2>"
          +                  "<add key=\"param21\" value=\"value21\" />"
          +              "</stage2>"
          +            "</pipelineStageSettings>"
          +          "</configuration>";

        PrintWriter out = new PrintWriter(XML_TEST_DATA_FILE_NAME);
        out.print(xml);
        out.close();
        ConfigFile config = ConfigFile.read(XML_TEST_DATA_FILE_NAME);

        TransferPipeline pipeline = TransferPipelineBuilder.buildPipeline(metadata, config);

        assertEquals(1, pipeline.getStages().get(0).getSignalsToES().length);
        assertEquals(2, pipeline.getStages().get(0).getSignalsToPM().length);
        assertEquals(2, pipeline.getStages().get(0).getConfig().getParameters().size());
        assertEquals("value2", pipeline.getStages().get(0).getConfig().getParam("param2"));
        assertEquals(1, pipeline.getStages().get(1).getConfig().getParameters().size());
        assertEquals("value21", pipeline.getStages().get(1).getConfig().getParam("param21"));
    }
}