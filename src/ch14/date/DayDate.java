/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 * 
 * Project Info:  http://www.jfree.org/jcommon/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------
 * SerialDate.java
 * ---------------
 * (C) Copyright 2001-2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SerialDate.java,v 1.8 2006/08/29 13:44:16 mungady Exp $
 *
 * Changes (from 11-Oct-2001)
 * --------------------------
 * 11-Oct-2001 : Re-organised the class and moved it to new package 
 *               com.jrefinery.date (DG);
 * 05-Nov-2001 : Added a getDescription() method, and eliminated NotableDate 
 *               class (DG);
 * 12-Nov-2001 : IBD requires setDescription() method, now that NotableDate 
 *               class is gone (DG);  Changed getPreviousDayOfWeek(), 
 *               getFollowingDayOfWeek() and getNearestDayOfWeek() to correct 
 *               bugs (DG);
 * 05-Dec-2001 : Fixed bug in SpreadsheetDate class (DG);
 * 29-May-2002 : Moved the month constants into a separate interface 
 *               (MonthConstants) (DG);
 * 27-Aug-2002 : Fixed bug in addMonths() method, thanks to N???levka Petr (DG);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Implemented Serializable (DG);
 * 29-May-2003 : Fixed bug in addMonths method (DG);
 * 04-Sep-2003 : Implemented Comparable.  Updated the isInRange javadocs (DG);
 * 05-Jan-2005 : Fixed bug in addYears() method (1096282) (DG);
 * 
 */

package ch14.date;

import java.io.Serializable;


public abstract class DayDate implements Comparable, Serializable {

    public abstract int getYear();
    public abstract Month getMonth();
    public abstract int getOrdinalDay();
    public DayDate plusDays(int days) {
        return DayDateFactory.makeDate(getOrdinalDay() + days);
    }
    public DayDate plusMonths(int months) {
        int thisMonthOrdinal = 12 * getYear() + getMonth().index -1 ;
        int resultMonthAsOrdinal = thisMonthOrdinal + months;
        int resultYear = resultMonthAsOrdinal / 12;
        Month resultMonth = Month.make(resultMonthAsOrdinal % 12 + 1);

        int lastDayOfResultMonth = DateUtil.lastDayOfMonth(resultMonth, resultYear);
        int resultDay = Math.min(getDayOfMonth(), lastDayOfResultMonth);
        return DayDateFactory.makeDate(resultDay, resultMonth, resultYear);

    }


    public DayDate addYears(final int years, final DayDate base) {

        final int baseY = base.getYear();
        final int baseM = base.getMonth();
        final int baseD = base.getDayOfMonth();

        final int targetY = baseY + years;
        final int targetD = Math.min(
            baseD, DateUtil.lastDayOfMonth(baseM, targetY)
        );

        return DayDateFactory.makeDate(targetD, baseM, targetY);

    }


    public static DayDate getPreviousDayOfWeek(final int targetWeekday,
                                               final DayDate base) {
        // find the date...
        final int adjust;
        final int baseDOW = base.getDayOfWeek();
        if (baseDOW > targetWeekday) {
            adjust = Math.min(0, targetWeekday - baseDOW);
        }
        else {
            adjust = -7 + Math.max(0, targetWeekday - baseDOW);
        }

        return DayDate.plusDays(adjust, base);

    }

    public static DayDate getFollowingDayOfWeek(final int targetWeekday,
                                                final DayDate base) {
        // find the date...
        final int adjust;
        final int baseDOW = base.getDayOfWeek();
        if (baseDOW > targetWeekday) {
            adjust = 7 + Math.min(0, targetWeekday - baseDOW);
        }
        else {
            adjust = Math.max(0, targetWeekday - baseDOW);
        }

        return DayDate.plusDays(adjust, base);
    }


    public static DayDate getNearestDayOfWeek(final int targetDOW,
                                              final DayDate base) {
        // find the date...
        final int baseDOW = base.getDayOfWeek();
        int adjust = -Math.abs(targetDOW - baseDOW);
        if (adjust >= 4) {
            adjust = 7 - adjust;
        }
        if (adjust <= -4) {
            adjust = 7 + adjust;
        }
        return DayDate.plusDays(adjust, base);

    }

    public DayDate getEndOfCurrentMonth(final DayDate base) {
        final int last = DateUtil.lastDayOfMonth(
            base.getMonth(), base.getYear()
        );
        return DayDateFactory.makeDate(last, base.getMonth(), base.getYear());
    }


    public static String weekInMonthToString(final int count) {
        return WeekInMonth.intToWeekInMonth(count).name();
    }


    public abstract int toSerial();
    public abstract java.util.Date toDate();

    public abstract int getDayOfMonth();
    public abstract int getDayOfWeek();
    public abstract int compare(DayDate other);
    public abstract boolean isOn(DayDate other);
    public abstract boolean isBefore(DayDate other);
    public abstract boolean isOnOrBefore(DayDate other);
    public abstract boolean isAfter(DayDate other);
    public abstract boolean isOnOrAfter(DayDate other);
    public abstract boolean isInRange(DayDate d1, DayDate d2);
    public abstract boolean isInRange(DayDate d1, DayDate d2,
                                      int include);
    public DayDate getPreviousDayOfWeek(final int targetDOW) {
        return getPreviousDayOfWeek(targetDOW, this);
    }

    public DayDate getFollowingDayOfWeek(final int targetDOW) {
        return getFollowingDayOfWeek(targetDOW, this);
    }
    public DayDate getNearestDayOfWeek(final int targetDOW) {
        return getNearestDayOfWeek(targetDOW, this);
    }

}
