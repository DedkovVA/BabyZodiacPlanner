package dedkoff.babyzodiacplanner.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dedkoff.babyzodiacplanner.util.AppResources;
import dedkoff.babyzodiacplanner.R;
import dedkoff.babyzodiacplanner.core.Zodiac;
import dedkoff.babyzodiacplanner.util.Util;

/**
 * Application menu
 * Created by Dedkov Vadim on 21.10.2014.
 */
public class OptionsMenuActivity extends Activity {
    public static final String BABY_ZODIAC_PLANNER_PREFS = "BABY_ZODIAC_PLANNER_PREFS";
    protected static final String ZODIACS_KEY = "ZODIACS_KEY";

    protected AppResources resources = new AppResources();
    //Map - zodiac to icon
    protected static final Map<Zodiac, Integer> zodiacDrawableMap = new HashMap<Zodiac, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void addAdvertising() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_calendar:
                intent = new Intent(this, CalendarActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_choose_zodiacs:
                intent = new Intent(this, ZodiacChooserActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void init() {
        if (resources.getZodiacList() == null) {
            InputStream is;
            if (isRu()) {
                is = getResources().openRawResource(R.raw.zodiacs_ru);
            } else {
                is = getResources().openRawResource(R.raw.zodiacs_en);
            }
            resources.setZodiacList(AppResources.readFileToListListStr(is).get(0));
        }

        Integer aries = R.drawable.aries;
        Integer taurus = R.drawable.taurus;
        Integer gemini = R.drawable.gemini;
        Integer cancer = R.drawable.cancer;
        Integer leo = R.drawable.leo;
        Integer virgo = R.drawable.virgo;
        Integer libra = R.drawable.libra;
        Integer scorpio = R.drawable.scorpio;
        Integer sagittarius = R.drawable.sagittarius;
        Integer capricorn = R.drawable.capricorn;
        Integer aquarius = R.drawable.aquarius;
        Integer pisces = R.drawable.pisces;

        zodiacDrawableMap.put(Zodiac.ARIES, aries);
        zodiacDrawableMap.put(Zodiac.TAURUS, taurus);
        zodiacDrawableMap.put(Zodiac.GEMINI, gemini);
        zodiacDrawableMap.put(Zodiac.CANCER, cancer);
        zodiacDrawableMap.put(Zodiac.LEO, leo);
        zodiacDrawableMap.put(Zodiac.VIRGO, virgo);
        zodiacDrawableMap.put(Zodiac.LIBRA, libra);
        zodiacDrawableMap.put(Zodiac.SCORPIO, scorpio);
        zodiacDrawableMap.put(Zodiac.SAGITTARIUS, sagittarius);
        zodiacDrawableMap.put(Zodiac.CAPRICORN, capricorn);
        zodiacDrawableMap.put(Zodiac.AQUARIUS, aquarius);
        zodiacDrawableMap.put(Zodiac.PISCES, pisces);
    }

    //For Russia and Ukraine - Russian lang
    protected static boolean isRu() {
        String lang = Locale.getDefault().getLanguage();
        return Util.Lang.ru.name().equalsIgnoreCase(lang) || Util.Lang.uk.name().equalsIgnoreCase(lang);
    }
}
