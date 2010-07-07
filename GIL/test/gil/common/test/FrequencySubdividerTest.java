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
import gil.core.SimTime;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class FrequencySubdividerTest {

    public FrequencySubdividerTest() {
    }

    @Test
    public void test_20Hz_To_10Hz_subdivision() {
        FrequencySubdivider instance = new FrequencySubdivider(10);
        instance.reset(new SimTime(2009, 8, 22, 1, 1, 1, 0));

        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 0)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 50)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 100)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 150)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 200)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 250)));
        assertEquals(2, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 400)));
        assertEquals(5, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 950)));
        
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 2, 0)));
    }

    @Test
    public void test_20Hz_To_10Hz_subdivision_Test2() {
        System.out.println("getSystem2TimeStepChange");
        FrequencySubdivider instance = new FrequencySubdivider(10);
        instance.reset(new SimTime(2009, 1, 1, 1, 1, 1, 0));

        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 50)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 100)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 150)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 200)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 250)));
        assertEquals(2, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 400)));
    }

    @Test
    public void test_10Hz_To_5Hz_subdivision() {
        FrequencySubdivider instance = new FrequencySubdivider(5);
        instance.reset(new SimTime(2009, 1, 1, 1, 1, 1, 0));

        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 199)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 200)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 399)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 1, 1, 1, 1, 1, 400)));
    }

    @Test
    public void test_16Hz_To_10Hz_subdivision() {
        FrequencySubdivider instance = new FrequencySubdivider(10);
        instance.reset(new SimTime(2009, 8, 22, 1, 1, 1, 0));

        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 0)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 62)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 125)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 187)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 250)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 312)));
        assertEquals(0, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 375)));
        assertEquals(1, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 437)));
        assertEquals(4, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 1, 812)));
        assertEquals(2, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 2, 0)));
        assertEquals(19, instance.getSystem2TimeStepChange(new SimTime(2009, 8, 22, 1, 1, 3, 999)));

    }

    @Test
    public void test_day_roll_over() {
        SimTime day1 = new SimTime(2009, 8, 22, 23, 59, 59, 0);
        SimTime day2 = new SimTime(2009, 8, 23, 0, 0, 0, 0);
        FrequencySubdivider instance = new FrequencySubdivider(10);
        instance.reset(day1);

        assertEquals(10, instance.getSystem2TimeStepChange(day2));
    }

    @Test
    public void expect_system2_time_reset_when_delta_change_is_to_big() {
        SimTime t1 = new SimTime(2009, 8, 22, 10, 0, 0, 0);
        SimTime t2 = new SimTime(2009, 8, 22, 10, 0, 2, 99);
        FrequencySubdivider instance = new FrequencySubdivider(10);
        instance.reset(t1);
        assertEquals(20, instance.getSystem2TimeStepChange(t2));

        instance.reset(t1);
        t2 = new SimTime(2009, 8, 22, 10, 0, 2, 100);
        assertEquals(0, instance.getSystem2TimeStepChange(t2));
        t2 = new SimTime(2009, 8, 22, 10, 0, 2, 200);
        assertEquals(1, instance.getSystem2TimeStepChange(t2));
    }
}