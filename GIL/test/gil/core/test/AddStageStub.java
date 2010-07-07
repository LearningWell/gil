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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.io.PipelineStage;
import gil.core.SignalMetadata;

/**
 * A stub class for adding an offset of 10 to all integers fed through the stage.
 * 
 * @author Göran Larsson @ LearningWell AB
 */
public class AddStageStub extends PipelineStage {

    private int _offset = 10;
    
    public void processSignals(ByteBuffer values, SignalMetadata.DataflowDirection direction) {
        do {
            int pos = values.position();
            int value = values.getInt();
            values.putInt(pos, value + _offset);
        } while (values.hasRemaining());
    }

    public Map<String, String> invokeControlCommand(Command command) {
        if (command.isCommand("OFFSET")) {
            _offset = Integer.parseInt(command.getParameters().values().iterator().next());
        }
        return new HashMap<String, String>();
    }
    
    public CommandDescriptor[] availableCommands() {
        return new CommandDescriptor[0];
    }
}
