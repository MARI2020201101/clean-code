/* ========================================================================
 * JCommon : a free general purpose class library for the Java(tm) platform
 * ========================================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * DayAndMonthRule.java
 * --------------------
 * (C) Copyright 2000-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DayAndMonthRule.java,v 1.6 2005/11/16 15:58:40 taqua Exp $
 *
 * Changes (from 26-Oct-2001)
 * --------------------------
 * 26-Oct-2001 : Changed package to com.jrefinery.date.* (DG);
 * 12-Nov-2001 : Added some argument checks (DG);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 01-Jun-2005 : Removed the explicit clonable declaration, it is declared
 *               in the super class.
 */

package ch14.date;

/**
 * An annual date rule where the generated date always falls on the same day
 * and month each year.
 * <P>
 * An example is ANZAC Day in Australia and New Zealand: it is observed on
 * 25 April of every year.
 *
 * @author David Gilbert
 */
public class DayAndMonthRule extends AnnualDateRule {

    /** The day of the month. */
    private int dayOfMonth;

    /** The month (uses 1 to 12 in the obvious way). */
    private int month;

    /**
     * Default constructor: builds a DayAndMonthRule for 1 January.
     */
    public DayAndMonthRule() {
        this(1, MonthConstants.JANUARY);
    }

    /**
     * Standard constructor: builds a DayAndMonthRule for the given
     * day-of-the-month and month.
     * <P>
     * For the month parameter, use SerialDate.JANUARY, etc. Note that there
     * are no checks to prevent you from entering an invalid combination (such
     * as 31 February).
     *
     * @param dayOfMonth  the day of the month (in the range 1 to 31).
     * @param month  the month (use SerialDate.JANUARY, SerialDate.FEBRUARY etc.);
     */
    public DayAndMonthRule(final int dayOfMonth, final int month) {

        // check arguments delegated to setter methods...
        setMonth(month);
        setDayOfMonth(dayOfMonth);

    }

    /**
     * Returns the day of the month.
     *
     * @return the day of the month.
     */
    public int getDayOfMonth() {
        return this.dayOfMonth;
    }

    /**
     * Sets the day-of-the-month for this rule.
     *
     * @param dayOfMonth  the day-of-the-month.
     */
    public void setDayOfMonth(final int dayOfMonth) {

        // check arguments...
        if ((dayOfMonth < 1) || (dayOfMonth > Month.LAST_DAY_OF_MONTH[this.month])) {
            throw new IllegalArgumentException(
                "DayAndMonthRule(): dayOfMonth outside valid range.");
        }

        // make the change...
        this.dayOfMonth = dayOfMonth;

    }

    /**
     * Returns an integer code representing the month.
     * <P>
     * The codes JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST,
     * SEPTEMBER, OCTOBER, NOVEMBER and DECEMBER are defined in the SerialDate
     * class.
     *
     * @return an integer code representing the month.
     */
    public int getMonth() {
        return this.month;
    }

    /**
     * Sets the month for this rule.
     *
     * @param month  the month for this rule.
     */
    public void setMonth(final int month) {

        this.month = month;

    }

    public DayDate getDate(final int yyyy) {
        return DayDateFactory.makeDate(this.dayOfMonth, this.month, yyyy);
    }
}
