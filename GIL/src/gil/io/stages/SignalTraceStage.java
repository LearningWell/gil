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
package gil.io.stages;

import gil.core.IllegalConfigurationException;
import gil.io.PipelineStage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import gil.common.Parameters;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.core.SignalMetadata;
import gil.core.SignalMetadata.DataflowDirection;

/**
 * TODO: Yet to be implemented.
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class SignalTraceStage extends PipelineStage {

    public void processSignals(ByteBuffer values, DataflowDirection direction) {
        Logger.getLogger(this.getClass().getName()).info("processSignals called on " + this.getClass().getName());
    }

    @Override
    public void setup(String id, SignalMetadata[] signalsToPM, SignalMetadata[] signalsToES, Parameters config) throws IllegalConfigurationException {
        super.setup(id, signalsToPM, signalsToES, config);
    }
    
    public Map<String, String> invokeControlCommand(Command command) {
        return new HashMap<String, String>();
    }
    
    public CommandDescriptor[] availableCommands() {
        return new CommandDescriptor[0];
    }
}
