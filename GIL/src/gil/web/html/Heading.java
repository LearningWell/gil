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
package gil.web.html;

/**
 * Represents the html &lt;h&gt; tag
 * @author Göran Larsson @ LearningWell AB
 */
public class Heading extends Tag {

    public static final int H1 = 1;
    public static final int H2 = 2;
    public static final int H3 = 3;
    public static final int H4 = 4;
    public static final int H5 = 5;
    public static final int H6 = 6;

    public Heading(String text, int level) {
        super("h" + Integer.toString(level));
        if (level < H1 || level > H6)
            throw new IllegalArgumentException("H1 <= level <= H6");

        super.setContent(new PlainText(text));
    }
}
