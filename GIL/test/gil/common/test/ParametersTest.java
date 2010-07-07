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

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import gil.common.InvalidParameterException;
import gil.common.Parameters;
import static org.junit.Assert.*;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class ParametersTest {

    private static final Map<String, String> _config = new HashMap<String, String>();
    static {
        _config.put("int", "123");
        _config.put("double", "123.567");
        _config.put("string", "aString");
    }
    Parameters _params = new Parameters(_config);

    public ParametersTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void expect_string_when_getParame_is_called() throws Exception {        
        assertEquals("aString", _params.getParam("string"));
    }

    @Test(expected=InvalidParameterException.class)
    public void expect_exception_when_parameter_does_not_exist_when_getParam_is_called() throws Exception {        
        _params.getParam("lasdkfj");
    }

    @Test
    public void expect_int_when_getIntParam_is_called() throws Exception {
        assertEquals(123, _params.getIntParam("int"));
    }

    @Test(expected=InvalidParameterException.class)
    public void expect_exception_when_invalid_int_when_getIntParam_is_called() throws Exception {
        _params.getIntParam("string");
    }

    @Test
    public void expect_double_when_getDoubleParam_is_called() throws Exception {
        assertEquals(123.567, _params.getDoubleParam("double"), 0.0001);
    }

    @Test(expected=InvalidParameterException.class)
    public void expect_exception_when_invalid_double_when_getDoubleParam_is_called() throws Exception {
        _params.getDoubleParam("string");
    }

}