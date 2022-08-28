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

    public int getYear() {
        return this.year;
    }
    public Month getMonth() {
        return this.month;
    }
    public int getDayOfMonth() {
        return this.day;
    }
    @Override
    public int getOrdinalDay() {
        return this.ordinalDay;
    }
    @Override
    protected Day getDayOfWeekForOrdinalZero() {
        return Day.SATURDAY;
    }

    private void calcDayMonthYear() {
        int days = ordinalDay - EARLIEST_DATE_ORDINAL;
        int overEstimatedYear = MINIMUM_YEAR_SUPPORTED + days / 365; //윤년을 고려하지 않고 그냥 센 year. 초과될수있다
        int nonLeapDays = days - DateUtil.leapYearCount(overEstimatedYear);
        int underEstimatedYear = MINIMUM_YEAR_SUPPORTED + nonLeapDays / 365 ; //윤년을 고려해서 최소로 센 year.
        year = huntForYearContaining(ordinalDay, underEstimatedYear); //윤년을 고려한 year 를 조정한다.
        int firstOrdinalOfYear = firstOrdinalOfYear(year);
        month = huntForMonthContaining(ordinalDay, firstOrdinalOfYear);
    }

    private Month huntForMonthContaining(int anOrdinal, int firstOrdinalOfYear) {
        int daysIntoThisYear = anOrdinal - firstOrdinalOfYear;
        int aMonth = 1;
        while(daysBeforeThisMonth(month) < daysIntoThisYear) aMonth++;
        return Month.make(aMonth -1);
    }

    private int daysBeforeThisMonth(Month aMonth) {
        if(DateUtil.isLeapYear(year))
            return LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[aMonth.toInt()] -1;
        else
            return AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[aMonth.toInt()] -1 ;
    }

    private int huntForYearContaining(int anOrdinalDay, int startingYear) {
        int aYear = startingYear;
        while(firstOrdinalOfYear(aYear) <= anOrdinalDay) aYear++;
        return aYear-1;
    }

    private int firstOrdinalOfYear(int year) {
        return calcOrdinal(1, Month.JANUARY, year);
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
