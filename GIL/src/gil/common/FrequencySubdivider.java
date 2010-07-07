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
package gil.common;

import gil.core.SimTime;

/**
 * Used to subdivide frame changes in one system (system 1) to frame changes in in a system with a
 * lower operating frequency (system 2).
 *
 * @author Göran Larsson @ LearningWell AB
 */
public class FrequencySubdivider {

    long _system2CycleTimeInMicros;
    long _previousSystem2TimeStep;

    /**
     * @param operatingFrequencySystem2 the frequency in Hz of the second system. This frequency must be less or
     * equal to the frequency of system 1.
     */
    public FrequencySubdivider(int operatingFrequencySystem2) {
        _system2CycleTimeInMicros = _MICROSECONDS_PER_SEC / operatingFrequencySystem2;
    }

    /**
     * Resets the internally held system2 time to be equal to the given system1 time. The internal held system2 time
     * is used to detect delta changes when calling getSystem2TimeStepChange. This method must be called once prior to
     * start calling getSystem2TimeStepChange.
     */
    public void reset(SimTime system1Time) {
        _previousSystem2TimeStep = _system1TimeInMicros(system1Time) / _system2CycleTimeInMicros;
    }

    /**
     * Subsequent call to this method will return the time step delta change of system 2 since last call. If the
     * resulting delta step change is greater than 20 some other event is presumed to cause the big difference
     * and instead of returning the delta change, system 2 time is reset and 0 returned.
     * @param system1Time the current time of system 1.
     * @return the number of time steps system 2 must be incremented with in order to
     * stay synchronized with system 1.
     */
    public long getSystem2TimeStepChange(SimTime system1Time) {

        long currentSystem2TimeStep = _system1TimeInMicros(system1Time) / _system2CycleTimeInMicros;
        
        long delta = currentSystem2TimeStep - _previousSystem2TimeStep;

        // Handle day roll over
        if (delta < 0) {
            delta = 3600 * 24 * (_MICROSECONDS_PER_SEC / _system2CycleTimeInMicros) + delta;
        }

        // Handle delta chages unreasonable big
        if (delta > _MAX_DELTA_CHANGE_BEFORE_RESET) {
            reset(system1Time);
            return 0;
        }

        _previousSystem2TimeStep = currentSystem2TimeStep;
        return delta;
    }

    private long _system1TimeInMicros(SimTime system1Time)
    {
        return (long)(system1Time.hour() * 3600
                + system1Time.minute() * 60
                + system1Time.second()) * _MICROSECONDS_PER_SEC
                + system1Time.millisecond() * _MICROSECONDS_PER_MILLISEC;
    }

    private static final long _MICROSECONDS_PER_SEC = 1000000L;
    private static final long _MICROSECONDS_PER_MILLISEC = 1000L;
    private static final long _MAX_DELTA_CHANGE_BEFORE_RESET = 20;
}
