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

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.io.IPipelineStage;
import gil.core.ITransferPipeline;
import static org.junit.Assert.*;
import gil.core.IntegrationExecutive;
import gil.core.SignalMetadata;
import gil.core.StageValueObject;
import gil.core.SimState;
import gil.core.SystemStatus;
import gil.io.ExternalSystemAdapter;
import gil.io.IProcessModelAdapter;
import gil.common.IProgressEventListener;
import gil.common.GILConfiguration;
import gil.core.AdapterValueObject;
import static org.mockito.Mockito.*;

public class IntegrationExecutiveTest {

    static GILConfiguration config = mock(GILConfiguration.class);

    IProcessModelAdapter _pmAdapter = mock(IProcessModelAdapter.class);
    ExternalSystemAdapter _esAdapter = mock(ExternalSystemAdapter.class);
    ITransferPipeline _pipeline = mock(ITransferPipeline.class);
    IPipelineStage _stage = mock(IPipelineStage.class);

    SignalMetadata[] _signalsMD = new SignalMetadata[0];

    public IntegrationExecutiveTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        when(_esAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.getState()).thenReturn(SimState.UNKNOWN);
        when(_esAdapter.getStatus()).thenReturn(new SystemStatus(SystemStatus.UNKNOWN,""));
        when(_esAdapter.addProgressChangeListener(any(IProgressEventListener.class))).thenReturn(false);
        when(_esAdapter.canReportState()).thenReturn(true);
        when(_esAdapter.canReportStatus()).thenReturn(true);
        when(_esAdapter.canShutDownAndPowerUp()).thenReturn(true);
        when(_esAdapter.expectsSimulatorCommands()).thenReturn(true);
        when(_esAdapter.isReadEventDriven()).thenReturn(false);
        when(_esAdapter.isSynchronous()).thenReturn(true);
        when(_pmAdapter.getState()).thenReturn(SimState.UNKNOWN);
        when(_pmAdapter.getStatus()).thenReturn(new SystemStatus(SystemStatus.UNKNOWN,""));
        when(_pmAdapter.addProgressChangeListener(any(IProgressEventListener.class))).thenReturn(false);
        when(_pmAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.availableControlCommands()).thenReturn(new CommandDescriptor[0]);
        when(_pmAdapter.availableControlCommands()).thenReturn(new CommandDescriptor[0]);
    }

    @Test
    public void expect_stage_value_objects_to_be_returned_when_getStages_is_called() throws Exception {
        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);

        ArrayList<IPipelineStage> stages = new ArrayList<IPipelineStage>();

        stages.add(_stage);
        CommandDescriptor[] descs = new CommandDescriptor[] {new CommandDescriptor("cmd", "doc")};
                
        
        when(_stage.availableCommands()).thenReturn(descs);
        when(_pipeline.getStages()).thenReturn(stages);

        StageValueObject[] stagesR = instance.getTransferPipelineStages();
        assertEquals(1, stagesR.length);
        assertEquals(0, stagesR[0].seqNo());
        assertEquals(1, stagesR[0].availableCommands().length);
        assertEquals("cmd", stagesR[0].availableCommands()[0].getCommandID());
        assertEquals("doc", stagesR[0].availableCommands()[0].getDocumentation());       
    }

    @Test
    public void expect_connect_being_called_on_ES_and_PM_when_start_is_called() throws Exception {

        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);
        
        when(_pmAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.connect()).thenReturn(false);
        when(_pmAdapter.connect()).thenReturn(false);

        instance.start();
        Thread.sleep(200);
        
        verify(_pmAdapter, times(2)).getOperatingFrequency();
        verify(_esAdapter, atLeast(1)).connect();
        verify(_pmAdapter, atLeast(1)).connect();
    }

    @Test
    public void expect_disconnect_and_tearDown_connect_being_called_on_ES_and_PM_on_closedown() throws Exception {

        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);

        when(_pmAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.connect()).thenReturn(false);
        when(_pmAdapter.connect()).thenReturn(false);

        instance.start();
        Thread.sleep(200);
        instance.stop();

        verify(_esAdapter).disconnect();
        verify(_esAdapter).tearDown();
        verify(_pmAdapter).disconnect();
        verify(_pmAdapter).tearDown();
    }

    @Test
    public void expect_pipeline_stage_commands_to_be_invoked() throws Exception {
        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);
        
        ArrayList<IPipelineStage> stages = new ArrayList<IPipelineStage>();
        stages.add(_stage);

        when(_pmAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.connect()).thenReturn(false);
        when(_pmAdapter.connect()).thenReturn(false);
        when(_pipeline.getStages()).thenReturn(stages);

        instance.start();
        HashMap<String, String> params2 = new HashMap<String, String>() {{
            put("param1", "abc");
        }};

        instance.invokePipelineStageCommand(0, "cmd2", params2);
        Thread.sleep(200);

        HashMap<String, String> params = new HashMap<String, String>() {{
            put("param1", "abc");
        }};
        verify(_stage).invokeControlCommand(new Command("cmd2", params));
    }

    @Test
    public void expect_adapter_value_to_contain_control_commands_when_getExternalSystemAdapter_is_called() throws Exception {
        when(_esAdapter.availableControlCommands()).thenReturn(new CommandDescriptor[] {new CommandDescriptor("cmd", "doc")});
        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);
        
        AdapterValueObject adapter = instance.getExternalSystemAdapter();
        assertEquals(2, adapter.availableCommands().length);
        assertEquals("reconnect", adapter.availableCommands()[0].getCommandID());
        assertEquals("cmd", adapter.availableCommands()[1].getCommandID());
        assertEquals("doc", adapter.availableCommands()[1].getDocumentation());
    }

    @Test
    public void expect_adapter_value_to_contain_controll_commands_when_getProcessModelAdapter_is_called() throws Exception {
        when(_pmAdapter.availableControlCommands()).thenReturn(new CommandDescriptor[] {new CommandDescriptor("cmd", "doc")});
        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);
      
        AdapterValueObject adapter = instance.getProcessModelAdapter();
        assertEquals(2, adapter.availableCommands().length);
        assertEquals("reconnect", adapter.availableCommands()[0].getCommandID());
        assertEquals("cmd", adapter.availableCommands()[1].getCommandID());
        assertEquals("doc", adapter.availableCommands()[1].getDocumentation());
    }

    @Test
    public void expect_ES_commads_to_be_invoked() throws Exception {

        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("param1", "abc");
        
        when(_pmAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.connect()).thenReturn(false);
        when(_pmAdapter.connect()).thenReturn(false);
        when(_esAdapter.addProgressChangeListener(any(IProgressEventListener.class))).thenReturn(false);
        
        instance.start();
        instance.invokeExternalSystemCommand("cmd2", params);
        Thread.sleep(200);

        ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
        verify(_esAdapter).invokeControlCommand(argument.capture());

        assertEquals("cmd2", argument.getValue().getID());        
    }

    @Test
    public void expect_PM_commads_to_be_invoked() throws Exception {
        IntegrationExecutive instance = new IntegrationExecutive(_pmAdapter, _esAdapter, _pipeline, _signalsMD, config);
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("param1", "abc");

        when(_pmAdapter.getOperatingFrequency()).thenReturn(1);
        when(_esAdapter.connect()).thenReturn(false);
        when(_pmAdapter.connect()).thenReturn(false);
        when(_esAdapter.addProgressChangeListener(any(IProgressEventListener.class))).thenReturn(false);

        instance.start();
        instance.invokeProcessModelCommand("cmd2", params);
        Thread.sleep(200);

        ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(Command.class);
        verify(_pmAdapter).invokeControlCommand(argument.capture());

        assertEquals("cmd2", argument.getValue().getID());
    }
}