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

/**
 * Use this class to detect state changes on variables of arbitrary data type.
 * @author Göran Larsson @ LearningWell AB
 */
public class Trigger<T> {

    public enum Change {
        NO_CHANGE,
        CHANGE,
        CHANGE_TO,
        CHANGE_FROM
    }

    public Trigger(T initialValue)
    {
        _previousValue = initialValue;
    }

    /**
     * Returns an indication on change since last call.
     * <p>
     * If newValue is changed to the target value since the previous call, CHANGE_TO is returned.
     * If newValue was equal to targetValue on the previous call and is now changed,  CHANGE_FROM is returned.
     * If newValue is changed since the previous call but neither to or from the targetValue, CHANGE is returned.
     */
    public Change getStateChange(T newValue, T targetValue)
    {
        if (newValue != _previousValue)
        {
            T prevValue = _previousValue;
            _previousValue = newValue;
            if (prevValue == targetValue)
                return Change.CHANGE_FROM;
            if (newValue == targetValue)
                return Change.CHANGE_TO;
            return Change.CHANGE;
        }
        return Change.NO_CHANGE;
    }

    T _previousValue;
}
