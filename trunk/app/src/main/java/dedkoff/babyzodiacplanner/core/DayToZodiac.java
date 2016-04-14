package dedkoff.babyzodiacplanner.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dedkov Vadim on 21.09.14.
 */
public class DayToZodiac {
    //Each zodiac sign for each day in the year
    private static Map<DayMonth, Zodiac> map;
    public static Map<DayMonth, Zodiac> get() {
        if (map == null) {
            map = new HashMap<DayMonth, Zodiac>();
            for (int month = 0; month < 12; month++) {
                for (int day = 1; day < 32; day++) {
                    for (Zodiac zodiac : Zodiac.values()) {
                        if (zodiac.getStartMonth() == month && zodiac.getStartDay() <= day
                                || zodiac.getEndMonth() == month && zodiac.getEndDay() >= day) {
                            DayMonth dayMonth = new DayMonth(day, month);
                            map.put(dayMonth, zodiac);
                            break;
                        }
                    }
                }
            }
        }
        return map;
    }
}
