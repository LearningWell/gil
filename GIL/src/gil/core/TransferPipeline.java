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

import gil.io.IPipelineStage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Representing a chain of stages where each stage can process data flowing through.
 * Data can be transferred in either of two directions and the stages can process data differently
 * depending on the actual transfer direction.
 * @author Göran Larsson - LearningWell AB
 */
public class TransferPipeline implements ITransferPipeline {
    private static Logger _logger = Logger.getLogger(TransferPipeline.class);

    private List<IPipelineStage> _stages;

    /**
     * Creates an empty transfer pipeline with no stages. Data passing through the pipeline will be unaffected.
     */
    public TransferPipeline() {
        _stages = new ArrayList<IPipelineStage>();
    }

    /**
     * Constructs a transfer pipeline with the given stages. Data passing through the pipeline will be monitored
     * and/or processed be the given stages.
     */
    public TransferPipeline(List<IPipelineStage> stages) {
        if (stages == null)
            throw new IllegalArgumentException("stages");

        _stages = stages;
    }

    /**
     * @return The set of stages used to monitor and/or process data flowing through this pipeline.
     */
    public List<IPipelineStage> getStages() {
        return _stages;
    }

    /**
     * Operate on signals in some sort of way. May or may not affect the original
     * values given in the values buffer. Values are expected to be in the same order
     * as their metadata, given in a prior mandatory call to {@link #setup(gil.core.SignalMetadata[])}.
     * @param values The values to be operated on. The values must be in the same sequence and of the same
     * datatype as the corresponding metadata given in a prior call to setup.
     * @param direction used by the pipeline stages when data processing depends on the data flow direction.
     */
    public void processSignals(ByteBuffer values, SignalMetadata.DataflowDirection direction) {
        for (IPipelineStage stage:_stages) {
            _logger.debug("Processing signals in stage " + stage.toString());
            values.rewind();
            stage.processSignals(values, direction);
        }
        values.rewind();
    }
}
