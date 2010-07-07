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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A date time container.
 * @author Göran Larsson @ LearningWell AB
 */
public class SimTime implements Cloneable {


    // Be aware!! If adding attributes, take into account that this class implements Cloneable
    private final int _year;
    private final int _month;
    private final int _dayOfMonth;
    private final int _hour;
    private final int _minute;
    private final int _second;
    private final int _millisecond;

    public SimTime(int year, int month, int dayOfMonth, int hour, int minute, int second, int millisecond)
    {
        _year = year;
        _month = month;
        _dayOfMonth = dayOfMonth;
        _hour = hour;
        _minute = minute;
        _second = second;
        _millisecond = millisecond;
    }

    public SimTime() {
        this (2009, 1, 1, 0, 0, 0, 0);
    }

    @Override
    public SimTime clone() {
        try {
            return (SimTime) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return String.format("%d-%02d-%02d %02d:%02d:%02d.%03d", _year, _month, _dayOfMonth,
                _hour, _minute, _second, _millisecond);
    }

    public static SimTime parse(String timeStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date d = sdf.parse(timeStr);
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        return new SimTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE), c.get(Calendar.SECOND),c.get(Calendar.MILLISECOND));
    }
    
    public int year() {
        return _year;
    }

    public int month() {
        return _month;
    }

    public int dayOfMonth() {
        return _dayOfMonth;
    }

    public int hour() {
        return _hour;
    }

    public int minute() {
        return _minute;
    }

    public int second() {
        return _second;
    }

    public int millisecond() {
        return _millisecond;
    }
}
