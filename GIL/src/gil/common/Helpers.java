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
package gil.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generic helpers.
 * @author Göran Larsson @ LearningWell AB
 */
public class Helpers {

    static final String[] FORMAT_SPECS = new String[] {".xml", ".json", ".html"};

    public static String dateTimeToString(Date d) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(d).toString();
    }

    /**
     * Removes ".xml", ".json"  or ".html" format specifiers from the given path, e.g.
     * "/resources.json/loggfiles.xml/test.bin" becomes "/resources/loggfiles/test.bin"
     * @param path A path string with a arbritrary numbers of format specifiers; ".json", ".xml" or ".html"
     * @return returns The path with the format specifiers removed.
     */
    public static String removeFormatSpecifierFromUriPath(String path) {
        StringBuilder sb = new StringBuilder(path);

        for (String format : FORMAT_SPECS) {
            int index = sb.indexOf(format);
            if (index >= 0) {
                sb.delete(index, index + format.length());
            }
        }
        
        String newPath = sb.toString();
        if (!newPath.equals(path)) {
            newPath = removeFormatSpecifierFromUriPath(newPath);
        }
        return newPath;
    }

    public static String retrieveFormatSpecifierFromUriPath(String path) {
        for (String format : FORMAT_SPECS) {
            if (path.indexOf(format) != -1)
                return format;
        }
        return "";
    }

    /**
     * Removes ".xml", ".json"  or ".html" format specifiers from the given uri, e.g.
     * "http://www.ksu.se/resources.json/loggfiles.xml/test.bin" becomes "http://www.ksu.se/resources/loggfiles/test.bin"
     * @param usi A usi string with a arbritrary numbers of format specifiers; ".json", ".xml" or ".html"
     * @return returns The usi with the format specifiers removed.
     */
    public static URI removeFormatSpecifierFromUri(URI uri) {
        try {
            String newPath = removeFormatSpecifierFromUriPath(uri.getPath());
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), newPath, uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    public static String retrieveFormatSpecifierFromUri(URI uri) {
        return retrieveFormatSpecifierFromUriPath(uri.getPath());
    }
}
