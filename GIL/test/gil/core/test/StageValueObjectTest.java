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

import org.junit.Test;
import static org.junit.Assert.*;
import gil.core.CommandDescriptor;
import gil.core.StageValueObject;

public class StageValueObjectTest {

    public StageValueObjectTest() {
    }

    @Test
    public void expect_constructor_to_initialize_fields() {
        CommandDescriptor[] cd = new CommandDescriptor[0];

        StageValueObject vo = new StageValueObject(78, "aName", cd);
        assertEquals(78, vo.seqNo());
        assertEquals("aName", vo.name());
        assertSame(cd, vo.availableCommands());
    }
}