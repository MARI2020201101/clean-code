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

import java.util.Calendar;
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
    /** 
     * The day number (1-Jan-1900 = 2, 2-Jan-1900 = 3, ..., 31-Dec-9999 = 
     * 2958465). 
     */
    private final int serial;

    /** The day of the month (1 to 28, 29, 30 or 31 depending on the month). */
    private final int day;


    /** The month of the year (1 to 12). */
    private final int month;
    /** The year (1900 to 9999). */
    private final int year;

    public SpreadsheetDate(final int day, final int month, final int year) {

        if ((year >= 1900) && (year <= 9999)) {
            this.year = year;
        }
        else {
            throw new IllegalArgumentException(
                    "The 'year' argument must be in range 1900 to 9999."
            );
        }

        if ((month >= Month.JANUARY.index)
                && (month <= Month.DECEMBER.index)) {
            this.month = month;
        }
        else {
            throw new IllegalArgumentException(
                    "The 'month' argument must be in the range 1 to 12."
            );
        }

        if ((day >= 1) && (day <= DayDate.lastDayOfMonth(month, year))) {
            this.day = day;
        }
        else {
            throw new IllegalArgumentException("Invalid 'day' argument.");
        }

        // the serial number needs to be synchronised with the day-month-year...
        this.serial = calcSerial(day, month, year);

    }
    public SpreadsheetDate(final int day, final Month month, final int year) {

        if ((year >= 1900) && (year <= 9999)) {
            this.year = year;
        }
        else {
            throw new IllegalArgumentException(
                "The 'year' argument must be in range 1900 to 9999."
            );
        }

        if ((month.index >= Month.JANUARY.index)
                && (month.index <= Month.DECEMBER.index)) {
            this.month = month.index;
        }
        else {
            throw new IllegalArgumentException(
                "The 'month' argument must be in the range 1 to 12."
            );
        }

        if ((day >= 1) && (day <= DayDate.lastDayOfMonth(month.index, year))) {
            this.day = day;
        }
        else {
            throw new IllegalArgumentException("Invalid 'day' argument.");
        }

        // the serial number needs to be synchronised with the day-month-year...
        this.serial = calcSerial(day, month.index, year);

    }

    /**
     * Standard constructor - creates a new date object representing the
     * specified day number (which should be in the range 2 to 2958465.
     *
     * @param serial  the serial number for the day (range: 2 to 2958465).
     */
    public SpreadsheetDate(final int serial) {

        if ((serial >= EARLIEST_DATE_ORDINAL) && (serial <= LATEST_DATE_ORDINAL)) {
            this.serial = serial;
        }
        else {
            throw new IllegalArgumentException(
                "SpreadsheetDate: Serial must be in range 2 to 2958465.");
        }

        // the day-month-year needs to be synchronised with the serial number...
      // get the year from the serial date
      final int days = this.serial - EARLIEST_DATE_ORDINAL;
      // overestimated because we ignored leap days
      final int overestimatedYYYY = 1900 + (days / 365);
      final int leaps = leapYearCount(overestimatedYYYY);
      final int nonleapdays = days - leaps;
      // underestimated because we overestimated years
      int underestimatedYYYY = 1900 + (nonleapdays / 365);

      if (underestimatedYYYY == overestimatedYYYY) {
          this.year = underestimatedYYYY;
      }
      else {
          int ss1 = calcSerial(1, 1, underestimatedYYYY);
          while (ss1 <= this.serial) {
              underestimatedYYYY = underestimatedYYYY + 1;
              ss1 = calcSerial(1, 1, underestimatedYYYY);
          }
          this.year = underestimatedYYYY - 1;
      }

      final int ss2 = calcSerial(1, 1, this.year);

      int[] daysToEndOfPrecedingMonth 
          = AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;

      if (isLeapYear(this.year)) {
          daysToEndOfPrecedingMonth 
              = LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
      }

      // get the month from the serial date
      int mm = 1;
      int sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1;
      while (sss < this.serial) {
          mm = mm + 1;
          sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1;
      }
      this.month = mm - 1;

      // what's left is d(+1);
      this.day = this.serial - ss2 
                 - daysToEndOfPrecedingMonth[this.month] + 1;

    }

    /**
     * Returns the serial number for the date, where 1 January 1900 = 2
     * (this corresponds, almost, to the numbering system used in Microsoft
     * Excel for Windows and Lotus 1-2-3).
     *
     * @return The serial number of this date.
     */
    public int toSerial() {
        return this.serial;
    }

    /**
     * Returns a <code>java.util.Date</code> equivalent to this date.
     *
     * @return The date.
     */
    public Date toDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(getYYYY(), getMonth() - 1, getDayOfMonth(), 0, 0, 0);
        return calendar.getTime();
    }
    public static int leapYearCount(final int yyyy) {

        final int leap4 = (yyyy - 1896) / 4;
        final int leap100 = (yyyy - 1800) / 100;
        final int leap400 = (yyyy - 1600) / 400;
        return leap4 - leap100 + leap400;

    }
    /**
     * Returns the year (assume a valid range of 1900 to 9999).
     *
     * @return The year.
     */
    public int getYYYY() {
        return this.year;
    }

    /**
     * Returns the month (January = 1, February = 2, March = 3).
     *
     * @return The month of the year.
     */
    public int getMonth() {
        return this.month;
    }

    /**
     * Returns the day of the month.
     *
     * @return The day of the month.
     */
    public int getDayOfMonth() {
        return this.day;
    }


    public int getDayOfWeek() {
        return (this.serial + 6) % 7 + 1;
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


    public int compare(final DayDate other) {
        return this.serial - other.toSerial();
    }


    public int compareTo(final Object other) {
        return compare((DayDate) other);
    }
    

    public boolean isOn(final DayDate other) {
        return (this.serial == other.toSerial());
    }


    public boolean isBefore(final DayDate other) {
        return (this.serial < other.toSerial());
    }


    public boolean isOnOrBefore(final DayDate other) {
        return (this.serial <= other.toSerial());
    }


    public boolean isAfter(final DayDate other) {
        return (this.serial > other.toSerial());
    }


    public boolean isOnOrAfter(final DayDate other) {
        return (this.serial >= other.toSerial());
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


    private int calcSerial(final int d, final int m, final int y) {
        final int yy = ((y - 1900) * 365) + leapYearCount(y - 1);
        int mm = SpreadsheetDate.AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[m];
        if (m > MonthConstants.FEBRUARY) {
            if (DayDate.isLeapYear(y)) {
                mm = mm + 1;
            }
        }
        final int dd = d;
        return yy + mm + dd + 1;
    }

}
