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
package gil.io.stages.test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import org.junit.Test;
import org.junit.Ignore;
import gil.common.Parameters;
import gil.core.Command;
import static org.junit.Assert.*;
import gil.io.stages.ConvertStage;
import gil.core.IllegalConfigurationException;
import gil.core.SignalMetadata;
import gil.core.SignalMetadata.DataflowDirection;
import java.nio.ByteOrder;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class ConvertStageTest {
    
    @Test
    public void expect_value_conversion_in_direction_to_ES() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config0 = new HashMap<String, String>();
        HashMap<String, String> config1 = new HashMap<String, String>();
        HashMap<String, String> config2 = new HashMap<String, String>();

        config0.put("PMMax", "200");
        config0.put("PMMin", "10");
        config0.put("ESMax", "20");
        config0.put("ESMin", "4");
        config1.put("PMMax", "1000");
        config1.put("PMMin", "300");
        config1.put("ESMax", "20");
        config1.put("ESMin", "0");
        
        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config0),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Int16, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config2),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float64, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config1)
        };


        testObject.setup("anId", new SignalMetadata[0], sm, new Parameters());

        ByteBuffer values = ByteBuffer.allocate(14);
        values.putFloat(200);
        values.putShort((short)123);
        values.putDouble(1000);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToES);
        values.rewind();
        assertEquals(20, values.getFloat(), 0.001);
        assertEquals(123, values.getShort());
        assertEquals(20, values.getDouble(), 0.001);

        values.rewind();
        values.putFloat(10);
        values.putShort((short)123);
        values.putDouble(300);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToES);
        values.rewind();
        assertEquals(4, values.getFloat(), 0.001);
        assertEquals(123, values.getShort());
        assertEquals(0, values.getDouble(), 0.001);
    }

    @Test
    public void expect_value_conversion_in_direction_to_PM() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config0 = new HashMap<String, String>();
        HashMap<String, String> config1 = new HashMap<String, String>();
        HashMap<String, String> config2 = new HashMap<String, String>();

        config0.put("PMMax", "200");
        config0.put("PMMin", "10");
        config0.put("ESMax", "20");
        config0.put("ESMin", "4");
        config1.put("PMMax", "1000");
        config1.put("PMMin", "300");
        config1.put("ESMax", "20");
        config1.put("ESMin", "0");

        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToPM, "pmid1", "pmid2", "", config0),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Int16, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config2),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float64, 1,
                DataflowDirection.ToPM, "pmid1", "pmid2", "", config1)
        };

        testObject.setup("anId", sm, new SignalMetadata[0], new Parameters());
        
        ByteBuffer values = ByteBuffer.allocate(14);
        values.putFloat(20);
        values.putShort((short)123);
        values.putDouble(20);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToPM);
        values.rewind();
        assertEquals(200.0, values.getFloat(), 0.001);
        assertEquals(123, values.getShort());
        assertEquals(1000.0, values.getDouble(), 0.001);

        values.rewind();
        values.putFloat(4);
        values.putShort((short)123);
        values.putDouble(0);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToPM);
        values.rewind();
        assertEquals(10.0, values.getFloat(), 0.001);
        assertEquals(123, values.getShort());
        assertEquals(300.0, values.getDouble(), 0.001);
    }

    @Test
    public void expect_elements_of_length_greater_than_one_to_be_converted() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config = new HashMap<String, String>();
        HashMap<String, String> config1 = new HashMap<String, String>();

        config.put("PMMax", "200");
        config.put("PMMin", "10");
        config.put("ESMax", "20");
        config.put("ESMin", "4");

        config1.put("PMMax", "500");
        config1.put("PMMin", "50");
        config1.put("ESMax", "20");
        config1.put("ESMin", "4");

        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 2,
                DataflowDirection.ToPM, "pmid1", "pmid2", "", config1),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config),
        };

        testObject.setup("anId", sm, new SignalMetadata[0], new Parameters());

        ByteBuffer values = ByteBuffer.allocate(12);
        values.putFloat(20);
        values.putFloat(20);
        values.putFloat(20);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToPM);
        values.rewind();
        assertEquals(500.0, values.getFloat(), 0.001);
        assertEquals(500.0, values.getFloat(), 0.001);
        assertEquals(200.0, values.getFloat(), 0.001);
    }

    @Test
    public void expect_values_not_having_conversion_factors_to_be_ignored() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config0 = new HashMap<String, String>();
        HashMap<String, String> config1 = new HashMap<String, String>();

        config0.put("PMMax", "200");
        config0.put("PMMin", "10");
        config0.put("ESMax", "20");
        config0.put("ESMin", "4");

        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config0),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config1),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config0)
        };

        testObject.setup("anId", new SignalMetadata[0], sm, new Parameters());

        ByteBuffer values = ByteBuffer.allocate(12);
        values.putFloat(200);
        values.putFloat(1000);
        values.putFloat(200);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToES);
        values.rewind();
        assertEquals(20, values.getFloat(), 0.001);
        assertEquals(1000, values.getFloat(), 0.001);
        assertEquals(20, values.getFloat(), 0.001);

        values.rewind();
        values.putFloat(10);
        values.putFloat(300);
        values.putFloat(10);
        values.rewind();

        testObject.processSignals(values, DataflowDirection.ToES);
        values.rewind();
        assertEquals(4, values.getFloat(), 0.001);
        assertEquals(300, values.getFloat(), 0.001);
        assertEquals(4, values.getFloat(), 0.001);
    }

    @Test(expected=IllegalConfigurationException.class)
    public void expect_metadata_records_not_having_all_conversion_attributes_generating_exception() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config0 = new HashMap<String, String>();

        config0.put("PMMax", "200");
        config0.put("PMMin", "10");
        //config0.put("ESMax", "20"); // Missing and shall generate an IllegalConfigurationException
        config0.put("ESMin", "4");

        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config0),
        };

        testObject.setup("anId", new SignalMetadata[0], sm, new Parameters());        
    }

    @Test(expected=IllegalConfigurationException.class)
    public void expect_illegal_float_format_to_generate_exception() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config0 = new HashMap<String, String>();

        config0.put("PMMax", "200");
        config0.put("PMMin", "10");
        config0.put("ESMax", "JKL"); // illegal float value
        config0.put("ESMin", "4");

        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config0),
        };

        testObject.setup("anId", new SignalMetadata[0], sm, new Parameters());
    }

    @Test
    @Ignore
    public void performance_test() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();

        SignalMetadata[] smds = new SignalMetadata[100000];
        for (int i = 0; i < smds.length; i++) {
            HashMap<String, String> config = new HashMap<String, String>();
            config.put("PMMax", "99999");
            config.put("PMMin", "0");
            config.put("ESMax", "20");
            config.put("ESMin", "4");
            smds[i] = new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config);
        }

        testObject.setup("anId", new SignalMetadata[0], smds, new Parameters());

        int i = 0;
        ByteBuffer values = ByteBuffer.allocate(100000 * 4);
        values.order(ByteOrder.BIG_ENDIAN);
        do {
            values.putFloat((float) i++);
        } while(values.hasRemaining());

        values.rewind();
        long time = System.currentTimeMillis();
        //for (int x = 0; x < 100; x++) {
        //    values.rewind();
            testObject.processSignals(values, DataflowDirection.ToES);
        //}
        long time2 = System.currentTimeMillis();

        System.out.println("Total time:" + (time2 - time));
        values.rewind();
        assertEquals(4, values.getFloat(), 0.001);
        assertEquals(20, values.getFloat(99999 * 4), 0.001);
    }

    @Test
    public void expect_invoked_bypass_command_to_affect_if_values_are_recalculated() throws IllegalConfigurationException {
        ConvertStage testObject = new ConvertStage();
        HashMap<String, String> config0 = new HashMap<String, String>();

        config0.put("PMMax", "200");
        config0.put("PMMin", "10");
        config0.put("ESMax", "20");
        config0.put("ESMin", "4");

        SignalMetadata[] sm =  new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float32, 1,
                DataflowDirection.ToES, "pmid1", "pmid2", "", config0),
        };
        
        testObject.setup("anId", new SignalMetadata[0], sm, new Parameters());

        ByteBuffer values = ByteBuffer.allocate(14);
        values.putFloat(200);
        values.rewind();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("doBypass", "true");
        testObject.invokeControlCommand(new Command("bypass", params, null));
        values.rewind();
        testObject.processSignals(values, DataflowDirection.ToES);
        values.rewind();
        assertEquals(200, values.getFloat(), 0.001);

        params.put("doBypass", "false");
        testObject.invokeControlCommand(new Command("bypass", params, null));
        values.rewind();
        testObject.processSignals(values, DataflowDirection.ToES);
        values.rewind();
        assertEquals(20, values.getFloat(), 0.001);
    }
}