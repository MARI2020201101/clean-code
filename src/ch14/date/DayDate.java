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
import java.util.Calendar;
import java.util.Date;

import static ch14.date.DateInterval.CLOSED;
import static ch14.date.DateUtil.lastDayOfMonth;


public abstract class DayDate implements Serializable {
    public abstract int getOrdinalDay();
    public abstract int getYear();
    public abstract Month getMonth();
    public abstract int getDayOfMonth();

    protected abstract Day getDayOfWeekForOrdinalZero();

    public DayDate plusDays(int days) {
        return DayDateFactory.makeDate(getOrdinalDay() + days);
    }

    public DayDate plusMonths(int months) {
        int thisMonthOrdinal = 12 * getYear() + getMonth().index -1 ;
        int resultMonthAsOrdinal = thisMonthOrdinal + months;
        int resultYear = resultMonthAsOrdinal / 12;
        Month resultMonth = Month.make(resultMonthAsOrdinal % 12 + 1);

        int resultDay = correctLastDayOfMonth(getDayOfMonth(), resultMonth, resultYear);
        //????????? day??? 31????????? ?????? month??? ??????????????? 30????????? ??? ?????? ?????? ????????? ??????
        return DayDateFactory.makeDate(resultDay, resultMonth, resultYear);
    }

    public DayDate plusYears(int years) {
        int resultYear = getYear() + years;
        int resultDay = correctLastDayOfMonth(getDayOfMonth(), getMonth(), resultYear);
        return DayDateFactory.makeDate(resultDay, getMonth(), resultYear);
    }

    private int correctLastDayOfMonth(int day, Month month, int year){
        int lastDayOfMonth = DateUtil.lastDayOfMonth(month, year);
        if(day > lastDayOfMonth) day = lastDayOfMonth;
        return day;
    }

    public DayDate getPreviousDayOfWeek(Day targetDayOfWeek) {
        int offsetToTarget = targetDayOfWeek.index - getDayOfWeek().index;
        if(offsetToTarget >= 0){
            offsetToTarget -= 7;
        }
        return plusDays(offsetToTarget);
    }

    public DayDate getFollowingDayOfWeek(Day targetDayOfWeek) {
        int offsetToTarget = targetDayOfWeek.index - getDayOfWeek().index;
        if(offsetToTarget <= 0){
            offsetToTarget += 7;
        }
        return plusDays(offsetToTarget);
    }

    public DayDate getNearestDayOfWeek(Day targetDay) {
        int offsetToThisWeeksTarget = targetDay.index - getDayOfWeek().index;
        int offsetToFutureTarget = (offsetToThisWeeksTarget + 6) % 7;
        int offsetToPreviousTarget = offsetToFutureTarget - 7;
        if(offsetToFutureTarget > 3)
            return plusDays(offsetToPreviousTarget);
        return plusDays(offsetToFutureTarget);
    }

    public DayDate getEndOfMonth() {
       Month month = getMonth();
       int year = getYear();
       int lastDay = lastDayOfMonth(month,year);
       return DayDateFactory.makeDate(lastDay, month, year);
    }

    public Date toDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(), getMonth().toInt() - Month.JANUARY.toInt(),
                getDayOfMonth(), 0, 0, 0);
        return calendar.getTime();
    }
    public Day getDayOfWeek(){//????????? ????????? ?????????
        Day startingDay = getDayOfWeekForOrdinalZero(); //???????????? 0????????? 7????????? ??????
        int startingOffset = startingDay.index - Day.SUNDAY.index;
        return Day.fromInt((getOrdinalDay() + startingOffset) % 7 + 1);
    }
    public int daysSince(DayDate other) {
        return getOrdinalDay() - other.getOrdinalDay();
    }
    public boolean isOn(DayDate other){
        return getOrdinalDay() == other.getOrdinalDay();
    }
    public boolean isBefore(DayDate other){
        return getOrdinalDay() < other.getOrdinalDay();
    }
    public boolean isOnOrBefore(DayDate other){
        return getOrdinalDay() <= other.getOrdinalDay();
    }
    public boolean isAfter(DayDate other){
        return getOrdinalDay() > other.getOrdinalDay();
    }
    public boolean isOnOrAfter(DayDate other){
        return getOrdinalDay() >= other.getOrdinalDay();
    }
    public boolean isInRange(DayDate d1, DayDate d2){
        return isInRange(d1, d2, CLOSED);
    }
    public boolean isInRange(DayDate d1, DayDate d2, DateInterval interval){
        int left = Math.min(d1.getOrdinalDay(), d2.getOrdinalDay());
        int right = Math.max(d1.getOrdinalDay(), d2.getOrdinalDay());
        return interval.isIn(getOrdinalDay(), left, right);
    }

}
