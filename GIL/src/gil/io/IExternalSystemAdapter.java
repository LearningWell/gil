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
import java.io.IOException;
import java.nio.ByteBuffer;
import gil.common.InvalidParameterException;
import gil.common.Parameters;
import gil.core.Command;
import gil.common.Result;
import gil.core.CommandDescriptor;
import gil.core.SignalMetadata;
import gil.core.SoftwareInfo;
import gil.core.SystemStatus;


/** 
 * The common interface to be implemented for every new type of external system to be integrated to the
 * simulator process models.
 * <p>
 * The implementor of this interface does not have to take concurrency into consideration. Operations
 * on this interface must only be called from a single thread.
 * <p>
 * @author Göran Larsson @ LearningWell AB
 */
public interface IExternalSystemAdapter extends IControlCommandInvokeable {

    
    /**
     * Returns a capabilities object specifying the features supported by this ES-adapter
     * <p>
     * @return Returns a capabilities object specifying the features supported
     * by this ES-adapter.
     */
    ESAdapterCapabilities getCapabilities();

    /**
     * Starts the external system.
     * <p>
     * This method blocks until the external system has been started.
     * If this method will be called or not depends on the settings in the capabilities object returned by
     * {@link #getCapabilities() getCapabilities()}.
     * @throws IOException If the power up fails. 
     */
    void powerUp() throws IOException;


    /**
     * Stops the external system.
     * <p>
     * This method blocks until the external system has been stopped.
     * <p>
     * If this method will be called or not depends on the settings in the capabilities object returned by
     * {@link #getCapabilities() getCapabilities()}.
     * @throws IOException If the power down fails. 
     */
    void shutDown() throws IOException;

    /**
     * Does the initial setup of the adapter.
     * <p>
     * Sets up a sequence of signals to be read and written using the {@link #readSignalData(java.nio.ByteBuffer) readSignalData()}
     * and {@link #writeSignalData(java.nio.ByteBuffer) writeSignalData()} respectively.
     * @param signalsToES An array of signal metadata specifying the signal values to be transferred from the
     * process model to the external system.
     * @param signalsToPM An array of signal metadata specifying the signal values to be transferred from the
     * external system to the process model.
     * @param config A collection of key-, value pairs holding configuration parameters specific to 
     * each implementation of {@link IExternalSystemAdapter IExternalSystemAdapter}. Key-, value pairs are
     * wrapped  in a {@link Parameters Parameters} class for convenience.
     * @throws IOException Is thrown to force the caller to an error state that must be handled by
     * manual intervention.
     * @throws InvalidParameterException Thrown if any parameter is illegal.
     */
     void setup(SignalMetadata[] signalsToES, SignalMetadata[] signalsToPM, Parameters config) throws IOException, InvalidParameterException;

    /**
     * Connects to the external system.
     * <p>
     * This operation may block for a longer period of time since several connect attempts may be done by the
     * current implementation of the ES-adapter. An {@link java.io.IOException IOException} is thrown if there is a
     * serious problem preventing a connection to be established.
     * @return Returns true if connected otherwise false. Please note that false is not indicating a failure. The
     * caller will call this method repeatedly until true is returned or an exception thrown.
     * @throws IOException Is thrown to force the caller to an error state that must be handled by
     * manual intervention.
     */
     boolean connect() throws IOException;
    

    /**
     * Disconnects from the external system.
     * <p>
     * The call is just ignored if already disconnected.
     */
    void disconnect();

    /**
     * Writes process data.
     * <p>
     * Writes values, of the previously setup sequence of signals, to the external system.
     * @param values A buffer containing the sequence of signal values to be written to the external system.
     * The number of signal values and their data type must correspond to the sequence of signal metadata given in
     * a previous call to {@link #setup(gil.core.SignalMetadata[], gil.core.SignalMetadata[], gil.common.Parameters) setup()}.
     * @return A result object indicating success or failure. A failure will indicate to the caller that
     * this write was unsuccessful but subsequent writes may succeed. The caller will not retry to rewrite
     * the same signal values. However, the caller will continue to call this method for new data.
     * An IOException is thrown for unrecoverable serious failures.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * external system and reconnect.
     */
    Result writeSignalData(ByteBuffer values) throws IOException;

    /**
     * Reads process data.
     * <p>
     * Reads values, of the previously setup sequence of signals, from the external system.
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
     * An IOException shall be thrown for unrecoverable serious failures.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * external system and reconnect.
     */
    Result readSignalData(ByteBuffer destBuf) throws IOException;

    /**
     * Returns software info such as version info.
     * <p>
     * Returns information about this implementation of the {@link IExternalSystemAdapter IExternalSystemAdapter}
     * interface, the external system and possibly about connected subsystems. 
     * @return Returns the software info structure for the current implementation of this interface and connected
     * subsystems. Index 0 contains the software info structure for the implementation of this interface. If
     * available; subsequent indexes contains information about the external system and subsystems thereof.
     */
    SoftwareInfo[] getInfo();

    /**
     * Returns the system status of the external system.
     * <p>
     * This gives an indication if the external system is OK or not OK.
     * It may not be possible to detect the current status of the external system and in that case
     * {@link SystemStatus SystemStatus.NOT_AVAILABLE} shall be returned.
     * <p>
     * If this method will be called or not depends on the settings in the capabilities object returned by
     * {@link #getCapabilities() getCapabilities()}.
     * @return The current status of the external system.
     */
    SystemStatus getStatus();


    /**
     * Returns the external system operating frequency in Hertz.
     * <p>
     * @return the external system operating frequency in Hertz.
     */
    int getOperatingFrequency();
    
    /**
     * Returns the current operating state. e.g. run, freeze.
     * <p>
     * States only has a meaning if the current status returned from {@link #getStatus() getStatus()} is OK.
     * Common states are defined in {@link SimState SimState}.
     * <p>
     * If this method will be called or not depends on the settings in the capabilities object returned by
     * {@link #getCapabilities() getCapabilities()}.
     * @return The current state e.g. running, freeze.
     */
    int getState();

    /**
     * Executes simulator commands.
     * <p>
     * The possible commands and their parameters are implementation specific and is
     * a contract between the current implementation of the {@link IExternalSystemAdapter IExternalSystemAdapter} and
     * the {@link IProcessModelAdapter IProcessModelAdapter}.
     * @param command The command to execute.
     * @return A result object indicating success or failure. A failure will indicate to the caller that
     * the command failed but subsequent commands may succeed. The caller will not retry the actual command.
     * However, the caller will continue to call this method for new commands.
     * An IOException shall be thrown for unrecoverable serious failures.
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * external system and reconnect.
     * @throws IllegalArgumentException Thrown when either the given command is not supported or the command parameters
     * are invalid.
     */
    Result executeSimCommand(Command command) throws IllegalArgumentException, IOException;


    /**
     * Adds a listener to receive progress change notifications.
     * <p>
     * If this method will be called or not depends on the settings in the capabilities object returned by
     * {@link #getCapabilities() getCapabilities()}.
     * @param listener The listener that subscibes for notifications when progress changes. The listeners must
     * handle concurrency since they may be called from separate threads.
     * @return true if the implemantation supports progress change notifications and the caller
     * can expect to receive notifications. Returns false when progress change notifications are not supported.
     */
    boolean addProgressChangeListener(IProgressEventListener listener);

    /**
     * Returns a list of control commands that can be invoked from client software.
     * <p>
     * The implementation of the {@link IExternalSystemAdapter IExternalSystemAdapter} interface may support a set
     * of control commands that can be invoked to control the behavior of the adapter. This method lists the available
     * commands. Commands listed may be invoked by calling
     * {@link #invokeControlCommand(java.lang.String, java.lang.String)  invokeControlCommand}.
     * @return An array holding the specification for each available command. If no commands are available an array
     * containing zero elements is returned.
     */
    CommandDescriptor[] availableControlCommands();

    /**
     * Advances the external system one frame.
     * <p> 
     * This method will be called if the adapter is supposed to run synchronous to the process model.
     * The calls will be done at the frequency returned by {@link #getOperatingFrequency()}.
     * <p>
     * If this method will be called or not depends on the settings in the capabilities object returned by
     * {@link #getCapabilities() getCapabilities()}.
     * @return A result object indicating success or failure. A failure indicates to the caller that the
     * time step call failed but subsequent time step calls may succeed. 
     * @throws IOException Thrown when there is a serious failure to force the caller to disconnect from the
     * external system and try to reconnect.
     */
    Result timeStepControl() throws IOException;
}
