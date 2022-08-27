package ch14.date;

import java.text.DateFormatSymbols;

public enum Month {
    JANUARY(1), FEBRUARY(2), MARCH(3), APRIL(4), MAY(5), JUNE(6),
    JULY(7), AUGUST(8), SEPTEMBER(9),OCTOBER (10),NOVEMBER (11)
    , DECEMBER (12);
    public int index;
    private static DateFormatSymbols dateSymbols = new DateFormatSymbols();
    static final int[] LAST_DAY_OF_MONTH =
            {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    Month(int index){
        this.index=index;
    }
    static Month make(int index){
        for(Month m : Month.values()){
            if(m.index == index){
                return m;
            }
        }
        throw new IllegalArgumentException("No Month");
    }

    public int quarter(){
        return 1 + (index-1)/3;
    }

    public static Month parse(String s){
        s = s.trim();
        for(Month m : Month.values()){
            if(m.matches(s)){
                return m;
            }
            try{
                return make(Integer.parseInt(s));
            }catch (Exception ignored){}
        }throw new IllegalArgumentException("No Month");
    }

    public int toInt(){
        return index;
    }

    private boolean matches(String s) {
        return s.equalsIgnoreCase(toShortString()) || s.equalsIgnoreCase(toString());
    }
    public int lastDay() {
        return LAST_DAY_OF_MONTH[index];
    }
    @Override
    public String toString() {
        return dateSymbols.getMonths()[index-1];
    }
    public String toShortString() {
        return dateSymbols.getShortMonths()[index-1];
    }


}
