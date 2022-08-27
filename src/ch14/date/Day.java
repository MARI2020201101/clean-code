package ch14.date;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public enum Day {
    MONDAY(Calendar.MONDAY)
    , TUESDAY(Calendar.TUESDAY)
    , WEDNESDAY(Calendar.WEDNESDAY)
    , THURSDAY(Calendar.THURSDAY)
    , FRIDAY(Calendar.FRIDAY)
    , SATURDAY(Calendar.SATURDAY)
    , SUNDAY(Calendar.SUNDAY);

    public final int index;
    private static DateFormatSymbols dateSymbols = new DateFormatSymbols();

    Day(int day){
        this.index = day;
    }
    public static Day fromInt(int index){
        for(Day d : Day.values()){
            if(d.index == index)
                return d;
        }throw new IllegalArgumentException("No Day");
    }

    public static Day parse(String s){
        String[] shortWeekDayNames = dateSymbols.getShortWeekdays();
        String[] weekDayNames = dateSymbols.getWeekdays();

        s = s.trim();
        for(Day day : Day.values()){
            if(s.equalsIgnoreCase(shortWeekDayNames[day.index]) || s.equalsIgnoreCase(weekDayNames[day.index])){
                return day;
            }
        }throw new IllegalArgumentException("No Day");
    }

    @Override
    public String toString() {
        return dateSymbols.getWeekdays()[index];
    }

    public int toInt(){
        return index;
    }
}
