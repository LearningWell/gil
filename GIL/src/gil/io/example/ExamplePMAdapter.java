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
package gil.io.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import gil.common.Parameters;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.core.ParameterDescriptor;
import gil.core.SoftwareInfo;
import gil.core.SignalMetadata;
import gil.core.SimState;
import gil.core.SimTime;
import gil.core.SystemStatus;
import gil.io.IProcessModelAdapter;
import gil.common.IProgressEventListener;
import gil.common.Result;
import gil.common.ValueResult;

/**
 * This class exemplifies the implementation of the IProcessModelAdapter interface. It works in pair with the
 * ExampleESAdapter. Please see IProcessModelAdapter.java for implementation details
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class ExamplePMAdapter implements IProcessModelAdapter {

    private static final String CURRENT_VERSION = "0.5.6";

    // The org.apache.log4j.Logger shall be used for logging if the GIL runtime shall be able to
    // manage the log output. Always use the adapter class name to retrieve a logger from the log4j framework.
    private static Logger _logger = Logger.getLogger(ExamplePMAdapter.class);

    // Definitions of the signals to be transferred to the external system from the process model.
    private SignalMetadata[] _signalsToES;

    // Definitions of the signals to be transferred from the external system to the process model.
    private SignalMetadata[] _signalsToPM;


    int _currentState = SimState.UNKNOWN;
    boolean _commandRequested = false;

    public void setup(SignalMetadata[] signalsToPM, SignalMetadata[] signalsFromPM, Parameters config) {
        _logger.info("Setup called.");
        _signalsToES = signalsFromPM;
        _signalsToPM = signalsToPM;
    }

    public void tearDown() {
        _logger.info("TearDown called.");
    }

    public boolean connect() throws IOException {
        _logger.info("Connect called.");
        return true;
    }

    public void disconnect() {
        _logger.info("Disconnect called.");
    }
    private static int  _transferCount = 0;
    public Result readSignalData(ByteBuffer destBuf) throws IOException {
        int i = 0;
        for (SignalMetadata smd : _signalsToES) {
            for (int elm = 0; elm < smd.getLength(); elm++) {
                destBuf.putFloat((float)((++i) + _transferCount % 10 + 10));
            }
        }
        ++_transferCount;
        return new Result(true);
    }
    
    public Result writeSignalData(ByteBuffer values) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Values written to PM: ");
        for (SignalMetadata smd : _signalsToPM) {
            for (int elm = 0; elm < smd.getLength(); elm++) {
                appendValueToLogOutput(smd.getPMSignalID(), values.getFloat(), sb);
            }
        }
        _logger.info(sb.toString());
        return new Result(true);
    }

    private void appendValueToLogOutput(String signalID, float value, StringBuilder sb) {
        sb.append(signalID);
        sb.append('=');
        sb.append(value);
        sb.append(' ');
    }

    public ValueResult<SimTime> getSimTime() throws IOException {
        return new ValueResult<SimTime>(currentSimTime());
    }

    public int getOperatingFrequency() {
        return 2; // Hz
    }

    public ValueResult<Command[]> readSimCommands() throws IOException {
        Command[] commands = new Command[0];

        if (currentSimTime().second() == 55 || currentSimTime().second() == 0 || currentSimTime().second() == 15) {
            if (_commandRequested)
                return new ValueResult<Command[]>(commands);
        }
        else {
            _commandRequested = false;
        }
            
        // Fake FREEZE every minute at second 55
        if (currentSimTime().second() == 55) {
            commands = new Command[1];
            commands[0] = new Command("FREEZE");
            _currentState = SimState.FREEZE;
            _commandRequested = true;
        }

        // Fake load of IC every minute at second 0
        if (currentSimTime().second() == 0) {
            commands = new Command[1];
            Map<String, String> params = new HashMap<String, String>() {{put("icNo", Integer.toString(currentSimTime().minute()));}};
            commands[0] = new Command("LOAD_IC", params);
            _commandRequested = true;
        }

        // Fake RUN every minute at second 15
        if (currentSimTime().second() == 15) {
            commands = new Command[1];
            commands[0] = new Command("RUN");
            _currentState = SimState.RUN;
            _commandRequested = true;
        }
        
        return new ValueResult<Command[]>(commands);
    }

    public SoftwareInfo[] getInfo() {
        // This example returns just a single SoftwareInfo structure. In a real world implementation the
        // software version of the process model and other relevant subsystems may be added.
        return new SoftwareInfo[] {
                new SoftwareInfo(this.getClass().getSimpleName(),
                "The example adapter to aid development of an IProcessModelAdapter",
                CURRENT_VERSION,
                "LearningWell"),
        };
    }

    public SystemStatus getStatus() {
        return new SystemStatus(SystemStatus.OK, "The PM is OK");
    }

    public int getState() {
        return _currentState;
    }

    public boolean addProgressChangeListener(IProgressEventListener listener) {
        return false; // return false to indicate that this implementation of the
                      // adapter will not notify the hosting software on progress changes.
    }

    public CommandDescriptor[] availableControlCommands() {
        return new CommandDescriptor[] {
            new CommandDescriptor("Restart", "Restarts the process model",
                new ParameterDescriptor[0]
            ),
        };
    }

    public Map<String, String> invokeControlCommand(Command command) throws IllegalArgumentException, IOException {
        if (command.isCommand("Restart")) {
            _logger.info("********* Restart");
            return new HashMap<String, String>() {{ put("Result", "Has been restarted successfully"); }};
        }
        throw new IllegalArgumentException("Unknown control command: " + command);
    }

    private SimTime currentSimTime() {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(System.currentTimeMillis());
        SimTime st = new SimTime(currentTime.get(Calendar.YEAR),
                currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH),
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                currentTime.get(Calendar.SECOND),
                currentTime.get(Calendar.MILLISECOND));
        return st;
    }
}
