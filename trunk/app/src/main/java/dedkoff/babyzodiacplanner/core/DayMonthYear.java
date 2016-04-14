package dedkoff.babyzodiacplanner.core;

/**
 * Created by Dedkov Vadim on 09.11.2014.
 */
public class DayMonthYear extends DayMonth {
    private int year;
    public DayMonthYear(int day, int month, int year) {
        super(day, month);
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DayMonthYear) {
            DayMonthYear dayMonthYear = (DayMonthYear) obj;
            return super.getDay() == dayMonthYear.getDay() &&
                    super.getMonth() == dayMonthYear.getMonth() &&
                    this.year == dayMonthYear.getYear();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.getDay() + super.getMonth() + year;
    }
}
