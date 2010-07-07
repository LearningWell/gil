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
package gil.core;

import gil.common.InvalidFileFormatException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.*;

/**
 * This class handles parsing of Signal Exchange List files (SEL). The SEL contains a header and subsequent lines
 * of signal metadata. The header consists of a minimum of seven columns, which must be present in the top of the file.
 * Example:
 * Type DataType Length Direction PMID ESID Description
 *
 * Additional colums may be added as so called custom attributes.
 * Example:
 * Type DataType Length Direction PMID ESID Description MaxValue MinValue
 *
 * MaxValues and MinValue are custom attributes. Custom attributes vill be contained in the
 * SignalMetadata.CustomAttributes map.
 *
 * Subsequent signal metadata lines must comprise the same numbers of colums as the header. Colums are separated
 * by blank characters such as spaces or tabs. Column entries containing blanks must be enclosed in quotation
 * marks e.g. "An entry with spaces".
 *
 * You can avoid setting values for custom attributes by entering --.
 * Example:
 * Type   DataType Length Direction PMID  ESID   Description  MaxValue MinValue
 * Analog Float32  1      ToES      anId1 anID2  "a descr"    --       --
 *
 * Exclamation mark ! is used for single line comments
 *
 * Example:
 * ! This line is a comment ignored by the parser
 *
 * The actual file format is rigorously checked and an InvalidFileFormatException i thrown if the format is not
 * acceptable.
 * 
 * @author Göran Larsson @ LearningWell AB
 */
public class SignalExchangeList {
    public static SignalMetadata[] parse(String selFileName) throws FileNotFoundException, IOException {
        FileReader rd = new FileReader(selFileName);
        try {
        
            StreamTokenizer st = new StreamTokenizer(rd);

            st.resetSyntax();
            st.wordChars('#', 255);
            st.eolIsSignificant(true);
            st.whitespaceChars(0, ' ');
            st.quoteChar('\"');
            st.commentChar('!');


            List<String> columns = new ArrayList<String>();
            List<SignalMetadata> signalMetadata = new ArrayList<SignalMetadata>();
            LineParser lineParser = new LineParser();

            int token = st.nextToken();
            while (token != StreamTokenizer.TT_EOF) {
                String str;
                switch (token) {
                    case '"':
                    case StreamTokenizer.TT_WORD:
                        str = st.sval;
                        columns.add(str);
                        break;
                    case StreamTokenizer.TT_EOL:
                        SignalMetadata md = lineParser.parse(columns, st.lineno());
                        if (md != null) {
                            signalMetadata.add(md);
                        }
                        columns.clear();
                    break;
                }
                token = st.nextToken();
            }

            SignalMetadata md = lineParser.parse(columns, st.lineno());
            if (md != null) {
                signalMetadata.add(md);
            }
        
            return signalMetadata.toArray(new SignalMetadata[0]);
        }
        finally {
            rd.close();
        }
    }


    /**
     * A signal exchange list contains one header and several lines of signal metadata. This class handles parsing
     * of individual lines, both header and signal metadata. It knows if to expect header or signal metadata when
     * the call to parse is done.
     */
    private static class LineParser {
        static final int STANDARD_ATTRIBUTE_COUNT = 7;
        private List<String> customAttributes = new ArrayList<String>();
        private boolean expectingHeader = true;

        /**
         * On first call this method expects column headers. The count of columns in the header is checked against
         * number of columns in subsequent calls. An InvalidFileFormatException is thrown if there is a mismatch.
         *
         * @return Signal metadata on successful parse of the given columns.
         * null is returned when parsing the column header. null is also returned if the given columns list
         * contains no elements.
         */
        public SignalMetadata parse(List<String> columns, int lineNo) throws InvalidFileFormatException {
            if (columns.size() > 0)
            {
                if (expectingHeader) {
                    parseHeader(columns);
                    expectingHeader = false;
                }
                else {
                    return parseRow(columns, lineNo);
                }
            }
            return null;
        }

        private void parseHeader(List<String> columns) throws InvalidFileFormatException {
            //Type DataType Direction PMID ESID Description
            if (columns.size() < STANDARD_ATTRIBUTE_COUNT) {
                throw new InvalidFileFormatException("Expected minimum " + STANDARD_ATTRIBUTE_COUNT
                        + " columns in header, only found " + columns.size() + '.');
            }
            checkColumnHeader("Type", columns.get(0), 1);
            checkColumnHeader("DataType", columns.get(1), 2);
            checkColumnHeader("Length", columns.get(2), 3);
            checkColumnHeader("Direction", columns.get(3), 4);
            checkColumnHeader("PMID", columns.get(4), 5);
            checkColumnHeader("ESID", columns.get(5), 6);
            checkColumnHeader("Description", columns.get(6), 7);

            for (int i = STANDARD_ATTRIBUTE_COUNT; i < columns.size(); i++) {
                customAttributes.add(columns.get(i));
            }
        }

        private void checkColumnHeader(String expected, String actual, int column) throws InvalidFileFormatException {
            if (!actual.equals(expected)) {
                throw new InvalidFileFormatException("In signal exchange list file: '" + actual + "' is not a valid header for column " + column + ". "
                        + "Expected '" + expected + "'.");
            }
        }

        private SignalMetadata parseRow(List<String> columns, int currentLine) throws InvalidFileFormatException {

            if (columns.size() < (STANDARD_ATTRIBUTE_COUNT + customAttributes.size())) {
                throw new InvalidFileFormatException("Expected " + (STANDARD_ATTRIBUTE_COUNT + customAttributes.size()) + " columns on line "
                        + currentLine + ", only found " + columns.size() + '.');
            }

            if (!getNamesOfEnum(SignalMetadata.SignalType.class).contains(columns.get(0)))
                throw new InvalidFileFormatException(columns.get(0) + " is not a valid SignalType on line " + currentLine);
            if (!getNamesOfEnum(SignalMetadata.SignalDataType.class).contains(columns.get(1)))
                throw new InvalidFileFormatException(columns.get(1) + " is not a valid SignalDataType on line " + currentLine);
            try { Integer.parseInt(columns.get(2)); }
            catch(NumberFormatException ex) {
                throw new InvalidFileFormatException(columns.get(2) + " is not a valid Length on line " + currentLine);
            }
            if (!getNamesOfEnum(SignalMetadata.DataflowDirection.class).contains(columns.get(3)))
                throw new InvalidFileFormatException(columns.get(3) + " is not a valid DataflowDirection on line " + currentLine);

            HashMap<String, String> cu = new HashMap<String, String>();

            for (int i = 0; i < customAttributes.size(); i++) {
                String value = columns.get(STANDARD_ATTRIBUTE_COUNT + i);
                if (value.equals("--")) {
                    continue;
                }
                cu.put(customAttributes.get(i), value);
            }

            SignalMetadata md =
                new SignalMetadata(SignalMetadata.SignalType.valueOf(columns.get(0)),
                    SignalMetadata.SignalDataType.valueOf(columns.get(1)), Integer.parseInt(columns.get(2)),
                    SignalMetadata.DataflowDirection.valueOf(columns.get(3)),
                    columns.get(4), columns.get(5), columns.get(6), cu);
            return md;
        }

        private <T  extends Enum<T>> List<String> getNamesOfEnum(Class<T> enumClass) {

            List<String> names = new ArrayList<String>();

            T[] stypes = enumClass.getEnumConstants();
                for (T st : stypes) {
                    names.add(st.name());
                }
                return  names;
        }
    }
}
