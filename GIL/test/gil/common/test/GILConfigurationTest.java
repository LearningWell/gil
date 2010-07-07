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
package gil.common.test;

import java.io.File;
import java.nio.ByteOrder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import gil.common.ConfigFile;
import gil.common.GILConfiguration;
import gil.common.InvalidFileFormatException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class GILConfigurationTest {

    ConfigFile _mockedConfig = mock(ConfigFile.class);

    public GILConfigurationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        File f = new File("dummy.file");
        if (!f.exists())
            f.createNewFile();

        when(_mockedConfig.getAppSetting("processModelAdapter")).thenReturn("gil.io.example.ExamplePMAdapter");
        when(_mockedConfig.getAppSetting("externalSystemAdapter")).thenReturn("gil.io.example.ExampleESAdapter");
        when(_mockedConfig.getAppSetting("signalExchangeList")).thenReturn("dummy.file");
        when(_mockedConfig.getAppSetting("idleProcessDataTransferInterval")).thenReturn("40");
        when(_mockedConfig.getAppSetting("esAdapterReadPollRate")).thenReturn("100");
        when(_mockedConfig.getAppSetting("esAdapterByteOrder")).thenReturn("LITTLE_ENDIAN");
        when(_mockedConfig.getAppSetting("pmAdapterByteOrder")).thenReturn("BIG_ENDIAN");
    }

    @After
    public void tearDown() {
        File f = new File("dummy.file");
        if (f.exists())
            f.delete();
    }

    @Test
    public void expect_valid_configuration_to_be_accepted() throws Exception {
        GILConfiguration c = new GILConfiguration(_mockedConfig);
        assertEquals("gil.io.example.ExamplePMAdapter", c.getPMAdapterClassName());
        assertEquals("gil.io.example.ExampleESAdapter", c.getESAdapterClassName());
        assertEquals("dummy.file", c.getSignalExchangeListPath());
        assertEquals(40, c.getIdleProcessDataTransferInterval());
        assertEquals(100, c.getESAdapterReadPollRate());
        assertEquals(ByteOrder.LITTLE_ENDIAN, c.getESAdapterByteOrder());
        assertEquals(ByteOrder.BIG_ENDIAN, c.getPMAdapterByteOrder());
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_PMAdapter_is_not_a_valid_class() throws Exception {
        when(_mockedConfig.getAppSetting("processModelAdapter")).thenReturn("dummy");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_PMAdapter_is_not_a_valid_subclass() throws Exception {
        when(_mockedConfig.getAppSetting("processModelAdapter")).thenReturn("gil.io.example.ExampleESAdapter");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_ESAdapter_is_not_a_valid_class() throws Exception {
        when(_mockedConfig.getAppSetting("externalSystemAdapter")).thenReturn("dummy");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_ESAdapter_is_not_a_valid_subclass() throws Exception {
        when(_mockedConfig.getAppSetting("externalSystemAdapter")).thenReturn("gil.io.example.ExamplePMAdapter");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_SEL_file_does_not_exists() throws Exception {
        when(_mockedConfig.getAppSetting("signalExchangeList")).thenReturn("nonexisting.file");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_read_poll_rate_is_not_a_valid_integer() throws Exception {
        when(_mockedConfig.getAppSetting("esAdapterReadPollRate")).thenReturn("notAnInterger");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_when_idle_process_data_transfer_intervall_is_not_a_valid_integer() throws Exception {
        when(_mockedConfig.getAppSetting("idleProcessDataTransferInterval")).thenReturn("notAnInterger");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_invalid_es_adapter_byte_order() throws Exception {
        when(_mockedConfig.getAppSetting("esAdapterByteOrder")).thenReturn("vvvvvvvvv");
        new GILConfiguration(_mockedConfig);
    }

    @Test(expected = InvalidFileFormatException.class)
    public void expect_exception_invalid_pm_adapter_byte_order() throws Exception {
        when(_mockedConfig.getAppSetting("pmAdapterByteOrder")).thenReturn("vvvvvvvvv");
        new GILConfiguration(_mockedConfig);
    }
}