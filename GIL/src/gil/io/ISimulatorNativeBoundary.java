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
package gil.io;

import java.nio.ByteBuffer;
import java.util.Map;
import gil.core.SignalMetadata;

/**
 * Interface that may be used as a template when there is a need to implement part of the process model adapter in a
 * different language than Java.
 * @author Göran Larsson @ LearningWell AB
 */
public interface ISimulatorNativeBoundary {

    /**
     * Defines list of signals read and written with readData and writeData.
     * @return 0 on success and -1 on failure.
     */
    int setup(SignalMetadata[] writeSignals, SignalMetadata[] readSignals, Map<String, String> config);

    /**
     * Gives the native code a chance to do cleanup.
     * Must be called last to do cleanup. A single call to tearDown is enough even if setup has been
     * called several times.
     */
    void tearDown();

    /**
     * Reads data from the process model. The given buffer must be of a size enough to receive signal data according
     * to the readSignals setup. Signal data is returned in buffer in the same order as signal metadata given when
     * calling setup.
     *
     * @return 0 on success and -1 on failure
     */
    int readData(ByteBuffer buffer);

    /**
     * Writes data to the process model. The given buffer must be of a size according
     * to the writeSignals setup. Signal data must be in the same order as signal metadata given when
     * calling setup.
     * @return 0 on success and -1 on failure
     */
    int writeData(ByteBuffer buffer);

    /**
     * Returns the systems variables.
     * <p>
     * The buffer will after the call contain variables such as clock, status, icNumber etc. depending on the
     * concrete implementation of this interface.
     * @return 0 on success and -1 on failure
     */
    int getSystemVariables(ByteBuffer buffer);
}
