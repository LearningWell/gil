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

import java.util.Date;

/**
 * @author Göran Larsson @ LearningWell AB
 */
public class LogfileMetadata {
    private long _sizeInBytes;
    private String _name;
    private Date _lastModified;

    public LogfileMetadata(String name, Date lastModified, long sizeInBytes) {
        _name = name;
        _lastModified = lastModified;
        _sizeInBytes = sizeInBytes;
    }

    public long getSizeInBytes() {
        return _sizeInBytes;
    }

    public String getName() {
        return _name;
    }

    public Date getLastModified() {
        return _lastModified;
    }
}
