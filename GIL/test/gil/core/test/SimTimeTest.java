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

import gil.core.SimTime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class SimTimeTest {

    public SimTimeTest() {
    }

    @Test
    public void expect_toString_to_format_string() {

        SimTime testObject = new SimTime(2009, 11, 23, 14, 54, 21, 457);
        assertEquals("2009-11-23 14:54:21.457", testObject.toString());

        testObject = new SimTime(2009, 8, 7, 1, 4, 2, 3);
        assertEquals("2009-08-07 01:04:02.003", testObject.toString());
    }

    @Test
    public void expect_SimTime_to_be_created_from_string_when_min() throws Exception {

        SimTime testObject = SimTime.parse("2008-01-01 00:00:00.000");

        assertEquals(2008, testObject.year());
        assertEquals(1, testObject.month());
        assertEquals(1, testObject.dayOfMonth());
        assertEquals(0, testObject.hour());
        assertEquals(0, testObject.minute());
        assertEquals(0, testObject.second());
        assertEquals(0, testObject.millisecond());
    }

    @Test
    public void expect_SimTime_to_be_created_from_string_when_max() throws Exception {

        SimTime testObject = SimTime.parse("2008-12-31 23:59:59.999");

        assertEquals(2008, testObject.year());
        assertEquals(12, testObject.month());
        assertEquals(31, testObject.dayOfMonth());
        assertEquals(23, testObject.hour());
        assertEquals(59, testObject.minute());
        assertEquals(59, testObject.second());
        assertEquals(999, testObject.millisecond());
    }
}