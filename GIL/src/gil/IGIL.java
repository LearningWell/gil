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
package gil;

import java.io.FileNotFoundException;
import java.io.InputStream;
import gil.GIL.VersionInfo;
import gil.core.IntegrationExecutive;
import gil.core.LogfileMetadata;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
public interface IGIL {

    IntegrationExecutive getExecutive();

    InputStream getLogfile(String name) throws FileNotFoundException;

    LogfileMetadata[] getLogfileInfo();

    VersionInfo getVersionInfo();

    /**
     * Starts the GIL runtime.
     */
    void start(int port) throws Exception;

    void stop() throws Exception;
}
