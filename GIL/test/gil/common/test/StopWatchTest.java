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

import gil.common.CurrentTime;
import gil.common.ICurrentTime;
import gil.common.StopWatch;
import org.junit.Test;
import static org.junit.Assert.*;

public class StopWatchTest {

    long _currentTime = 0;
   
    @Test
    public void testStopwWatch() {
        CurrentTime.setInstanceForTestPurposes(new ICurrentTime() {
            public long inMilliseconds() {
                return _currentTime;
            }
        });
        StopWatch sw = new StopWatch();
        _currentTime = 1000;
        sw.start();
        _currentTime = 1010;
        assertEquals(10, sw.getElapsedMilliseconds());
        _currentTime = 1020;
        assertEquals(20, sw.getElapsedMilliseconds());
        sw.stop();
        _currentTime = 1030;
        assertEquals(20, sw.getElapsedMilliseconds());
        sw.start();
        _currentTime = 1040;
        assertEquals(10, sw.getElapsedMilliseconds());
    }
}