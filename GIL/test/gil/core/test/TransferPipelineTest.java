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

import gil.io.IPipelineStage;
import java.nio.ByteBuffer;
import gil.core.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;


public class TransferPipelineTest {
    public TransferPipelineTest() {
    }

    @Test
    public void expect_signals_to_be_processed_by_pipeline_stages() {

        ArrayList<IPipelineStage> stages = new ArrayList<IPipelineStage>();
        
        AddStageStub add = new AddStageStub();
        MultiplyStageStub mul = new MultiplyStageStub();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("param1", "20");

        add.invokeControlCommand(new Command("OFFSET", params));
        params.clear();
        params.put("param1", "100");
        mul.invokeControlCommand(new Command("FACTOR", params));
                
        stages.add(add);
        stages.add(mul);

        ByteBuffer values = ByteBuffer.allocate(8);

        values.putInt(1);
        values.putInt(2);

        TransferPipeline instance = new TransferPipeline(stages);

        instance.processSignals(values, SignalMetadata.DataflowDirection.ToES);

        assertEquals(2100, values.getInt());
        assertEquals(2200, values.getInt());
    }
}