package dedkoff.babyzodiacplanner.core;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dedkoff.babyzodiacplanner.util.AppResources.readFileToListListZodiac;

/**
 * Created by Dedkov Vadim on 13.12.2014.
 */
public class ZodiacCompatibilitier {
    //Map of compatibility one zodiac sign with another zodiac signs
    private static Map<Zodiac, Map<Zodiac, Integer>> compatibilityMap;

    public static final int CHALLENGING = 1;
    public static final int POOR = 2;
    public static final int EITHER_WAY = 3;
    public static final int GOOD = 4;
    public static final int BEST = 5;

    public static Map<Zodiac, Map<Zodiac, Integer>> get(InputStream is) {
        if (compatibilityMap == null) {
            compatibilityMap = new HashMap<Zodiac, Map<Zodiac, Integer>>();
            fillMap(is);
        }
        return compatibilityMap;
    }

    private static void fillMap(InputStream is) {
        List<List<Zodiac>> listList = readFileToListListZodiac(is);
        for (List<Zodiac> zodiacs : listList) {
            Map<Zodiac, Integer> map = new HashMap<Zodiac, Integer>();
            compatibilityMap.put(zodiacs.get(0), map);
            for (int i = 1; i <= 12; i++) {
                //Worst compatibility
                if (i == 1) {
                    map.put(zodiacs.get(i), CHALLENGING);
                } else if (i == 2 || i == 3 || i == 4) {
                    map.put(zodiacs.get(i), POOR);
                } else if (i == 5 || i == 6 || i == 7) {
                    map.put(zodiacs.get(i), EITHER_WAY);
                } else if (i == 8 || i == 9 || i == 10) {
                    map.put(zodiacs.get(i), GOOD);
                //Best compatibility
                } else if (i == 11 || i == 12) {
                    map.put(zodiacs.get(i), BEST);
                }
            }
        }
    }
}
