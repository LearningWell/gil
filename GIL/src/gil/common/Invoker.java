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
package gil.common;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Aggreagates a queue of commands to be executed on a separate thread from the thread where they have been
 * scheduled.
 * @author Göran Larsson @ LearningWell AB
 */
public class Invoker {

    private class ScheduledCommand {
        private IInvokeable _command;
        private WeakReference<AsyncResult> _result;

        ScheduledCommand(IInvokeable command) {
            _command = command;
            _result = new WeakReference<AsyncResult>(new AsyncResult());
        }

        public IInvokeable getCommand() {
            return _command;
        }
        
        public AsyncResult getResult() {
            return _result.get();
        }
    }

    private final Queue<ScheduledCommand> _pendingCommands = new ConcurrentLinkedQueue<ScheduledCommand>();

    /**
     * Schedules the given command to be invoked. 
     * @param command The command to be invoked.
     * @return An object to monitor the progress of the asynchronous operation and to provide the result when
     * the asynchronous operation is completed.
     */
    public AsyncResult schedule(IInvokeable command) {
        ScheduledCommand cmd = new ScheduledCommand(command);
        _pendingCommands.add(cmd);
        return cmd.getResult();
    }

    /**
     * Executes the next command from the underlying queue of scheduled commands.
     * This method is most certain not called from the same thread as {@link #schedule(gil.common.IInvokeable)}.
     *  TODO: Handle cancellation of async operations
     */
    public void executeNextCommandInQueue() {
        ScheduledCommand next = _pendingCommands.poll();
        if (next == null) {
            return;
        }
        AsyncResult result = next.getResult();
        if (result != null) {
            try
            {
                result.setDone(next.getCommand().invoke());
            } catch(Throwable ex) {
                result.setDone(ex);
            }
        } 
    }
}
