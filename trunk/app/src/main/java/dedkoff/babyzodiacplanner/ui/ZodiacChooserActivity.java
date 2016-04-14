package dedkoff.babyzodiacplanner.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dedkoff.babyzodiacplanner.R;
import dedkoff.babyzodiacplanner.core.Zodiac;
import dedkoff.babyzodiacplanner.util.Util;

/**Chooser of zodiac signs for test compatibility*/
public class ZodiacChooserActivity extends OptionsMenuActivity implements OnClickListener {

    private static final Set<Zodiac> zodiacSet = new HashSet<Zodiac>();

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showZodiacChooser();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    /**Click on zodiac sign - choose for compatibility test*/
    public void onClick(View v) {
        int position = (Integer)v.getTag();

        Zodiac zodiac = Zodiac.values()[position];

        if (zodiacSet.contains(zodiac)) {
            zodiacSet.remove(zodiac);
        } else {
            zodiacSet.add(zodiac);
        }

        paintBackgroundCell(v, zodiac);


        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(BABY_ZODIAC_PLANNER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(ZODIACS_KEY, toStringSet());

        // Commit the edits!
        editor.apply();
    }

    private void showZodiacChooser() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(BABY_ZODIAC_PLANNER_PREFS, 0);
        toZodiacSet(settings.getStringSet(ZODIACS_KEY, new HashSet<String>()));

        setContentView(R.layout.activity_zodiac_chooser);
        super.addAdvertising();

        GridView zodiacChooserView = (GridView) findViewById(R.id.zodiacChooserViewId);

        // Initialised
        GridCellAdapter adapter = new GridCellAdapter(getApplicationContext());
        zodiacChooserView.setAdapter(adapter);
    }

    private Set<String> toStringSet() {
        Set<String> zodiacsForSave = new HashSet<String>();
        for (Zodiac zodiac : zodiacSet) {
            zodiacsForSave.add(zodiac.name());
        }
        return zodiacsForSave;
    }

    private void toZodiacSet(Set<String> zodiacs) {
        zodiacSet.clear();
        for (String zodiac : zodiacs) {
            zodiacSet.add(Zodiac.valueOf(zodiac));
        }
    }

    private void paintBackgroundCell(View v, Zodiac zodiac) {
        if (zodiacSet.contains(zodiac)) {
            v.setBackgroundColor(getResources().getColor(R.color.deep_mint));
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.mint));
        }
    }

    /**Grid adapter for zodiac chooser*/
    private class GridCellAdapter extends BaseAdapter {
        private final Context _context;
        private List<View> views = Collections.synchronizedList(new ArrayList<View>());
        private List<Button> buttons = Collections.synchronizedList(new ArrayList<Button>());

        public GridCellAdapter(Context context) {
            super();
            this._context = context;

            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < 12; i++) {
                View view = inflater.inflate(R.layout.screen_gridcell_chooser, null);
                views.add(view);

                Button button = (Button) view.findViewById(R.id.calendar_day_gridcell_chooser);
                buttons.add(button);
            }
        }

        @Override
        /**All cells in grid*/
        public int getCount() {
            return 12;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        /**One grid cell view for zodiac chooser*/
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = views.get(position);
            } else {
                return convertView;
            }

            // Get a reference to the Day gridcell
            Button button = (Button) view.findViewById(R.id.calendar_day_gridcell_chooser);

            //For Android API 21 & 22
            button.setTransformationMethod(null);

            Zodiac zodiac = Zodiac.values()[position];

            String upText = "          ";
            String zodiacName = resources.getZodiacList().get(zodiac.ordinal());
            Spannable span = new SpannableString(upText + Util.LINEBREAK + Util.LINEBREAK + zodiacName);

            span.setSpan(new ImageSpan(getApplicationContext(), zodiacDrawableMap.get(zodiac),
                    ImageSpan.ALIGN_BOTTOM), 4, 5, 0);

            span.setSpan(new RelativeSizeSpan(0.8f), upText.length() + 2, upText.length() + 2 + zodiacName.length(), 0);
            button.setText(span);


            button.setTag(position);
            button.setOnClickListener(ZodiacChooserActivity.this);

            paintBackgroundCell(button, Zodiac.values()[position]);

            return view;
        }
    }
}
