package dedkoff.babyzodiacplanner.core;

/**
 * Created by Dedkov Vadim on 20.09.14.
 */
public enum Zodiac {
    ARIES(21, 2, 19, 3), TAURUS(20, 3, 20, 4), GEMINI(21, 4, 20, 5), CANCER(21, 5, 22, 6),
    LEO(23, 6, 22, 7), VIRGO(23, 7, 22, 8), LIBRA(23, 8, 22, 9), SCORPIO(23, 9, 21, 10),
    SAGITTARIUS(22, 10, 21, 11), CAPRICORN(22, 11, 19, 0), AQUARIUS(20, 0, 18, 1), PISCES(19, 1, 20, 2);

    //Start date for zodiac sign
    private int startDay;
    private int startMonth;
    //End date for zodiac sign
    private int endDay;
    private int endMonth;

    Zodiac(int startDay, int startMonth, int endDay, int endMonth) {
        this.startDay = startDay;
        this.startMonth = startMonth;
        this.endDay = endDay;
        this.endMonth = endMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }
}
