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
package gil.io;

import java.nio.ByteBuffer;
import java.util.Map;
import gil.common.Parameters;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.core.IllegalConfigurationException;
import gil.core.SignalMetadata;
import gil.core.SignalMetadata.DataflowDirection;

/**
 * Abstract baseclass for pipeline stages.
 * @author Göran Larsson @ LearningWell AB
 */
public abstract class PipelineStage implements IPipelineStage {

    private String _id;
    private SignalMetadata[] _signalsToPM;
    private SignalMetadata[] _signalsToES;
    private Parameters _config;

    public abstract void processSignals(ByteBuffer values, DataflowDirection direction);
    public abstract Map<String, String> invokeControlCommand(Command command) throws IllegalArgumentException;
    public abstract CommandDescriptor[] availableCommands();

    public void setup(String id, SignalMetadata[] signalsToPM, SignalMetadata[] signalsToES, Parameters config) throws IllegalConfigurationException {
        _id = id;
        _signalsToES = signalsToES;
        _signalsToPM = signalsToPM;
        _config = config;
    }

    public String getId() {
        return _id;
    }

    public SignalMetadata[] getSignalsToPM() {
        return _signalsToPM;
    }

    public SignalMetadata[] getSignalsToES() {
        return _signalsToES;
    }

    public Parameters getConfig() {
        return _config;
    }
}
