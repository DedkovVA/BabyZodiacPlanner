package dedkoff.babyzodiacplanner.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import dedkoff.babyzodiacplanner.core.Zodiac;

/**
 * For parsing app file resources
 * Created by Dedkov Vadim on 11.05.2015.
 */
public class AppResources {
    private List<String> zodiacList;
    private Properties futureDlgProps;

    /**Read UTF-8 file line by line with word separator comma to list of string lists*/
    public static List<List<String>> readFileToListListStr(InputStream is) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, Util.UTF8));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(Util.LINEBREAK);
                line = br.readLine();
            }
            String everything = sb.toString();

            String[] zodiacList = everything.split(Util.LINEBREAK);
            List<List<String>> result = new ArrayList<List<String>>();
            for (String str : zodiacList) {
                String[] stringArray = str.split(",");
                result.add(Arrays.asList(stringArray));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**Read UTF-8 file line by line with word separator comma to list of zodiac lists*/
    public static List<List<Zodiac>> readFileToListListZodiac(InputStream is) {
        List<List<Zodiac>> result = new ArrayList<List<Zodiac>>();

        List<List<String>> zodiacStrListList = readFileToListListStr(is);

        for (List<String> zodiacStrList : zodiacStrListList) {
            for (String zodiacStr : zodiacStrList) {
                List<Zodiac> zodiacList = new ArrayList<Zodiac>();
                zodiacList.add(Zodiac.valueOf(zodiacStr));
                result.add(zodiacList);
            }
        }
        return result;
    }

    /**Load to @param props properties from input stream*/
    public static void loadProps(Properties props, InputStream is) {
        try {
            props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> getZodiacList() {
        return zodiacList;
    }

    public void setZodiacList(List<String> zodiacList) {
        this.zodiacList = zodiacList;
    }

    public Properties getFutureDlgProps() {
        return futureDlgProps;
    }

    public void setFutureDlgProps(Properties futureDlgProps) {
        this.futureDlgProps = futureDlgProps;
    }
}
