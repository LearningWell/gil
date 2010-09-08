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
package gil.web.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import gil.core.Statistics;

/**
 *
 * @author Göran Larsson @ LearningWell AB
 */
@XmlRootElement(name="statistics")
public class StatisticsJAXB {

    @XmlRootElement
    public static class AdapterStatistics {

        public AdapterStatistics() {}

        public AdapterStatistics(int droppedFrames, int commandFailureCount, int dataWriteFailureCount, 
                int dataReadFailureCount, int dataReadCount, int dataWriteCount) {
            this.droppedFrames = droppedFrames;
            this.commandFailureCount = commandFailureCount;
            this.dataWriteFailureCount = dataWriteFailureCount;
            this.dataReadFailureCount = dataReadFailureCount;
            this.dataReadCount = dataReadCount;
            this.dataWriteCount = dataWriteCount;
        }
        
        public int droppedFrames;
        public int commandFailureCount;
        public int dataWriteFailureCount;
        public int dataReadFailureCount;
        public int dataReadCount;
        public int dataWriteCount;
    }

    public AdapterStatistics externalSystem;
    public AdapterStatistics processModel;

    public StatisticsJAXB() {
    }

    public StatisticsJAXB(Statistics esStats, Statistics pmStats) {

        externalSystem = new AdapterStatistics(esStats.droppedFrames, esStats.commandFailureCount,
                esStats.dataWriteFailureCount, esStats.dataReadFailureCount, esStats.dataReadCount,
                esStats.dataWriteCount);
        processModel = new AdapterStatistics(pmStats.droppedFrames, pmStats.commandFailureCount,
                pmStats.dataWriteFailureCount, pmStats.dataReadFailureCount, pmStats.dataReadCount,
                pmStats.dataWriteCount);
    }
}
