package ch14.date;

public enum WeekInMonth {

    LAST(0),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);

    public int index;

    WeekInMonth(int index){
        this.index = index;
    }

    String toString(int index){
        for(WeekInMonth w : WeekInMonth.values()){
            if(index == w.index) return w.name();
        }
        throw new IllegalArgumentException("No WIM");
    }

}
