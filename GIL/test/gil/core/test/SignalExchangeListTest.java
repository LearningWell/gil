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

import java.io.*;
import org.junit.*;
import gil.core.*;
import gil.common.InvalidFileFormatException;
import static org.junit.Assert.*;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class SignalExchangeListTest {

    static final String TEST_SEL_FILE_NAME = "testsel.txt";
    PrintWriter _writer;

    public SignalExchangeListTest() {
    }

    @Before
    public void setUp() throws Exception {
        _writer = new PrintWriter(TEST_SEL_FILE_NAME);
    }

    @After
    public void tearDown() {
        _writer.close();
        File file = new File(TEST_SEL_FILE_NAME);
        file.delete();
    }

    @Test
    public void expect_single_row_to_be_parsed() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
         + "Analog Float64 25 ToPM a_pm_ID an_es_ID a_discription_of_the_signal";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(1, sm.length);
        assertEquals(SignalMetadata.SignalType.Analog, sm[0].getType());
        assertEquals(SignalMetadata.SignalDataType.Float64, sm[0].getDataType());
        assertEquals(25, sm[0].getLength());
        assertEquals(SignalMetadata.DataflowDirection.ToPM, sm[0].getDataflowDirection());
        assertEquals("a_pm_ID", sm[0].getPMSignalID());
        assertEquals("an_es_ID", sm[0].getESSignalID());
        assertEquals("a_discription_of_the_signal", sm[0].getDescription());
    }
   
    @Test
    public void expect_single_row_with_trailing_eol_to_be_parsed() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "Analog Float64 1 ToPM a_pm_ID an_es_ID a_discription_of_the_signal\n   \r\n \n  \r \r";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(1, sm.length);
        assertEquals(SignalMetadata.SignalType.Analog, sm[0].getType());
        assertEquals("a_discription_of_the_signal", sm[0].getDescription());
    }

    @Test
    public void expect_row_with_mixed_chars_in_attributes_to_be_parsed() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "Analog  Float64 1  ToPM \t  123a;.-+()/ an_es_ID 718";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(1, sm.length);
        assertEquals(SignalMetadata.SignalType.Analog, sm[0].getType());
        assertEquals("123a;.-+()/", sm[0].getPMSignalID());
        assertEquals("718", sm[0].getDescription());
    }

    @Test
    public void expect_custom_attributes_to_be_parsed() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description customAttr1 customAttr2\n"
                + "Analog  Float64  1 ToPM  idp ide desc caVal1 caVal2";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata sm[] = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(1, sm.length);
        assertEquals("caVal1", sm[0].getCustomAttributes().get("customAttr1"));
        assertEquals("caVal2", sm[0].getCustomAttributes().get("customAttr2"));
    }

    @Test
    public void expect_custom_attributes_to_be_skipped_conditionally() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description customAttr1 customAttr2\n"
                + "Analog  Float64   1   ToPM  idp ide desc caVal1 caVal2\n"
                + "Analog  Float64   1   ToPM  idp ide desc -- --\n"
                + "Analog  Float64   1   ToPM  idp ide desc -- caVal4\n";
        
        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(3, sm.length);
        assertEquals(2, sm[0].getCustomAttributes().size());
        assertEquals("caVal1", sm[0].getCustomAttributes().get("customAttr1"));
        assertEquals("caVal2", sm[0].getCustomAttributes().get("customAttr2"));

        assertEquals(0, sm[1].getCustomAttributes().size());
        assertEquals(1, sm[2].getCustomAttributes().size());
        assertEquals("caVal4", sm[2].getCustomAttributes().get("customAttr2"));
    }

    @Test
    public void expect_quoted_strings_to_be_parsed() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "Analog Float64 1 ToPM a_pm_ID an_es_ID \"A quoted description\"   ";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(1, sm.length);
        assertEquals(SignalMetadata.SignalType.Analog, sm[0].getType());
        assertEquals("A quoted description", sm[0].getDescription());
    }

    @Test
    public void expect_multiple_rows_to_be_parsed() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                   +      "Analog Float64 1 ToPM a_pm_ID an_es_ID row1\r\n"
                   +      "Digital Int8 1 ToES a_pm_ID an_es_ID row2\r"
                   +      "Digital Int16 1 ToES a_pm_ID an_es_ID row3\n"
                   +      "Digital Int32 1 ToES a_pm_ID an_es_ID row4";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(4, sm.length);
        assertEquals("row1", sm[0].getDescription());
        assertEquals("row4", sm[3].getDescription());
    }

    @Test
    public void expect_bufferPos_to_be_set() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                   +      "Analog Float64 1 ToPM a_pm_ID an_es_ID row1\r\n"
                   +      "Digital Int8 1 ToES a_pm_ID an_es_ID row2\r"
                   +      "Digital Int16 2 ToES a_pm_ID an_es_ID row3\n"
                   +      "Digital Int32 1 ToES a_pm_ID an_es_ID row4\n"
                   +      "Analog  Int32 1 ToPM a_pm_ID an_es_ID row5";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);
        
        assertEquals(0, sm[0].getBufferPos());
        assertEquals(0, sm[1].getBufferPos());
        assertEquals(1, sm[2].getBufferPos());
        assertEquals(5, sm[3].getBufferPos());
        assertEquals(8, sm[4].getBufferPos());
    }


    @Test
    public void expect_empty_rows_to_be_ignored() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "\n\nType DataType Length Direction PMID ESID Description\n"
                   +      "Analog Float64 1 ToPM a_pm_ID an_es_ID row1\r\n\n\n"
                   +      "Digital Int8 1 ToES a_pm_ID an_es_ID row2";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(2, sm.length);
        assertEquals("row1", sm[0].getDescription());
        assertEquals("row2", sm[1].getDescription());
    }

    @Test
    public void expect_out_commented_rows_to_be_ignored() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                   +      "Analog Float64 1 ToPM a_pm_ID an_es_ID row1\r\n"
                   +      "!Digital Int8 1 ToES a_pm_ID an_es_ID row2\r"
                   +      "!Digital Int16 1 ToES a_pm_ID an_es_ID row3\n"
                   +      "Digital Int32 1 ToES a_pm_ID an_es_ID row4";

        _writer.print(testdata);
        _writer.close();
        SignalMetadata[] sm = SignalExchangeList.parse(TEST_SEL_FILE_NAME);

        assertEquals(2, sm.length);
        assertEquals("row1", sm[0].getDescription());
        assertEquals("row4", sm[1].getDescription());
    }

    //----------------------------------------------------------------------------------
    // Test of exceptional conditions
    //----------------------------------------------------------------------------------
    
    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_missing_column_in_header() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID\n"
                + "Analog Float64 1 ToPM pmID esID desc";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_illegal_row() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "ldskfj dsfsd";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_custom_attribute_is_missing() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description customAttr1 customAttr2\n"
                + "Analog  Float64  1 ToPM  idp ide desc caVal1 ";

        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_invalid_SignalType_attribute() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "AnaloG Float64 1 ToPM pmID esID desc";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_invalid_SignalDataType_attribute() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "Analog FloaT64 1 ToPM pmID esID desc";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_invalid_Length_attribute() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "Analog Float64 A ToPM pmID esID desc";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_invalid_DataflowDirection_attribute() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID Description\n"
                + "Analog Float64 1 TOPM pmID esID desc";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_invalid_Type_header_label() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "TypE DataType Length Direction PMID ESID Description\n";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }

    @Test(expected=InvalidFileFormatException.class)
    public void expect_exception_when_invalid_Decription_header_label() throws Exception {

        // SignalType, SignalDataType, DataflowDirection, pmSignalID, esSignalID, _description
        String testdata = "Type DataType Length Direction PMID ESID DescriptioN\n";
        _writer.print(testdata);
        _writer.close();
        SignalExchangeList.parse(TEST_SEL_FILE_NAME);
    }
}