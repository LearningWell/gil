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

import gil.common.XMLHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public class XMLHelpersTest {

    public XMLHelpersTest() {
    }
  
    @Test
    public void expect_DTDDocType_to_be_removed() {
        String str = "<?xml version=1.0?>A<!DOCTYPE <><> >B<SomeData>";
        assertEquals("<?xml version=1.0?>AB<SomeData>", XMLHelpers.removeDTDDocType(str));

        String str2 = "<?xml version=1.0?>A< !   DOCTYPE <><> >B<SomeData>";
        assertEquals("<?xml version=1.0?>AB<SomeData>", XMLHelpers.removeDTDDocType(str2));
    }
}