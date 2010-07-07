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

import gil.common.Result;
import gil.common.ValueResult;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import gil.core.*;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import gil.io.IProcessModelAdapter;
import static org.junit.Assert.*;
import gil.core.SignalMetadata.DataflowDirection;
import gil.core.SimTime;
import gil.common.GILConfiguration;
import gil.io.IPipelineStage;
import static org.mockito.Mockito.*;

/**
 * Test cases for the ProcessModelProcedure.
 * @author Göran Larsson @ LearningWell AB
 */
public class ProcessModelProcedureTest {
    static GILConfiguration config = mock(GILConfiguration.class);
    static {
        when(config.getIdleProcessDataTransferInterval()).thenReturn(40000);
        when(config.getESAdapterByteOrder()).thenReturn(ByteOrder.BIG_ENDIAN);
    }


    final static int BUF_SIZE = 8;
    IntegrationContext context = new IntegrationContext();
    IProcessModelAdapter _pmAdapterMock = mock(IProcessModelAdapter.class);
    ITransferPipeline _pipeline = mock(ITransferPipeline.class);
    ProcessModelProcedure _procedure = new ProcessModelProcedure(_pmAdapterMock, 10, _pipeline, context, BUF_SIZE, config);

    public ProcessModelProcedureTest() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Tests of successful and unsuccessful establishment of a connection to the process model
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_established_connection_to_process_model_after_first_call_to_runOnce() throws Exception {

        context.pendingTransferToPM.add(ByteBuffer.allocate(100));

        when(_pmAdapterMock.connect()).thenReturn(true);
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 50)));
        _procedure.runOnce(0);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ConnectedState);
        assertEquals(0, context.pendingTransferToPM.size());
    }

    @Test
    public void expect_added_pending_transfer_at_first_call_to_runOnce_after_connection_is_established() throws Exception {

        context.pendingTransferToPM.add(ByteBuffer.allocate(100));

        setupPMAdapterStub();
        _procedure.runOnce(0); // Will get connected
        assertEquals(0, context.pendingTransferToES.size());
        _procedure.runOnce(0); // Will do first read from PM and add pending transfer to ES
        assertEquals(1, context.pendingTransferToES.size());
        _procedure.runOnce(0); // No new pending transfers shall be added
        assertEquals(1, context.pendingTransferToES.size());
    }

    @Test
    public void expect_connect_to_be_called_repeadetly_when_connect_returns_false() throws Exception {
        when(_pmAdapterMock.connect()).thenReturn(false);
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 50)));
        _procedure.runOnce(0);
        _procedure.runOnce(0);
        _procedure.runOnce(0);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
        when(_pmAdapterMock.connect()).thenReturn(true);
        _procedure.runOnce(0);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ConnectedState);
    }
    
    @Test
    public void expect_transition_to_ErrorState_when_failing_to_read_simTime_in_disconnected_state() throws Exception {
        when(_pmAdapterMock.connect()).thenReturn(true);
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(" afailure"));
        _procedure.runOnce(0);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ErrorState);
    }

    @Test
    public void expect_transition_to_ErrorState_if_connection_to_process_model_fails() throws Exception {
        when(_pmAdapterMock.connect()).thenThrow(new IOException());
        _procedure.runOnce(0);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ErrorState);
    }

    @Test
    public void expect_es_state_and_status_to_change_depending_on_current_state_of_the_procedure() throws Exception {
        when(_pmAdapterMock.getStatus()).thenReturn(new SystemStatus(SystemStatus.OK, "its OK"));
        when(_pmAdapterMock.getState()).thenReturn(SimState.SLOW);
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 0)));
        when(_pmAdapterMock.connect()).thenReturn(true);
        when(_pmAdapterMock.readSignalData((ByteBuffer)any())).thenReturn(new Result(true));
        when(_pmAdapterMock.readSimCommands()).thenReturn(new ValueResult<Command[]>(new Command[0]));

        // Expect unknown state and status when adapter is disconnected
        assertEquals(SimState.UNKNOWN, _procedure.getProcessModelState());
        assertEquals(SystemStatus.UNKNOWN, _procedure.getProcessModelStatus().getStatusCode());

        // When connected, expect status and state to be read from the adapter.
        _procedure.runOnce(1);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ConnectedState);
        assertEquals(SimState.SLOW, _procedure.getProcessModelState());
        assertEquals("its OK", _procedure.getProcessModelStatus().getDescription());

        // Expect to be disconnected and the status and state to be unknown after the adapter has thrown exception.
        when(_pmAdapterMock.readSignalData((ByteBuffer)any())).thenThrow(new IOException("aaaaa"));
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 2, 0)));
        _procedure.runOnce(2);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
        assertEquals(SimState.UNKNOWN, _procedure.getProcessModelState());
        assertEquals(SystemStatus.UNKNOWN, _procedure.getProcessModelStatus().getStatusCode());

        // Expect to be in error state when adapter has thrown exception when connecting. Expect state to be
        // unknown and status to be NOK. The status description is expected to be the message as the error message
        // in the exception thrown by the adapter.
        when(_pmAdapterMock.connect()).thenThrow(new IOException("errmsg from ES-adapter"));
        _procedure.runOnce(3);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ErrorState);
        assertEquals(SimState.UNKNOWN, _procedure.getProcessModelState());
        assertEquals(SystemStatus.NOK, _procedure.getProcessModelStatus().getStatusCode());
        assertEquals("errmsg from ES-adapter", _procedure.getProcessModelStatus().getDescription());
    }

    @Test
    public void expect_state_transition_from_error_to_disconnected_when_reconnect_is_called() throws Exception {
        when(_pmAdapterMock.connect()).thenThrow(new IOException("errmsg from PM-adapter"));
        _procedure.runOnce(1);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.ErrorState);
        _procedure.reconnect();
        _procedure.runOnce(1);
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
    }

    @Test
    public void expect_state_transition_from_connected_to_disconnected_when_reconnect_is_called() throws Exception {
        doFirstRunOnceCallToConnect();
        _procedure.reconnect();
        _procedure.runOnce(1);
        verify(_pmAdapterMock).disconnect();
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    // Tests of unsuccessful reads of the sim time
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_read_sim_time_failure_count_to_be_incremented_if_failure_when_reading_time_from_process_model() throws Exception{

        doFirstRunOnceCallToConnect();
        
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>("a failure"));

        _procedure.runOnce(0);

        assertEquals(1, _procedure.getSimTimeReadFailureCount());
    }

    @Test
    public void expect_disconnect_from_PM_when_an_IOException_is_thrown_when_reading_simTime() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenThrow(new IOException());

        _procedure.runOnce(0);

        verify(_pmAdapterMock).disconnect();
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Tests of successful and unsuccessful reads of sim commands from the process model
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_added_pending_ES_commands_when_there_are_simulator_events() throws Exception{

        doFirstRunOnceCallToConnect();

        final Command sc0 = new Command("FREEZE");
        final Command sc1 = new Command("LOAD_IC");
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 0)));
        when(_pmAdapterMock.readSimCommands()).thenReturn(new ValueResult<Command[]>(new Command[] { sc0, sc1 }));

        _procedure.runOnce(0);
        
        assertEquals(2, context.pendingSimCommands.size());
        assertEquals("FREEZE", context.pendingSimCommands.pollFirst().getID());
        assertEquals("LOAD_IC", context.pendingSimCommands.pollFirst().getID());
    }

    @Test
    public void expect_disconnect_from_PM_when_an_IOException_is_thrown_when_reading_simulator_commands() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 0)));
        when(_pmAdapterMock.readSimCommands()).thenThrow(new IOException());

        _procedure.runOnce(0);

        verify(_pmAdapterMock).disconnect();
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
    }

    @Test
    public void expect_command_read_failure_count_to_be_incremented_if_failure_when_polling_commands_from_the_process_model() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 0)));
        when(_pmAdapterMock.readSimCommands()).thenReturn(new ValueResult<Command[]>("a failure"));

        _procedure.runOnce(0);

        assertEquals(1, _procedure.getCommandReadFailureCount());
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    // Tests of successful and unsuccessful reading of the process model data to be added
    // as pending data transfers to the external system
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_added_pending_ES_transfers_when_there_is_a_time_step_change() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        when(_pmAdapterMock.readSignalData(any(ByteBuffer.class))).thenReturn(new Result(true));

        // Second runOnce() call will detect a frame change in process model and retrieve process data
        // to be stored as pending data transfers in context object
        _procedure.runOnce(0);        

        assertEquals(1, context.pendingTransferToES.size());
        ByteBuffer values = context.pendingTransferToES.pollFirst();
        verify(_pmAdapterMock).readSignalData(same(values));
    }

    @Test
    public void expect_PM_data_to_be_passing_the_transfer_pipeline_in_the_toES_direction() throws Exception {
        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        when(_pmAdapterMock.readSignalData(any(ByteBuffer.class))).thenReturn(new Result(true));

        _procedure.runOnce(0); // data transfer through pipeline

        ByteBuffer values = context.pendingTransferToES.pollFirst();
        verify(_pipeline).processSignals(same(values), eq(DataflowDirection.ToES));
    }

    @Test
    public void expect_no_added_pending_ES_transfers_when_there_is_no_time_step_change() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 99)));

        _procedure.runOnce(0);

        verify(_pipeline, never()).processSignals(any(ByteBuffer.class), any(DataflowDirection.class));
        assertEquals(0, context.pendingTransferToES.size());
    }

    @Test
    public void expect_no_added_pending_ES_transfers_if_there_is_a_failure_when_reading_PM_data() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        when(_pmAdapterMock.readSignalData(any(ByteBuffer.class))).thenReturn(new Result(false, "a failure"));

        // Second runOnce() call will detect a frame change in process model and retrieve process data
        // to be stored as pending data transfers in context object
        _procedure.runOnce(0);

        verify(_pipeline, never()).processSignals(any(ByteBuffer.class), any(DataflowDirection.class));
        assertEquals(0, context.pendingTransferToES.size());
        assertEquals(1, _procedure.getDataReadFailureCount());
    }

    @Test
    public void expect_disconnect_from_PM_when_an_IOException_is_thrown_when_reading_PM_data() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        when(_pmAdapterMock.readSignalData(any(ByteBuffer.class))).thenThrow(new IOException());

        // In the second runOnce() call the PMAdapter will throw an IOException.
        _procedure.runOnce(0);
        
        verify(_pmAdapterMock).disconnect();
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
        assertEquals(0, context.pendingTransferToES.size());

        when(_pmAdapterMock.connect()).thenReturn(true);

        // In the third call we try to reconnnect
        _procedure.runOnce(0);
        verify(_pmAdapterMock).connect();
    }

    @Test
    public void expect_added_pending_ES_transfer_after_timeout() throws Exception {
        doFirstRunOnceCallToConnect();

        // 39.999 seconds have elapsed and no transfer shall be done.
        _procedure.runOnce(39999);
        verify(_pmAdapterMock, never()).readSignalData(any(ByteBuffer.class));

        // 40 seconds have elapsed and a single transfer shall be done.
        _procedure.runOnce(40000);
        verify(_pmAdapterMock, times(1)).readSignalData(any(ByteBuffer.class));

        assertEquals(1, context.pendingTransferToES.size());
        ByteBuffer values = context.pendingTransferToES.pollFirst();
        verify(_pmAdapterMock, times(1)).readSignalData(same(values));
    }

    @Test
    public void expect_elapsed_time_adjustment_to_timeout() throws Exception {
        doFirstRunOnceCallToConnect();

        _procedure.runOnce(70000);
        _procedure.runOnce(80000);

        assertEquals(2, context.pendingTransferToES.size());
    }

    @Test
    public void expect_dropped_Process_model_frames_to_be_incremented_when_several_timeouts_have_occurred() throws Exception {
        doFirstRunOnceCallToConnect();

        _procedure.runOnce(80000);

        assertEquals(1, _procedure.getDroppedProcessModelFrames());
    }

    @Test
    public void expect_no_added_pending_ES_transfer_after_timeout_when_timeout_is_set_to_zero() throws Exception {

        when(config.getIdleProcessDataTransferInterval()).thenReturn(0);

        ProcessModelProcedure pm = new ProcessModelProcedure(_pmAdapterMock, 10, _pipeline, context, BUF_SIZE, config);

        when(_pmAdapterMock.connect()).thenReturn(true);
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 0)));
        when(_pmAdapterMock.readSignalData((ByteBuffer)any())).thenReturn(new Result(true));
        when(_pmAdapterMock.readSimCommands()).thenReturn(new ValueResult<Command[]>(new Command[0]));

        pm.runOnce(0);
        pm.runOnce(0);
        verify(_pmAdapterMock).readSignalData(any(ByteBuffer.class));

        pm.runOnce(1001); // This call shall not trigger another pmAdapter.readSignalData and consequently no ES-transfers
                          // will be added.
        verify(_pmAdapterMock).readSignalData(any(ByteBuffer.class));
    }

    @Test
    public void expect_dropped_Process_model_frames_to_be_incremented_when_frames_are_lost() throws Exception {

        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 300)));
        when(_pmAdapterMock.readSignalData((ByteBuffer)any())).thenReturn(new Result(true));

        _procedure.runOnce(0);

        assertEquals(2, _procedure.getDroppedProcessModelFrames());

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 700)));

        _procedure.runOnce(0);

        assertEquals(5, _procedure.getDroppedProcessModelFrames());
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Tests of successful and unsuccessful writing of pending data transfers from the
    // external system to the process model.
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_pending_ES_data_to_be_written_to_PM() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        doFirstRunOnceCallToConnect();

        context.pendingTransferToPM.add(buf);
        _procedure.runOnce(0);

        verify(_pmAdapterMock).writeSignalData(same(buf));
        assertEquals(0, _procedure.getDataWriteFailureCount());
        assertEquals(0, context.pendingTransferToPM.size());
    }

    @Test
    public void expect_ES_data_to_be_passing_the_transfer_pipeline_in_the_toPM_direction() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.writeSignalData(same(buf))).thenReturn(new Result(true));

        context.pendingTransferToPM.add(buf);
        _procedure.runOnce(0); // data transfer through pipeline

        verify(_pipeline).processSignals(same(buf), eq(DataflowDirection.ToPM));
    }

    @Test
    public void expect_write_failure_count_to_be_incremented_if_failure_when_writing_ES_data_to_the_process_model() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.writeSignalData(same(buf))).thenReturn(new Result(false));

        context.pendingTransferToPM.add(buf);
        _procedure.runOnce(0);

        assertEquals(1, _procedure.getDataWriteFailureCount());
        assertEquals(0, context.pendingTransferToPM.size());
    }

    @Test
    public void expect_disconnect_from_PM_when_an_IOException_is_thrown_when_writing_ES_data_to_the_process_model() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        doFirstRunOnceCallToConnect();
        
        when(_pmAdapterMock.writeSignalData(same(buf))).thenThrow(new IOException());       

        context.pendingTransferToPM.add(buf);
        _procedure.runOnce(0);

        verify(_pmAdapterMock).disconnect();
        assertTrue(_procedure.currentState() instanceof ProcessModelProcedure.DisconnectedState);
        assertEquals(0, context.pendingTransferToPM.size());
    }

    @Test
    public void expect_dropped_External_system_frames_to_be_incremented_when_more_than_a_single_data_transfer_is_pending() throws Exception {
        ByteBuffer buf0 = ByteBuffer.allocate(BUF_SIZE);
        ByteBuffer buf1 = ByteBuffer.allocate(BUF_SIZE);
        ByteBuffer buf2 = ByteBuffer.allocate(BUF_SIZE);

        doFirstRunOnceCallToConnect();

        context.pendingTransferToPM.add(buf0);
        context.pendingTransferToPM.add(buf1);
        context.pendingTransferToPM.add(buf2);
        _procedure.runOnce(0);

        verify(_pmAdapterMock).writeSignalData(same(buf2));
        assertEquals(2, _procedure.getDroppedExternalSystemFrames());
        assertEquals(0, context.pendingTransferToPM.size());

        when(_pmAdapterMock.writeSignalData(same(buf1))).thenReturn(new Result(true));

        context.pendingTransferToPM.add(buf0);
        context.pendingTransferToPM.add(buf1);
        _procedure.runOnce(0);

        verify(_pmAdapterMock).writeSignalData(same(buf1));
        assertEquals(3, _procedure.getDroppedExternalSystemFrames());
        assertEquals(0, context.pendingTransferToPM.size());
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Detection of frame changes in PM
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_esFrameCount_to_be_incremented_corresponding_to_the_ES_operating_frequency() throws Exception {
        doFirstRunOnceCallToConnect();

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        _procedure.runOnce(0);
        assertEquals(1, context.esFrameCount);

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        _procedure.runOnce(0);
        assertEquals(1, context.esFrameCount);

        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 200)));
        _procedure.runOnce(0);
        assertEquals(2, context.esFrameCount);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Test control commands
    ////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void expect_controlCommands_to_be_invoked_on_PMAdapter() throws Exception {

        doFirstRunOnceCallToConnect();

        new Thread(new Runnable() {
            public void run() {
                try { Thread.sleep(25); } catch (InterruptedException ex) {}
                _procedure.runOnce(1);
            }
        }).start();
                
        _procedure.invokeControlCommand("cmID", new HashMap<String, String>(){{ put("p1", "v1"); }});

        ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
        verify(_pmAdapterMock).invokeControlCommand(argument.capture());
        assertEquals("cmID", argument.getValue().getID());
        assertEquals("v1", argument.getValue().getParameter("p1"));
    }

    @Test
    public void expect_controlCommands_to_be_invoked_on_pipeline_stage() throws Exception {
        IPipelineStage stage0 = mock(IPipelineStage.class);
        IPipelineStage stage1 = mock(IPipelineStage.class);

        doFirstRunOnceCallToConnect();

        List<IPipelineStage> stages = new ArrayList<IPipelineStage>();
        stages.add(stage0);
        stages.add(stage1);
        when(_pipeline.getStages()).thenReturn(stages);

        new Thread(new Runnable() {
            public void run() {
                try { Thread.sleep(25); } catch (InterruptedException ex) {}
                _procedure.runOnce(1);
            }
        }).start();

        _procedure.invokePipelineStageControlCommand(1, "cmID", new HashMap<String, String>(){{ put("p1", "v1"); }});

        ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
        verify(stage1).invokeControlCommand(argument.capture());
        assertEquals("cmID", argument.getValue().getID());
        assertEquals("v1", argument.getValue().getParameter("p1"));
        verify(stage0, never()).invokeControlCommand(any(Command.class));
    }

    // Connect and set up stubs using the Mockito mocking library
    private void doFirstRunOnceCallToConnect() throws Exception {
        setupPMAdapterStub();
        
        _procedure.runOnce(0); // Connect
        _procedure.runOnce(0); // First call that will always do a read from the PM and add a pending transfer to ES.

        context.esFrameCount = 0;
        context.pendingSimCommands.clear();
        context.pendingTransferToPM.clear();
        context.pendingTransferToES.clear();

        reset(_pmAdapterMock);
        reset(_pipeline);
        setupPMAdapterStub();
    }

    private void setupPMAdapterStub() throws IOException {
        when(_pmAdapterMock.connect()).thenReturn(true);
        when(_pmAdapterMock.getStatus()).thenReturn(new SystemStatus(SystemStatus.OK, "its OK"));
        when(_pmAdapterMock.getState()).thenReturn(SimState.FREEZE);
        when(_pmAdapterMock.getSimTime()).thenReturn(new ValueResult<SimTime>(new SimTime(2009, 1, 1, 1, 1, 1, 0)));
        when(_pmAdapterMock.readSignalData((ByteBuffer) any())).thenReturn(new Result(true));
        when(_pmAdapterMock.writeSignalData(any(ByteBuffer.class))).thenReturn(new Result(true));
        when(_pmAdapterMock.readSimCommands()).thenReturn(new ValueResult<Command[]>(new Command[0]));
    }
 }