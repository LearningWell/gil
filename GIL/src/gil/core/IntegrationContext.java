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

import java.nio.ByteBuffer;
import java.util.ArrayDeque;

/**
 * Class containing context data to be shared between the two threads hadling the communication
 * with the external system and the process models.
 * @author Göran Larsson @ LearningWell AB
 */
public class IntegrationContext {

    // Theese are shared between threads and concurrency issues must be handled
    public final ArrayDeque<ByteBuffer> pendingTransferToPM = new ArrayDeque<ByteBuffer>();
    public final ArrayDeque<ByteBuffer> pendingTransferToES = new ArrayDeque<ByteBuffer>();
    public final ArrayDeque<Command> pendingSimCommands = new ArrayDeque<Command>();
    public volatile long esFrameCount = 0;
}
