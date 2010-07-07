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
package gil.common.test;

import java.util.concurrent.ExecutionException;
import org.junit.Test;
import gil.common.AsyncResult;
import gil.common.IInvokeable;
import gil.common.Invoker;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class InvokerTest {

    public InvokerTest() {
    }

    @Test
    public void expect_empty_command_queue_not_to_fail_when_executeNextCommandInQueue_is_called() {
        Invoker invoker = new Invoker();
        invoker.executeNextCommandInQueue();
    }

    @Test
    public void expect_a_single_command_to_be_executed_when_executeNextCommandInQueue_is_called() throws Exception  {
        IInvokeable command = mock(IInvokeable.class);
        Invoker invoker = new Invoker();
        invoker.schedule(command);
        invoker.executeNextCommandInQueue();
        verify(command).invoke();
    }

    @Test
    public void expect_two_added_commands_to_be_executed_in_expected_order_when_executeNextCommandInQueue_is_called() throws Exception {
        IInvokeable command0 = mock(IInvokeable.class);
        IInvokeable command1 = mock(IInvokeable.class);
        Invoker invoker = new Invoker();
        invoker.schedule(command0);
        invoker.schedule(command1);

        // Expect command0 to be executed
        invoker.executeNextCommandInQueue();
        verify(command0).invoke();

        // Expect command1 to be executed
        invoker.executeNextCommandInQueue();
        verify(command1).invoke();
    }

    @Test
    public void expect_AsyncResult_to_be_done_when_command_has_been_completed() {
        IInvokeable command0 = mock(IInvokeable.class);
        IInvokeable command1 = mock(IInvokeable.class);
        Invoker invoker = new Invoker();
        AsyncResult res0 = invoker.schedule(command0);
        AsyncResult res1 = invoker.schedule(command1);

        assertFalse(res0.isDone());
        assertFalse(res1.isDone());

        invoker.executeNextCommandInQueue();
        invoker.executeNextCommandInQueue();

        assertTrue(res0.isDone());
        assertTrue(res1.isDone());
    }

    @Test(expected=ExecutionException.class)
    public void expect_AsyncResult_to_throw_exception_when_command_has_failed() throws Exception {
        IInvokeable command0 = mock(IInvokeable.class);
        Invoker invoker = new Invoker();
        AsyncResult res0 = invoker.schedule(command0);
        when(command0.invoke()).thenThrow(new Exception("dkjfh"));

        invoker.executeNextCommandInQueue();

        res0.get();
    }
}