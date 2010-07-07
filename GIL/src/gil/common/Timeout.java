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

/**
 * Encapsulates a start time and a timeout. Handles the logic of determining if a timeout has occurred given
 * the current time.
 * @author Göran Larsson @ LearningWell AB
 */
public class Timeout {

    private long _startTime;
    private long _timeoutInterval;
    private boolean _forcedTimeout = false;
    
    /**
     * Constructs a timeout object with the given timeout interval and offset time.
     * @param startTime The offset start time.
     * @param timeout The timeout interval. If set to zero the timout never occur 
     * (except if timeout is forced by call to {@link #forceTimeout()}.
     */
    public Timeout(long startTime, long timeout) {
        _startTime = startTime;
        _timeoutInterval = timeout;
    }

    /**
     * Constructs a timeout object with the given timeout interval.
     * @param timeout The timeout interval. If set to zero the timout never occur
     * (except if timeout is forced by call to {@link #forceTimeout()}.
     */
    public Timeout(long timeout) {
        _startTime = 0;
        _timeoutInterval = timeout;
    }

    /**
     * Returns true if the timeout is met.
     * Given the encapsulated start time and timeout interval; returns true if the timeout has been passed.
     * Also returns true if the given currentTime is less than the encapsulated startTime.
     */
    public boolean isTimeout(long currentTime) {
        if (_forcedTimeout) {
            _forcedTimeout = false;
            return true;
        }
        if (_timeoutInterval == 0)
            return false;        
        return ((currentTime - _startTime) >= _timeoutInterval) || (currentTime < _startTime);
    }

    /**
     * Resets the encapsulated start time to the given start time.
     * <p>
     * A timeout will occur at startTime + timeoutInterval.
     *
     */
    public void reset(long startTime) {
        _startTime = startTime;
    }

    /**
     * Forces this object unconditionally to a timeout state.
     * <p>
     * A subsequent call to to {@link #isTimeout(long) isTimeout} will unconditionally return true.
     */
    public void forceTimeout() {
        _forcedTimeout = true;
    }

    /**
     * Reschedules the timeout to the nearest multiple of the underlying timeout interval.
     * @param currentTime The current time as an arbitrary counter.
     * @return The numbers of elapsed timeout intervals since the last timeout.
     * <p>
     * E.g. Given that the timeout interval is set to 40 and last time out occurred at 1000:
     * <ul>

     * <li>reschedule(1040) returns 1 and the next timeout is rescheduled to 1080. </li>
     * <li>reschedule(1080) returns 2 and the next timeout is rescheduled to 1120. </li>
     * <li>reschedule(1039) returns 0 and the next timeout is rescheduled to 1080. </li>
     * <li>reschedule(1001) returns 0 and the next timeout is rescheduled to 1080. </li>
     * <li>reschedule(1000) returns 0 and the next timeout is rescheduled to 1040. </li>
     * <li>reschedule(999)  returns 0 and the next timeout is rescheduled to 1039. </li>
     * <li>reschedule(961)  returns 0 and the next timeout is rescheduled to 1001. </li>
     * <li>reschedule(810)  returns -4 and the next timeout is rescheduled to 850. </li>
     * </ul>
     */
    public long reschedule(long currentTime) {
        if (_timeoutInterval <= 0)
            return 0;

        long elapsedTime = currentTime - _startTime;
        long elapsedFrames = elapsedTime / _timeoutInterval;

        if (elapsedFrames > 0) {
            _startTime += elapsedFrames * _timeoutInterval;
            return elapsedFrames;
        }

        if (elapsedFrames == 0 && elapsedTime > 0) {
            _startTime += _timeoutInterval;
            return elapsedFrames;
        }

        _startTime = currentTime;
        return elapsedFrames;
    }
}
