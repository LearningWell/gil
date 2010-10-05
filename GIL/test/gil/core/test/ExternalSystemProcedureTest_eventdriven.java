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
import org.junit.Before;
import gil.common.Result;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import gil.core.*;
import org.junit.Test;
import gil.io.ExternalSystemAdapter;
import gil.common.GILConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the event driven read of signal data.
 * @author Göran Larsson @ LearningWell AB
 */
public class ExternalSystemProcedureTest_eventdriven {

    final static int BUF_SIZE = 8;
    IntegrationContext context = new IntegrationContext();

    static final GILConfiguration config = mock(GILConfiguration.class);
    static {
        when(config.getESAdapterReadPollRate()).thenReturn(1);
        when(config.getPMAdapterByteOrder()).thenReturn(ByteOrder.BIG_ENDIAN);
    }
    
    ExternalSystemAdapter _esAdapterMock = mock(ExternalSystemAdapter.class);
    ExternalSystemProcedure _procedure;    

    @Before
    public void setUp() {
        setupESAdapterMock();
        _procedure = new ExternalSystemProcedure(_esAdapterMock, context, BUF_SIZE, config);
    }

    @After
    public void tearDown() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Tests of successful and unsuccessful reading of signal data from the external system
    // using event driven reads.
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_added_pending_transfers_to_PM_when_data_changed_event_is_triggered() throws Exception {
        _procedure = new ExternalSystemProcedure(_esAdapterMock, context, BUF_SIZE, config);

        doFirstRunOnceCallToConnect();

        ByteBuffer data = ByteBuffer.allocate(BUF_SIZE);
        _procedure.dataChanged(data, new SimTime(), new Result(true), null);

        assertEquals(1, context.pendingTransferToPM.size());
        Data values = context.pendingTransferToPM.pollFirst();
        assertSame(data, values.getData());
        assertEquals(0, _procedure.getStatistics().dataReadFailureCount);
        verify(_esAdapterMock, never()).readSignalData(any(ByteBuffer.class));
    }

    @Test
    public void expect_failure_count_to_be_incremented_on_failures() throws Exception {
        _procedure = new ExternalSystemProcedure(_esAdapterMock, context, BUF_SIZE, config);

        doFirstRunOnceCallToConnect();

        ByteBuffer data = ByteBuffer.allocate(BUF_SIZE);
        _procedure.dataChanged(data, new SimTime(), new Result(false), null);

        assertEquals(0, context.pendingTransferToPM.size());
        assertEquals(1, _procedure.getStatistics().dataReadFailureCount);
    }

    @Test
    public void expect_disconnect_from_ES_when_an_IOException_is_signaled() throws Exception {
        _procedure = new ExternalSystemProcedure(_esAdapterMock, context, BUF_SIZE, config);

        doFirstRunOnceCallToConnect();

        ByteBuffer data = ByteBuffer.allocate(BUF_SIZE);
        _procedure.dataChanged(data, new SimTime(), new Result(true), new IOException());

        _procedure.runOnce(1);

        verify(_esAdapterMock).disconnect();
        assertTrue(_procedure.currentState() instanceof ExternalSystemProcedure.DisconnectedState);
    }

    @Test
    public void expect_dropped_External_system_frames_to_be_incremented_when_more_than_a_single_data_transfer_is_pending() throws Exception {
        _procedure = new ExternalSystemProcedure(_esAdapterMock, context, BUF_SIZE, config);

        doFirstRunOnceCallToConnect();

        ByteBuffer data = ByteBuffer.allocate(BUF_SIZE);
        _procedure.dataChanged(data, new SimTime(), new Result(true), null);
        _procedure.dataChanged(data, new SimTime(), new Result(true), null);
        _procedure.dataChanged(data, new SimTime(), new Result(true), null);
        _procedure.dataChanged(data, new SimTime(), new Result(true), null);
        
        assertEquals(3, _procedure.getStatistics().droppedFrames);
        assertEquals(1, context.pendingTransferToPM.size());
    }

    private void setupESAdapterMock() {
        when(_esAdapterMock.canReportState()).thenReturn(true);
        when(_esAdapterMock.canReportStatus()).thenReturn(true);
        when(_esAdapterMock.canShutDownAndPowerUp()).thenReturn(true);
        when(_esAdapterMock.expectsSimulatorCommands()).thenReturn(true);
        when(_esAdapterMock.isReadEventDriven()).thenReturn(true);
        when(_esAdapterMock.isSynchronous()).thenReturn(true);
    }
    
    private void doFirstRunOnceCallToConnect() throws Exception {
        when(_esAdapterMock.connect()).thenReturn(true);
        when(_esAdapterMock.getStatus()).thenReturn(new SystemStatus(SystemStatus.OK, "its OK"));
        when(_esAdapterMock.getState()).thenReturn(SimState.FREEZE);
        when(_esAdapterMock.readSignalData((ByteBuffer)any())).thenReturn(null);

        when(_esAdapterMock.connect()).thenReturn(true);
        _procedure.runOnce(0);
    }  
}
