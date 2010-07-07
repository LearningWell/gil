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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Used to monitor the progress of a asynchronous operation and provide the results when the operation is done.
 * @author Göran Larsson @ LearningWell AB
 */
public class AsyncResult<V> implements Future<V> {

    private boolean _done = false;
    private boolean _cancelled = false;
    private V _resultValue = null;
    private Throwable _resultException = null;

    public boolean isDone() {
        return _done;
    }

    /**
     * Sets this AsyncResult to indicate that the underlying operation is done. 
     * @param resultValue
     */
    public void setDone(V resultValue) {
        synchronized(this) {
            _resultValue = resultValue;
            _done = true;
            this.notifyAll();
        }
    }

    /**
     * Sets this AsyncResult to indicate that the underlying operation was ended by an exception thrown.
     * @param exception
     */
    public void setDone(Throwable exception) {
        synchronized(this) {
            _resultException = exception;
            _done = true;
            this.notifyAll();
        }
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized(this) {
            if (_done) {
                return false;
            }
            _cancelled = true;
            _done = true;
            this.notifyAll();
            return true;
        }
    }

    public boolean isCancelled() {
        return _cancelled;
    }

    public V get() throws InterruptedException, ExecutionException {

        synchronized(this) {
            while(!_done) {
                this.wait();
            }
            if (_cancelled)
                throw new java.util.concurrent.CancellationException();
            if (_resultException != null)
                throw new ExecutionException(_resultException);

            return _resultValue;
        }        
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Timeout maxWait = new Timeout(System.currentTimeMillis(), unit.toMillis(timeout));
        synchronized(this) {
            while(!_done) {
                this.wait(unit.toMillis(timeout));
                if (maxWait.isTimeout(System.currentTimeMillis()))
                    throw new TimeoutException("Timed out when waiting for AsyncResult. Timeout was " + timeout + " " + unit.name() + ".");
            }
            if (_cancelled)
                throw new java.util.concurrent.CancellationException();
            if (_resultException != null)
                throw new ExecutionException(_resultException);

            return _resultValue;
        }        
    }
}
