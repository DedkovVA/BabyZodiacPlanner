package dedkoff.babyzodiacplanner.core;

/**
 * Created by Dedkov Vadim on 21.09.14.
 */
public class DayMonth {
    public DayMonth(int day, int month) {
        this.day = day;
        this.month = month;
    }

    private int day;
    private int month;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DayMonth) {
            DayMonth dayMonth = (DayMonth) obj;
            return this.day == dayMonth.getDay() && this.month == dayMonth.getMonth();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return day + month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
