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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import gil.core.SignalMetadata.SignalDataType;
import gil.core.SignalMetadata;
import static org.junit.Assert.*;

/**
  * @author Göran Larsson @ LearningWell AB
 */
public class SignalMetadataTest {

    public SignalMetadataTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void expect_buffer_size_for_a_metadata_when_calcBufferSize_is_called() {

        SignalMetadata md = new SignalMetadata(SignalMetadata.SignalType.Analog, SignalMetadata.SignalDataType.Float64, 10,
              SignalMetadata.DataflowDirection.ToPM, "sig1", null, null, null);
        assertEquals(80, md.getBufferSize());
    }

    @Test
    public void expect_total_buffer_size_for_a_metadata_array_when_static_calcBufferSize_is_called() {
        SignalMetadata[] mds = new SignalMetadata[SignalDataType.values().length - 1];
        SignalDataType[] enumValues = SignalDataType.values();
        for (int i = 1; i < enumValues.length; i++)
        {
            mds[i - 1] = new SignalMetadata();
            mds[i - 1].setDataType(enumValues[i]);
        }
        assertEquals(19, SignalMetadata.calcBufferSize(mds));
    }

    @Test
    public void expect_size_to_be_calculated_when_metadata_represents_several_values() {
        SignalMetadata[] mds = new SignalMetadata[SignalDataType.values().length - 1];
        SignalDataType[] enumValues = SignalDataType.values();
        for (int i = 1; i < enumValues.length; i++)
        {
            mds[i - 1] = new SignalMetadata(SignalMetadata.SignalType.Analog, enumValues[i], 5,
                    SignalMetadata.DataflowDirection.ToPM, "sig1", null, null, null);
        }
        assertEquals(19 * 5, SignalMetadata.calcBufferSize(mds));
    }

    @Test
    public void expect_only_signals_with_direction_to_external_system_from_getSignalsToExternalSystem() {
        SignalMetadata[] allSignals = new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToPM, "sig1", null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToES, "sig2", null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToPM, "sig3", null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToES, "sig4", null, null, null)
        };

        SignalMetadata[] signalsToES = SignalMetadata.getSignalsToExternalSystem(allSignals);
        assertEquals(2, signalsToES.length);
        assertEquals("sig2", signalsToES[0].getPMSignalID());
        assertEquals("sig4", signalsToES[1].getPMSignalID());
    }

    @Test
    public void expect_only_signals_with_direction_to_process_model_from_getSignalsToProcessModel() {
        SignalMetadata[] allSignals = new SignalMetadata[] {
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToPM, "sig1", null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToES, "sig2", null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToPM, "sig3", null, null, null),
            new SignalMetadata(SignalMetadata.SignalType.Analog, SignalDataType.Float32, 1,
                    SignalMetadata.DataflowDirection.ToES, "sig4", null, null, null)
        };

        SignalMetadata[] signalsToES = SignalMetadata.getSignalsToProcessModel(allSignals);
        assertEquals(2, signalsToES.length);
        assertEquals("sig1", signalsToES[0].getPMSignalID());
        assertEquals("sig3", signalsToES[1].getPMSignalID());
    }
}