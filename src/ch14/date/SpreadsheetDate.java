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
 * --------------------
 * SpreadsheetDate.java
 * --------------------
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: SpreadsheetDate.java,v 1.10 2006/08/29 13:59:30 mungady Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 05-Nov-2001 : Added getDescription() and setDescription() methods (DG);
 * 12-Nov-2001 : Changed name from ExcelDate.java to SpreadsheetDate.java (DG);
 *               Fixed a bug in calculating day, month and year from serial 
 *               number (DG);
 * 24-Jan-2002 : Fixed a bug in calculating the serial number from the day, 
 *               month and year.  Thanks to Trevor Hills for the report (DG);
 * 29-May-2002 : Added equals(Object) method (SourceForge ID 558850) (DG);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Implemented Serializable (DG);
 * 04-Sep-2003 : Completed isInRange() methods (DG);
 * 05-Sep-2003 : Implemented Comparable (DG);
 * 21-Oct-2003 : Added hashCode() method (DG);
 * 29-Aug-2006 : Removed redundant description attribute (DG);
 *
 */

package ch14.date;

import java.util.Date;

public class SpreadsheetDate extends DayDate {
    public static final int EARLIEST_DATE_ORDINAL = 2;
    public static final int LATEST_DATE_ORDINAL = 2958465;
    public static final int MINIMUM_YEAR_SUPPORTED = 1900;
    public static final int MAXIMUM_YEAR_SUPPORTED = 9999;
    static final int[] AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH =
            {0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
    static final int[]
            LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH =
            {0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366};

    private int ordinalDay;
    private int day;
    private Month month;
    private int year;
    public SpreadsheetDate(int day, Month month, int year) {
        if(year < MINIMUM_YEAR_SUPPORTED || year > MAXIMUM_YEAR_SUPPORTED)
            throw new IllegalArgumentException("Non Valid Year");
        if(day < 1 || day > DateUtil.lastDayOfMonth(month, year))
            throw new IllegalArgumentException("Non Valid Day");

        this.year = year;
        this.month = month;
        this.day = day;
        this.ordinalDay = calcOrdinal(day, month, year);
    }

    public SpreadsheetDate(int day, int month, int year) {
        this(day, Month.make(month), year);
    }

    public SpreadsheetDate(int ordinalDay) {
        if(ordinalDay < EARLIEST_DATE_ORDINAL || ordinalDay > LATEST_DATE_ORDINAL)
            throw new IllegalArgumentException("Non Valid Ordinal Day");
        this.ordinalDay = ordinalDay;
        calcDayMonthYear();
    }

    private void calcDayMonthYear() {
        int days = ordinalDay - EARLIEST_DATE_ORDINAL;
        int overEstimatedYear = MINIMUM_YEAR_SUPPORTED + days / 365; //윤년을 고려하지 않고 그냥 센 year. 초과될수있다
        int nonLeapDays = days - DateUtil.leapYearCount(overEstimatedYear);
        int underEstimatedYear = MINIMUM_YEAR_SUPPORTED + nonLeapDays / 365 ; //윤년을 고려해서 최소로 센 year.
        year = huntForYearContaining(ordinalDay, underEstimatedYear);
        int firstOrdinalOfYear =
    }

    /**
     * Returns the serial number for the date, where 1 January 1900 = 2
     * (this corresponds, almost, to the numbering system used in Microsoft
     * Excel for Windows and Lotus 1-2-3).
     *
     * @return The serial number of this date.
     */
    public int toSerial() {
        return this.ordinalDay;
    }

    public static int leapYearCount(final int yyyy) {

        final int leap4 = (yyyy - 1896) / 4;
        final int leap100 = (yyyy - 1800) / 100;
        final int leap400 = (yyyy - 1600) / 400;
        return leap4 - leap100 + leap400;

    }
    public int getYear() {
        return this.year;
    }
    public Month getMonth() {
        return this.month;
    }
    public int getDayOfMonth() {
        return this.day;
    }


    public int getDayOfWeek() {
        return (this.ordinalDay + 6) % 7 + 1;
    }


    public boolean equals(final Object object) {

        if (object instanceof DayDate) {
            final DayDate s = (DayDate) object;
            return (s.toSerial() == this.toSerial());
        }
        else {
            return false;
        }

    }


    public int hashCode() {
        return toSerial();
    }





    public int compareTo(final Object other) {
        return daysSince((DayDate) other);
    }
    

    public boolean isOn(final DayDate other) {
        return (this.ordinalDay == other.toSerial());
    }


    public boolean isBefore(final DayDate other) {
        return (this.ordinalDay < other.toSerial());
    }


    public boolean isOnOrBefore(final DayDate other) {
        return (this.ordinalDay <= other.toSerial());
    }


    public boolean isAfter(final DayDate other) {
        return (this.ordinalDay > other.toSerial());
    }


    public boolean isOnOrAfter(final DayDate other) {
        return (this.ordinalDay >= other.toSerial());
    }


    public boolean isInRange(final DayDate d1, final DayDate d2) {
        return isInRange(d1, d2, DayDate.INCLUDE_BOTH);
    }


    public boolean isInRange(final DayDate d1, final DayDate d2,
                             final int include) {
        final int s1 = d1.toSerial();
        final int s2 = d2.toSerial();
        final int start = Math.min(s1, s2);
        final int end = Math.max(s1, s2);
        
        final int s = toSerial();
        if (include == DayDate.INCLUDE_BOTH) {
            return (s >= start && s <= end);
        }
        else if (include == DayDate.INCLUDE_FIRST) {
            return (s >= start && s < end);            
        }
        else if (include == DayDate.INCLUDE_SECOND) {
            return (s > start && s <= end);            
        }
        else {
            return (s > start && s < end);            
        }    
    }


    private int calcOrdinal(int day, Month month, int year) {
        int leapDaysForYear = DateUtil.leapYearCount(year-1);
        int daysUpToYear = (year - MINIMUM_YEAR_SUPPORTED) * 365 + leapDaysForYear; //해당 년도까지의 Day 카운트
        int daysUpToMonth = AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[month.toInt()];
        if(DateUtil.isLeapYear(year) && month.toInt() > Month.FEBRUARY.toInt()) {daysUpToMonth++;}
        int daysInMonth = day - 1;
        return daysUpToYear + daysInMonth + daysInMonth + EARLIEST_DATE_ORDINAL;
    }

}
