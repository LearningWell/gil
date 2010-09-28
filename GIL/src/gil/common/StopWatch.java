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
 * Class to be used as a stopwatch for time measurements.
 * @author Göran Larsson @ LearningWell AB
 */
public class StopWatch {

    private long _startTime = 0;
    private long _stopTime = 0;
    private boolean _running = false;

    public void start() {
        _startTime = CurrentTime.instance().inMilliseconds();
        _running = true;
    }

    public void stop() {
        _stopTime = CurrentTime.instance().inMilliseconds();
        _running = false;
    }

    public long getElapsedMilliseconds() {
        if (_running) {
             return (CurrentTime.instance().inMilliseconds() - _startTime);
        }
        else {
            return (_stopTime - _startTime);
        }
    }
}
