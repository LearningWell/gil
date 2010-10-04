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

import java.nio.ByteBuffer;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class Data {
    private final SimTime _origin;
    private final ByteBuffer _data;

    public Data(ByteBuffer data, SimTime origin) {
        _data = data;
        _origin = origin;
    }

    public Data(ByteBuffer data) {
        _data = data;
        _origin = new SimTime();
    }

    public SimTime getOrigin() {
        return _origin;
    }

    public ByteBuffer getData() {
        return _data;
    }
}
