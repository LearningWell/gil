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

import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;
import gil.core.Command;
import gil.core.SimTime;

public class CommandTest {

    public CommandTest() {
    }

    @Test
    public void testToString() {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("p1", "v1");
                put("p2", "v2");
            }
        };

        Command instance = new Command("cmd", params, new SimTime(2006, 6, 12, 23, 59, 1, 0));
        assertEquals("2006-06-12 23:59:01.000 id=cmd parameters={p2=v2, p1=v1}", instance.toString());
    }

    @Test
    public void testIsCommand() {
        HashMap<String, String> params = new HashMap<String, String>() {

            {
                put("p1", "v1");
                put("p2", "v2");
            }
        };
        Command cmd = new Command("aCmd", params, null);

        assertTrue(cmd.isCommand("aCmd"));
        assertFalse(cmd.isCommand("acmd"));
    }

    @Test
    public void expect_constructor_to_initialize_fields() {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("p1", "v1");
                put("p2", "v2");
            }
        };
        Command cmd = new Command("aCmd", params, new SimTime(2009, 12, 23, 14, 1, 22, 100));

        assertEquals("aCmd", cmd.getID());
        assertEquals(2, cmd.getParameters().size());
        assertEquals("v2", cmd.getParameters().get("p2"));
        assertEquals(2009, cmd.getOrigin().year());
    }

    @Test(expected = RuntimeException.class)
    public void expect_runtime_exception_if_parameter_does_not_exist_when_getParameter_is_called() {
        HashMap<String, String> params = new HashMap<String, String>() {

            {
                put("p1", "v1");
            }
        };
        Command cmd = new Command("aCmd", params, null);

        cmd.getParameter("dummy");
    }

    @Test
    public void expect_parameter_value_when_getParameter_is_called() {
        HashMap<String, String> params = new HashMap<String, String>() {

            {
                put("p1", "v1");
            }
        };
        Command cmd = new Command("aCmd", params, null);

        assertEquals("v1", cmd.getParameter("p1"));
    }

    @Test
    public void expect_boolean_value_when_parameterExists_is_called() {
        HashMap<String, String> params = new HashMap<String, String>() {

            {
                put("p1", "v1");
                put("p2", "v2");
            }
        };
        Command cmd = new Command("aCmd", params, null);

        assertTrue(cmd.parameterExists("p2"));
        assertFalse(cmd.parameterExists("dummy"));
    }
}
