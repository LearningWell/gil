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

import gil.common.IProgressEventListener;
import gil.common.ValueResult;
import java.io.IOException;
import java.nio.ByteBuffer;
import gil.core.CommandDescriptor;
import gil.common.InvalidParameterException;
import gil.common.Parameters;
import gil.core.SignalMetadata;
import gil.core.Command;
import gil.core.SimTime;
import gil.core.SoftwareInfo;
import gil.core.SystemStatus;
import gil.common.Result;
import gil.core.SimState;

/**
 * The common interface to be implemented for every new type of process model to be connected to GIL.
 * <p>
 * The implementor of this interface does not have to take concurrency into consideration. Operations
 * on this interface must only be called from a single thread.
 * <p>
 * @author Göran Larsson @ LearningWell AB
 */
public interface IProcessModelAdapter extends IControlCommandInvokeable {


    /**
     * Does the initial setup of the adapter.
     * <p>
     * Sets up a sequence of signals to be read and written using the {@link #readSignalData(java.nio.ByteBuffer) readSignalData()}
     * and {@link #writeSignalData(java.nio.ByteBuffer) writeSignalData()} respectively.
     * @param signalsToPM An array of signal metadata specifying the signal values to be transferred from the
     * external system to the process model.
     * @param signalsToES An array of signal metadata specifying the signal values to be transferred from the
     * process model to the external system.
     * @param config A collection of key-, value pairs holding configuration parameters specific to
     * each implementation of {@link IProcessModelAdapter IProcessModelAdapter}. Key-, value pairs are
     * wrapped  in a {@link Parameters Parameters} class for convenience.
     * @throws IOException Is thrown to force the caller to an error state that must be handled by
     * manual intervention.
     * @throws InvalidParameterException Thrown if any parameter is illegal.
     */
    void setup(SignalMetadata[] signalsToPM, SignalMetadata[] signalsToES, Parameters config) throws InvalidParameterException;

     /**
      * Does the cleanup before close down.
      * <p>
      * Call this method before application is closed down. The method may be called multiple times.
      */
     void tearDown();

    /**
     * Connects to the process model.
     * <p>
     * This operation may block for a longer period of time since several connect attempts may be done by the
     * current implementation of the PM-adapter. An {@link java.io.IOException IOException} is thrown if there is a
     * serious problem preventing a connection to be established.
     * @return Returns true if connected otherwise false. Please note that false is not indicating a failure. The
     * caller will call this method repeatedly until true is returned or an exception thrown.
     * @throws IOException Is thrown to force the caller to an error state that must be handled by
     * manual intervention.
     */
    boolean connect() throws IOException;

    /**
     * Disconnects from the process model.
     * <p>
     * The call is just ignored if already disconnected.
     */
    void disconnect();

    /**
     * Reads process data.
     * <p>
     * Reads values, of the previously setup sequence of signals, from the process model.
     * @param destBuf If data is available this buffer will be populated with signal values. The number of signal
     * values and their data type must correspond to the sequence of signal metadata given in a previous call to
     * {@link #setup(gil.core.SignalMetadata[], gil.core.SignalMetadata[], gil.common.Parameters) setup()}.
     * The given ByteBuffer has memory allocated just enough to contain the expected signal values.
     * <p>
     * When this operation is called, the given ByteBuffer has a preset byte order (endianness). The caller
     * expects data to be added in that byte order. This has to be taken into consideration only if the byte array that
     * backs the ByteBuffer is accessed directly. Using put methods, i.e. putFloat, putInt, putShort etc. is safe.
     * @return Null or a result object indicating success or failure. When there is no data available null is returned.
     * A failure will indicate to the caller that this read was unsuccessful but subsequent reads may succeed.
     * An IOException shall be thrown for unrecoverable serious failures. On success the result object will
     * contain the time from which the data originates.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * process model and reconnect.
     */
    ValueResult<SimTime> readSignalData(ByteBuffer destBuf) throws IOException;

    /**
     * Writes process data.
     * <p>
     * Writes values, of the previously setup sequence of signals, to the process model.
     * @param values A buffer containing the sequence of signal values to be written to the process model.
     * The number of signal values and their data type must correspond to the sequence of signal metadata given in
     * a previous call to {@link #setup(gil.core.SignalMetadata[], gil.core.SignalMetadata[], gil.common.Parameters) setup()}.
     * @param origin The time from when the data originates.
     * @return A result object indicating success or failure. A failure will indicate to the caller that
     * this write was unsuccessful but subsequent writes may succeed. The caller shall not retry to rewrite
     * the same signal values. However, the caller can continue to call this method for new data.
     * An IOException is thrown for unrecoverable serious failures.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * process model and reconnect.
     */
    Result writeSignalData(ByteBuffer values, SimTime origin) throws IOException;

    /**
     * Returns the current date and time of the process model simulation clock. The returned time will be used by the
     * caller to detect frame changes in the process model and transfer the simulation time to the external system.
     * @return A result object indicating success or failure. When success, the result object contains the
     * simulator time.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * process model and reconnect.
     */
    ValueResult<SimTime> getSimTime() throws IOException;

    /**
     * Returns the process model operating frequency in Hertz.
     * <p>
     * @return the process model operating frequency in Hertz.
     */
    int getOperatingFrequency();
    
    /**
     * Returns simulator commands that have occurred since the last call to this operation.
     * <p>
     * The returned ValueResult object indicates failure if the read is unsuccessful. Failure indicated by the
     * ValueResult object will tell the caller that this call was unsuccessful but subsequent calls may succeed.
     * When the failure is major, an IOException is thrown which shall cause the caller to call disconnect
     * followed by a reconnect.
     * @return a ValueResult object containing an array of commands with the command that have occurred since
     * the last call to this operation.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * process model and reconnect.
     */
    ValueResult<Command[]> readSimCommands() throws IOException;

    /**
     * Returns software info such as version info.
     * <p>
     * Returns information about this implementation of the {@link IProcessModelAdapter IProcessModelAdapter}
     * interface, the process model and possibly about connected subsystems.
     * @return Returns the software info structure for the current implementation of this interface and connected
     * subsystems. Index 0 contains the software info structure for the implementation of this interface. If
     * available; subsequent indexes contains information about the process model and subsystems thereof.
     */
    SoftwareInfo[] getInfo();


    /**
     * Returns the system status of the process model.
     * <p>
     * This gives an indication if the process model is OK or not OK.
     * It may not be possible to detect the current status of the process model and in that case
     * {@link SystemStatus SystemStatus.NOT_AVAILABLE} shall be returned.
     * <p>
     * @return The current status of the process model.
     */
     SystemStatus getStatus();


    /**
     * Returns the current operating state. e.g. run, freeze.
     * <p>
     * States only has a meaning if the current status returned from {@link #getStatus() getStatus()} is OK.
     * Common states are defined in {@link SimState SimState}.
     * <p>
     * @return The current state e.g. running, freeze.
     */
     int getState();

    /**
     * Adds a listener to receive progress change notifications.
     * <p>
     * @param listener The listener that subscibes for notifications when progress changes. The listeners must
     * handle concurrency since they may be called from separate threads.
     * @return true if the implemantation supports progress change notifications and the caller
     * can expect to receive notifications. Returns false when progress change notifications are not supported.
     */
     boolean addProgressChangeListener(IProgressEventListener listener);

    /**
     * Returns a list of control commands that can be invoked from client software.
     * <p>
     * The implementation of the {@link IProcessModelAdapter IProcessModelAdapter} interface may support a set
     * of control commands that can be invoked to control the behavior of the adapter. This method lists the available
     * commands. Commands listed may be invoked by calling
     * {@link #invokeControlCommand(java.lang.String, java.lang.String) invokeControlCommand}.
     * @return An array holding the specification for each available command. If no commands are available, an array
     * containing zero elements is returned.
     */
    CommandDescriptor[] availableControlCommands();
}
