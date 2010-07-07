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

import gil.common.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class TimeoutTest {

    public TimeoutTest() {
    }

    @Test
    public void expect_isTimeout_to_return_true_when_timeout_is_met() {
        Timeout instance = new Timeout(1000, 40);
        assertFalse(instance.isTimeout(1039));
        assertTrue(instance.isTimeout(1040));
    }

    @Test
    public void expect_isTimeout_when_current_time_is_less_than_start_time_offset() {
        Timeout instance = new Timeout(1000, 40);
        assertFalse(instance.isTimeout(1000));
        assertTrue(instance.isTimeout(999));
    }

    @Test
    public void expect_compensation_for_actual_elapsed_time_when_reschedule_is_called() {
        Timeout instance = new Timeout(1000, 40);        
        assertTrue(instance.isTimeout(1050));
        instance.reschedule(1050);
        assertFalse(instance.isTimeout(1079));
        assertTrue(instance.isTimeout(1080));
    }

    @Test
    public void expect_always_rescheduling_even_when_timeout_has_not_occurred() {
        Timeout instance = new Timeout(1000, 40);

        instance.reset(1000);
        assertEquals(0, instance.reschedule(1000));
        assertFalse(instance.isTimeout(1039));
        assertTrue(instance.isTimeout(1040));

        instance.reset(1000);
        assertEquals(0, instance.reschedule(1001));
        assertFalse(instance.isTimeout(1079));
        assertTrue(instance.isTimeout(1080));
    }

    @Test
    public void expect_rescheduling_to_currentTime_plus_interval_when_current_time_is_less_than_startTimeOffset() {
        Timeout instance = new Timeout(1000, 40);

        assertEquals(0, instance.reschedule(961));
        assertFalse(instance.isTimeout(1000));
        assertTrue(instance.isTimeout(1001));

        instance.reset(1000);
        assertEquals(0, instance.reschedule(999));
        assertFalse(instance.isTimeout(1038));
        assertTrue(instance.isTimeout(1039));
        
        instance.reset(1000);
        assertEquals(-4, instance.reschedule(810));
        assertFalse(instance.isTimeout(849));
        assertTrue(instance.isTimeout(850));
    }

    @Test
    public void expect_number_of_timeout_intervals_when_rescheduling() {
        Timeout instance = new Timeout(1000, 40);
       
        instance.reset(1000);
        assertEquals(1, instance.reschedule(1040));
        assertFalse(instance.isTimeout(1079));
        assertTrue(instance.isTimeout(1080));

        instance.reset(1000);
        assertEquals(1, instance.reschedule(1079));
        assertFalse(instance.isTimeout(1079));
        assertTrue(instance.isTimeout(1080));
        
        instance.reset(1000);
        assertEquals(2, instance.reschedule(1080));
        assertFalse(instance.isTimeout(1119));
        assertTrue(instance.isTimeout(1120));

        instance.reset(1000);
        assertEquals(2, instance.reschedule(1119));
        assertFalse(instance.isTimeout(1119));
        assertTrue(instance.isTimeout(1120));

        instance.reset(1000);
        assertEquals(3, instance.reschedule(1120));
        assertFalse(instance.isTimeout(1159));
        assertTrue(instance.isTimeout(1160));
    }

    @Test
    public void expect_rescheduling_to_return_zero_when_timeout_interval_is_set_to_zero() {
        Timeout instance = new Timeout(1000, 0);

        assertEquals(0, instance.reschedule(1000));
        assertFalse(instance.isTimeout(2000));
    }

    @Test
    public void expect_isTimeout_to_always_return_false_when_timeout_intervall_is_set_to_zero() {
        Timeout instance = new Timeout(1000, 0);
        assertFalse(instance.isTimeout(2000));
    }

    @Test
    public void expect_start_time_to_be_set_to_a_given_time_when_reset_is_called() {
        Timeout instance = new Timeout(40);
        instance.reset(2000);
        assertFalse(instance.isTimeout(2039));
        assertTrue(instance.isTimeout(2040));
    }

    @Test
    public void expect_forced_timeout_to_result_in_true_returned_when_isTimedout_is_called() {
        Timeout instance = new Timeout(40);
        instance.reset(2000);
        instance.forceTimeout();
        assertTrue(instance.isTimeout(0));
        assertFalse(instance.isTimeout(2039));
        assertTrue(instance.isTimeout(2040));
    }

    @Test
    public void expect_forced_timeout_to_result_in_true_eve_when_timer_is_disabled() {
        Timeout instance = new Timeout(0);
        instance.reset(2000);
        instance.forceTimeout();
        assertTrue(instance.isTimeout(0));
    }
}