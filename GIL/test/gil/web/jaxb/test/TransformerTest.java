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
package gil.web.jaxb.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;
import org.junit.Test;
import gil.core.LogfileMetadata;
import static org.junit.Assert.*;
import gil.core.CommandDescriptor;
import gil.core.ParameterDescriptor;
import gil.core.SoftwareInfo;
import gil.core.StageValueObject;
import gil.web.jaxb.AboutJAXB;
import gil.web.jaxb.Command;
import gil.web.jaxb.Logfile;
import gil.web.jaxb.PipelineStage;
import gil.web.jaxb.Transformer;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class TransformerTest {

    public TransformerTest() {
    }

    @Test
    public void expect_CommandDescriptors_to_be_transformed_to_CommandJAXBs() {
        
        CommandDescriptor[] cds = new CommandDescriptor[] {
            new CommandDescriptor("cmd1", "cmd1Doc", new ParameterDescriptor[]
                { new ParameterDescriptor("param1_1", "paramDoc1_1", true),
                  new ParameterDescriptor("param1_2", "paramDoc1_2", false)
                }),
            new CommandDescriptor("cmd2", "cmd2Doc", new ParameterDescriptor[]
                { new ParameterDescriptor("param2_1", "paramDoc2_1", true),
                  new ParameterDescriptor("param2_2", "paramDoc2_2", false)
                })
        };

        Command[] result = Transformer.transformCommandDescriptors(cds);
        assertEquals(cds.length, result.length);
        assertEquals(cds[0].getCommandID(), result[0].getName());
        assertEquals(cds[0].getDocumentation(), result[0].getDocumentation());
        assertEquals(cds[0].getParameters().length, result[0].getParameters().getParameter().size());
        assertEquals(cds[0].getParameters()[0].getName(), result[0].getParameters().getParameter().get(0).getName());
        assertEquals(cds[0].getParameters()[0].getDocumentation(), result[0].getParameters().getParameter().get(0).getDocumentation());
        assertEquals(cds[0].getParameters()[0].mandatory(), result[0].getParameters().getParameter().get(0).isMandatory());
        assertEquals(cds[1].getCommandID(), result[1].getName());
        assertEquals(cds[1].getDocumentation(), result[1].getDocumentation());
        assertEquals(cds[1].getParameters().length, result[1].getParameters().getParameter().size());
        assertEquals(cds[1].getParameters()[1].getName(), result[1].getParameters().getParameter().get(1).getName());
        assertEquals(cds[1].getParameters()[1].getDocumentation(), result[1].getParameters().getParameter().get(1).getDocumentation());
        assertEquals(cds[1].getParameters()[1].mandatory(), result[1].getParameters().getParameter().get(1).isMandatory());
    }
    
    @Test
    public void expect_LogfileMetadata_array_to_be_transformed_to_jaxb_Logfile_array() throws URISyntaxException {

        LogfileMetadata[] mds = new LogfileMetadata[] {
            new LogfileMetadata("n1", new GregorianCalendar(2000, 5, 23).getTime(), 100),
            new LogfileMetadata("n2", new GregorianCalendar(2001, 6, 24).getTime(), 50)
        };
        Logfile[] lf = Transformer.transformLogfileMetadataArray(mds, new URI("http://localhost:1234/base"));
        assertEquals(2, lf.length);
        assertEquals(mds[0].getLastModified(), lf[0].lastModified);
        assertEquals("http://localhost:1234/base/n1", lf[0].name);
        assertEquals(mds[0].getSizeInBytes(), lf[0].sizeInBytes);
        assertEquals(mds[1].getLastModified(), lf[1].lastModified);
        assertEquals("http://localhost:1234/base/n2", lf[1].name);
        assertEquals(mds[1].getSizeInBytes(), lf[1].sizeInBytes);
    }

    @Test
    public void expect_stage_array_to_be_transformed_to_jaxb_stage_array() throws URISyntaxException {

        StageValueObject[] svos = new StageValueObject[] {
            new StageValueObject(2, "aName", null),
            new StageValueObject(3, "aName3", null)
        };
        PipelineStage[] stages = Transformer.transformStageArray(svos, new URI("http://localhost:1234/base"));
        assertEquals(2, stages.length);
        assertEquals(svos[0].name(), stages[0].getName());
        assertEquals(svos[1].name(), stages[1].getName());
        assertEquals("http://localhost:1234/base/2/commands", stages[0].getCommandsLink());
        assertEquals("http://localhost:1234/base/3/commands", stages[1].getCommandsLink());
    }

    @Test
    public void expect_version_info_to_be_transformed() throws URISyntaxException {

        LogfileMetadata[] mds = new LogfileMetadata[] {
            new LogfileMetadata("n1", new GregorianCalendar(2000, 5, 23).getTime(), 100),
            new LogfileMetadata("n2", new GregorianCalendar(2001, 6, 24).getTime(), 50)
        };
        AboutJAXB about = Transformer.transformSoftwareInfo(new SoftwareInfo("n1", "d1", "v1", "c1"),
                new SoftwareInfo[] {new SoftwareInfo("n2", "d2", "v2", "c2"), new SoftwareInfo("n3", "d3", "v3", "c3")},
                new SoftwareInfo[] {new SoftwareInfo("n4", "d4", "v4", "c4"), new SoftwareInfo("n5", "d5", "v5", "c5")});


        assertEquals("n1", about.framework.name);
        assertEquals("c1", about.framework.company);
        assertEquals("d1", about.framework.description);
        assertEquals("v1", about.framework.version);
        assertEquals("n2", about.externalSystem.subsystem[0].name);
        assertEquals("n3", about.externalSystem.subsystem[1].name);
        assertEquals("n4", about.processModel.subsystem[0].name);
        assertEquals("n5", about.processModel.subsystem[1].name);
    }
}