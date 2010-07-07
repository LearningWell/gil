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
package gil.core;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class StageValueObject {

    private final int _seqNo;
    private final String _name;
    private final CommandDescriptor[] _availableCommands;

            
    public StageValueObject(int seqNo, String name, CommandDescriptor[] availableCommands) {
        _seqNo = seqNo;
        _name = name;
        _availableCommands = availableCommands;
    }

    public CommandDescriptor[] availableCommands() {
        return _availableCommands;
    }

    /**
     * Sequence number indicates where the stage is placed in the chain of stages in the transfer pipeline. seqNo
     * 0 is the first stage.
     */
    public int seqNo() {
        return _seqNo;
    }

    public String name() {
        return _name;
    }
}
