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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import gil.common.AsyncResult;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class AsyncResultTest {

    public AsyncResultTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void expect_result_from_blocking_get_when_second_thread_reports_done() throws Exception {
        final AsyncResult<Object> result = new AsyncResult<Object>();
        runSecondThreadAndReportDone(result);
        assertEquals("is done", result.get());
    }

    @Test
    public void expect_result_from_blocking_get_when_second_thread_reports_done_and_wait_is_within_timeout_limits() throws Exception {
        final AsyncResult<Object> result = new AsyncResult<Object>();
        runSecondThreadAndReportDone(result);
        assertEquals("is done", result.get(50, TimeUnit.MILLISECONDS));
    }

    @Test
    public void expect_cancel_to_return_false_when_already_completed() throws Exception {
        final AsyncResult<Object> result = new AsyncResult<Object>();
        runSecondThreadAndReportDone(result);
        Thread.sleep(50);
        assertEquals(false, result.cancel(true));
    }

    @Test
    public void expect_cancel_to_return_true_when_cancelled_successfully() throws Exception {
        final AsyncResult<Object> result = new AsyncResult<Object>();
        assertEquals(true, result.cancel(true));
    }

    @Test(expected=ExecutionException.class)
    public void expect_exception_when_exception_was_thrown_during_invokation_of_the_underlying_operation() throws Exception {
        final AsyncResult<Object> result = new AsyncResult<Object>();
        runSecondThreadAndReportException(result, new Exception());
        result.get();
    }

    @Test(expected=CancellationException.class)
    public void expect_exception_when_calling_get_and_the_operation_has_been_cancelled() throws Exception {
        final AsyncResult<Object> result = new AsyncResult<Object>();
        runSecondThreadAndCancel(result);        
        result.get();
    }

    @Test(expected=TimeoutException.class)
    public void expect_exception_at_timeout_when_get_is_called() throws Exception {
        AsyncResult<Object> result = new AsyncResult<Object>();
        result.get(50, TimeUnit.MILLISECONDS);
    }

    private void runSecondThreadAndReportDone(final AsyncResult<Object> result) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(25);
                    result.setDone("is done");
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    private void runSecondThreadAndReportException(final AsyncResult<Object> result, final Throwable ex) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(25);
                    result.setDone(ex);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    private void runSecondThreadAndCancel(final AsyncResult<Object> result) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(25);
                    result.cancel(true);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

}