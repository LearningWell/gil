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

import gil.common.ValueResult;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class ValueResultTest {

    public ValueResultTest() {
    }

    @Test
    public void expect_valueResult_object_to_contain_actual_value_object_and_return_true_on_isSuccess_call() {

        ValueResult<Integer> instance = new ValueResult<Integer>(100);

        assertEquals(100, (int)instance.getReturnValue());
        assertEquals("", instance.getErrorDescription());
        assertEquals(true, instance.isSuccess());
    }

    @Test
    public void expect_null_valueResult_object_and_false_on_isSuccess_call_on_failure() {

        ValueResult<Integer> instance = new ValueResult<Integer>("an error message");

        assertEquals(null, instance.getReturnValue());
        assertEquals("an error message", instance.getErrorDescription());
        assertEquals(false, instance.isSuccess());
    }
}