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
import gil.common.Parameters;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.core.IllegalConfigurationException;
import gil.core.SignalMetadata;

/**
 * The interface to be implemented for stages to be added to the transfer pipeline.
 * <p>
 * The transfer pipeline consists of a chain of stages through which process data is fed by the GIL runtime.
 * A stage processes, or monitor the passing process data.
 * @author Göran Larsson - LearningWell AB
 */
public interface IPipelineStage extends IControlCommandInvokeable {
    /**
     * Operates on, or monitor the given process data.
     * <p>
     * May or may not affect the original values given in the values buffer. Values are expected to be in the same
     * sequence and the same datatype as their metadata specifies in a prior call to
     * {@link #setup(gil.core.SignalMetadata[], gil.core.SignalMetadata[]) setup()}.
     * @param values The signals values to be operated on. Contained in a byte buffer.
     * @param direction Used by the pipeline stage when the processing depends on the data flow direction.
     */
    void processSignals(ByteBuffer values, SignalMetadata.DataflowDirection direction);

    /**
     * Does the initial setup of the stage.
     * <p>
     * @param id The identifier for this stage. Unique within the transfer pipeline this stage is part of.
     * @param signalsToPM describes the signals to operate on in this stage when data is flowing from the 
     * external system to the process model.
     * @param signalsToES describes the signals to operate on in this stage when data is flowing from the
     * process model to the external system.
     * @param config A collection of key-, value pairs holding configuration parameters specific to
     * each implementation of {@link IPipelineStage IPipelineStage}. Key-, value pairs are
     * wrapped  in a {@link Parameters Parameters} class for convenience.
     * @throws IllegalConfigurationException if any given metadata is not valid.
     */
    void setup(String id, SignalMetadata[] signalsToPM, SignalMetadata[] signalsToES, Parameters config) throws IllegalConfigurationException;

    /**
     * Invokes a control command.
     *
     * <p>
     * Used to invoke commands to control the behavior of the stage. The commands are specific to each
     * implementation of the {@link IPipelineStage IPipelineStage} interface.
     * @param command The command to invoke.
     * @return A map of result parameters.
     * @throws IllegalArgumentException If the given command or parameters are illegal.
     */
    Map<String, String> invokeControlCommand(Command command) throws IllegalArgumentException;

    /**
     * Returns a list of available control commands.
     * <p>
     * The implementation of the {@link IPipelineStage IPipelineStage} interface may support a set
     * of control commands to be invoked to control the behavior of the stage. This method returns
     * a list of the available commands. Commands listed may be invoked by calling
     * {@link #invokeControlCommand(gil.core.Command) invokeControlCommand()}.
     * @return An array holding the specification for each available command. If no commands are available, an array
     * containing zero elements is returned.
     */
    CommandDescriptor[] availableCommands();

    /**
     * Returns the id for this stage. 
     * @return The identifier of this stage. The returned id is unique within the transfer pipeline this stage is part of.
     */
    String getId();


    /**
     * Returns signal definitions for signals written to the process model.
     * @return The signal definitions for the process data transferred from the External system to the Process model.
     */
    SignalMetadata[] getSignalsToPM();

    /**
     * Returns signal definitions for signals written to the external system.
     * @return The signal definitions for the process data transferred from the Process model to the External system.
     */
    SignalMetadata[] getSignalsToES();

    /**
     * Returns the set of configuration parameters for this stage.
     * @return A Parameters object containing key- value pairs with configuration parameters.
     */
    Parameters getConfig();
}
