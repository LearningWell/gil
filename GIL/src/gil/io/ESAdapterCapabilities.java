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

/**
 * A class with fields used to specifying the capabilities of a adapter implementing the
 * {@link IExternalSystemAdapter} interface.
 * @author Göran Larsson @ LearningWell AB
 */
public class ESAdapterCapabilities {

    /**
     * Constructs the capabilities object with all capabilities set to true.
     */
    public ESAdapterCapabilities() {
    }

    /**
     * If true the external system adapter must handle calls to {@link IExternalSystemAdapter#powerUp() } and
     * {@link IExternalSystemAdapter#shutDown() }.
     * <p>
     * If false is returned these methods don not have to contain any
     * code.
     * @return
     */
    public boolean canShutDownAndPowerUp = true;
    

    /**
     * True if the external system adapter expects calls to {@link IExternalSystemAdapter#timeStepControl()} 
     * at a frequency specified by {@link IExternalSystemAdapter#getOperatingFrequency()}. 
     * <p>
     * The calls are expected to be synchronous to the process model operating frequency or a
     * subdivided frequency thereof.
     */
    public boolean isSynchronous = true;

    /**
     * True if listeners may be added by calling 
     * {@link IExternalSystemAdapter#addProgressChangeListener(gil.common.IProgressEventListener)
     * IExternalSystemAdapter.addProgressChangeListener()}.
     * <p>
     * Listeners shall receive progress info when actions are executed.
     * 
     */
    public boolean reportsProgress = true;

    /**
     * True if calls to {@link IExternalSystemAdapter#getStatus()} returns the actual status of the external system
     * other than NOT_AVAILABLE.
     */
    public boolean canReportStatus = true;

    /**
     * True if calls to {@link IExternalSystemAdapter#getState()} returns the actual state of the external system
     * other than NOT_AVAILABLE.
     */
    public boolean canReportState = true;
    
    /**
     * True if the adapter expects to receive simulator commands, such
     * as run, freeze etc., from the process model.
     */
    public boolean expectsSimulatorCommands = true;
}
