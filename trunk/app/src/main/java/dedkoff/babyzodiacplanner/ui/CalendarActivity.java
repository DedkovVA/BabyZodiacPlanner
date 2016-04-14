package dedkoff.babyzodiacplanner.ui;

import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import dedkoff.babyzodiacplanner.util.AppResources;
import dedkoff.babyzodiacplanner.R;
import dedkoff.babyzodiacplanner.core.DayMonth;
import dedkoff.babyzodiacplanner.core.DayMonthYear;
import dedkoff.babyzodiacplanner.core.DayToZodiac;
import dedkoff.babyzodiacplanner.core.Zodiac;
import dedkoff.babyzodiacplanner.core.ZodiacCompatibilitier;
import dedkoff.babyzodiacplanner.util.Util;

/**Calendar with zodiac signs and compatibility info*/
public class CalendarActivity extends OptionsMenuActivity implements OnClickListener {
    //UI elements
    private TextView currentMonth;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;

    private Calendar calendar;
    private int month, year;
    private static final Calendar NOW_CALENDAR = Calendar.getInstance(Locale.getDefault());

    private static String PREV_MONTH_DAY = "C1";
    private static String CURRENT_MONTH_DAY = "C2";
    private static String NEXT_MONTH_DAY = "C3";
    private static String CURRENT_DAY = "C4";

    private static final String MONTH_KEY = "MONTH_KEY";
    private static final String YEAR_KEY = "YEAR_KEY";

    private static final int MONTH_VAL_DEF = -1;
    private static final int YEAR_VAL_DEF = -1;

    private static final Locale defaultLocale = Locale.getDefault();
    private static final DateFormatSymbols symbols = new DateFormatSymbols(defaultLocale);

    private final String[] monthsDefault = symbols.getMonths();
    private final String[] monthsRu = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    private final Map<DayMonth, Zodiac> dayMonthZodiacMap = DayToZodiac.get();

    private Set<Zodiac> selectedZodiacSet;

    private Map<Zodiac, Map<Zodiac, Integer>> compatibilitier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCalendar();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(BABY_ZODIAC_PLANNER_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(MONTH_KEY, month);
        editor.putInt(YEAR_KEY, year);

        // Commit the edits!
        editor.apply();
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
    /**Handler for buttons "Next month" & "Prev month"*/
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else {
                month++;
            }
            setGridCellAdapterToDate(month, year);
        }
    }

    private static Set<Zodiac> toZodiacSet(Set<String> zodiacs) {
        Set<Zodiac> zodiacSet = new TreeSet<Zodiac>();
        for (String zodiac : zodiacs) {
            zodiacSet.add(Zodiac.valueOf(zodiac));
        }
        return zodiacSet;
    }

    /**Show calendar with zodiac signs*/
    private void showCalendar() {
        if (compatibilitier == null) {
            InputStream is = getResources().openRawResource(R.raw.zodiac_compatibility);
            compatibilitier = ZodiacCompatibilitier.get(is);
        }

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(BABY_ZODIAC_PLANNER_PREFS, 0);
        month = settings.getInt(MONTH_KEY, MONTH_VAL_DEF);
        year = settings.getInt(YEAR_KEY, YEAR_VAL_DEF);

        selectedZodiacSet = toZodiacSet(settings.getStringSet(ZODIACS_KEY, new HashSet<String>()));

        calendar = Calendar.getInstance(Locale.getDefault());
        if (month == MONTH_VAL_DEF || year == YEAR_VAL_DEF) {
            month = calendar.get(Calendar.MONTH) + 1;
            year = calendar.get(Calendar.YEAR);
        }

        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        setContentView(R.layout.activity_calendar);
        super.addAdvertising();

        prevMonth = (ImageView) findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) findViewById(R.id.currentMonth);
        installTextInCurrentMonth();

        nextMonth = (ImageView) findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        GridView weekDays = (GridView) findViewById(R.id.weekDays);
        calendarView = (GridView) findViewById(R.id.calendar);

        weekDays.setAdapter(new GridWeekDaysAdapter(getApplicationContext()));

        GridCellAdapter adapter = new GridCellAdapter(getApplicationContext(), month, year);
        calendarView.setAdapter(adapter);
    }

    private void installTextInCurrentMonth() {
        if (isRu()) {
            currentMonth.setText(calendar.get(Calendar.YEAR) + " " + monthsRu[calendar.get(Calendar.MONTH)]);
        } else {
            currentMonth.setText(calendar.get(Calendar.YEAR) + " " + monthsDefault[calendar.get(Calendar.MONTH)]);
        }
    }

    private void setGridCellAdapterToDate(int month, int year) {
        calendar.set(year, month - 1, 1);

        installTextInCurrentMonth();

        GridCellAdapter adapter = new GridCellAdapter(getApplicationContext(), month, year);
        calendarView.setAdapter(adapter);
    }

    /**Grid adapter for week days view*/
    private class GridWeekDaysAdapter extends BaseAdapter {
        private final Context _context;

        private final String[] weekDays = new String[7];

        //In Russia week starts from Monday
        public GridWeekDaysAdapter(Context context) {
            this._context = context;

            String[] weekDaysTmp = symbols.getShortWeekdays();
            for (int i = 1; i < weekDaysTmp.length; i++) {
                int newOrder = (i - calendar.getFirstDayOfWeek() + 7) % 7;
                String weekDay = weekDaysTmp[i].trim();
                if (!weekDay.isEmpty()) {
                    String firstLetter = weekDay.substring(0, 1).toUpperCase();
                    String tail = weekDay.substring(1, weekDay.length());
                    weekDay = firstLetter + tail;
                }
                weekDays[newOrder] = weekDay;
            }
        }

        @Override
        /**All cells in grid*/
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int position) {
            return weekDays[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inflater.inflate(R.layout.weekday_gridcell, null);
            Button gridcell = (Button) row.findViewById(R.id.week_day_gridcell);

            String weekDay = weekDays[position];
            gridcell.setText(weekDay);

            return row;
        }
    }

    /**Grid adapter for calendar view*/
    private class GridCellAdapter extends BaseAdapter implements OnClickListener {
        private final Context _context;

        private final List<String> calendarDayTags;
        private final List<View> views;
        private final List<Button> buttons;
        private final Map<String, DayMonthYear> dayMonthYears;
        private static final int DAY_OFFSET = 1;

        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private static final int MAX_DAYS_IN_MONTH = 42;
        private int daysInMonth;

        private int preListSize = 0;

        // Days in Current Month
        public GridCellAdapter(Context context, final int month, final int year) {
            super();
            this._context = context;
            this.calendarDayTags = Collections.synchronizedList(new ArrayList<String>());
            this.views = Collections.synchronizedList(new ArrayList<View>());
            this.buttons = Collections.synchronizedList(new ArrayList<Button>());
            this.dayMonthYears = Collections.synchronizedMap(new HashMap<String, DayMonthYear>());

            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int i = 0; i < MAX_DAYS_IN_MONTH; i++) {
                View view = inflater.inflate(R.layout.screen_gridcell, null);
                views.add(view);

                Button button = (Button) view.findViewById(R.id.calendar_day_gridcell);
                buttons.add(button);
            }

            //Calculate zodiac sign for each day in calendar after pregnancy period
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    int trailingSpaces;
                    int daysInPrevMonth;
                    int prevMonth;
                    int prevYear;
                    int nextMonth;
                    int nextYear;

                    int currentMonth = month - 1;
                    daysInMonth = getNumberOfDaysOfMonth(currentMonth);

                    GregorianCalendar cal = new GregorianCalendar(year, currentMonth, 1);

                    if (currentMonth == 11) {
                        prevMonth = currentMonth - 1;
                        daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                        nextMonth = 0;
                        prevYear = year;
                        nextYear = year + 1;
                    } else if (currentMonth == 0) {
                        prevMonth = 11;
                        prevYear = year - 1;
                        nextYear = year;
                        daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                        nextMonth = 1;
                    } else {
                        prevMonth = currentMonth - 1;
                        nextMonth = currentMonth + 1;
                        nextYear = year;
                        prevYear = year;
                        daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                    }

                    int dayOrder = (cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek() + 7) % 7 + 1;
                    trailingSpaces = dayOrder - 1;

                    if (cal.isLeapYear(cal.get(Calendar.YEAR))) {
                        if (month == 2) {
                            ++daysInMonth;
                        } else if (month == 3) {
                            ++daysInPrevMonth;
                        }
                    }

                    preListSize = trailingSpaces + daysInMonth;
                    int tail = preListSize % 7;
                    int leadingCount = 0;
                    if (tail != 0) {
                        leadingCount = 7 - tail;
                        preListSize = preListSize + (7 - tail);
                    }

                    // Trailing Month days
                    for (int i = 0; i < trailingSpaces; i++) {
                        int day = (daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i;
                        String value = String
                                .valueOf(day)
                                + "-" + PREV_MONTH_DAY
                                + "-"
                                + getMonthAsString(prevMonth)
                                + "-"
                                + prevYear;
                        calendarDayTags.add(value);
                        dayMonthYears.put(value, new DayMonthYear(day, prevMonth, prevYear));
                    }

                    // Current Month Days
                    for (int i = 1; i <= daysInMonth; i++) {
                        String value;
                        if (NOW_CALENDAR.get(Calendar.YEAR) == year &&
                                NOW_CALENDAR.get(Calendar.MONTH) == currentMonth &&
                                NOW_CALENDAR.get(Calendar.DAY_OF_MONTH) == i) {
                            value = String.valueOf(i) + "-" + CURRENT_DAY + "-"
                                    + getMonthAsString(currentMonth) + "-" + year;
                            calendarDayTags.add(value);
                        } else {
                            value = String.valueOf(i) + "-" + CURRENT_MONTH_DAY + "-"
                                    + getMonthAsString(currentMonth) + "-" + year;
                            calendarDayTags.add(value);
                        }
                        dayMonthYears.put(value, new DayMonthYear(i, currentMonth, year));
                    }


                    // Leading Month days
                    for (int i = 0; i < leadingCount; i++) {
                        String value = String.valueOf(i + 1) + "-" + NEXT_MONTH_DAY + "-"
                                + getMonthAsString(nextMonth) + "-" + nextYear;
                        calendarDayTags.add(value);
                        dayMonthYears.put(value, new DayMonthYear(i + 1, nextMonth, nextYear));
                    }

                    for (int i = 0; i < calendarDayTags.size(); i++) {
                        // Get a reference to the Day gridcell
                        final Button button = buttons.get(i);

                        final String[] day_cn_month_year = calendarDayTags.get(i).split("-");
                        String theDay = day_cn_month_year[0];
                        String theMonth = day_cn_month_year[2];
                        String theYear = day_cn_month_year[3];

                        DayMonthYear calendar = dayMonthYears.get(calendarDayTags.get(i));
                        Calendar birthdayCalendar = Calendar.getInstance(Locale.getDefault());
                        birthdayCalendar.set(calendar.getYear(), calendar.getMonth(), calendar.getDay());
                        //Add count of pregnancy weeks (in average 38)
                        birthdayCalendar.add(Calendar.DAY_OF_MONTH, 7 * 38);

                        DayMonth dayMonth = new DayMonth(birthdayCalendar.get(Calendar.DAY_OF_MONTH), birthdayCalendar.get(Calendar.MONTH));
                        Zodiac zodiac = dayMonthZodiacMap.get(dayMonth);

                        final Spannable spannable = createSpannable(zodiac, theDay, day_cn_month_year[1].equals(CURRENT_DAY));

                        int birthDay = birthdayCalendar.get(Calendar.DAY_OF_MONTH);
                        String birthMonth = getMonthAsString(birthdayCalendar.get(Calendar.MONTH));
                        int birthYear = birthdayCalendar.get(Calendar.YEAR);
                        final String buttonTag = String.format("%s-%s-%s-%s-%s-%s-%s", theDay, theMonth, theYear, zodiac.name(), birthDay, birthMonth, birthYear);

                        //Paint calendar with zodiac signs
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Hack For Android API 21 & 22
                                button.setTransformationMethod(null);
                                button.setOnClickListener(GridCellAdapter.this);

                                if (day_cn_month_year[1].equals(CURRENT_MONTH_DAY)) {
                                    button.setTextColor(getResources().getColor(R.color.black));
                                } else if (day_cn_month_year[1].equals(PREV_MONTH_DAY) || day_cn_month_year[1].equals(NEXT_MONTH_DAY)) {
                                    button.setTextColor(getResources().getColor(R.color.gray));
                                    button.setTypeface(null, Typeface.ITALIC);
                                }

                                button.setTag(buttonTag);
                                button.setText(spannable);
                            }
                        });
                    }

                    return null;
                }

                protected void onPostExecute(Void result) {
                    notifyDataSetChanged();
                }

                /**
                 * @param theDay calendar day
                 * @param zodiac zodiac sign for that day
                 * @return content for calendar day*/
                private Spannable createSpannable(Zodiac zodiac, String theDay, boolean today) {
                    Map<Zodiac, Integer> zodiacs = compatibilitier.get(zodiac);
                    int goodZodiacs = 0;
                    int badZodiacs = 0;
                    for (Zodiac z : selectedZodiacSet) {
                        if (zodiacs.get(z) == ZodiacCompatibilitier.GOOD || zodiacs.get(z) == ZodiacCompatibilitier.BEST) {
                            ++goodZodiacs;
                        } else if (zodiacs.get(z) == ZodiacCompatibilitier.POOR || zodiacs.get(z) == ZodiacCompatibilitier.CHALLENGING) {
                            ++badZodiacs;
                        }
                    }

                    // Set the Day GridCell
                    String underText = "           ";
                    if (goodZodiacs > 0 || badZodiacs > 0) {
                        if (badZodiacs > 0) {
                            underText = " " + badZodiacs;
                        }
                        if (goodZodiacs > 0) {
                            if (badZodiacs > 0) {
                                underText += "    ";
                            }
                            underText += goodZodiacs;
                        }
                    }

                    String upText = "    " + theDay + "  ";

                    Spannable span = new SpannableString(upText + Util.LINEBREAK + Util.LINEBREAK + underText);

                    span.setSpan(new ImageSpan(getApplicationContext(), zodiacDrawableMap.get(zodiac),
                            ImageSpan.ALIGN_BOTTOM), 0, 1, 0);

                    if (badZodiacs > 0) {
                        span.setSpan(new ImageSpan(getApplicationContext(), R.drawable.alert,
                                ImageSpan.ALIGN_BOTTOM), upText.length() + 2, upText.length() + 3, 0);
                    }

                    if (goodZodiacs > 0) {
                        span.setSpan(new ImageSpan(getApplicationContext(), R.drawable.star,
                                        ImageSpan.ALIGN_BOTTOM), upText.length() + underText.length(),
                                upText.length() + underText.length() + 1, 0);
                    }

                    span.setSpan(new RelativeSizeSpan(0.7f), 0, upText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new RelativeSizeSpan(0.5f), upText.length(), upText.length() + 2 + underText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    if (today) {
                        span.setSpan(new ForegroundColorSpan(Color.RED), 0, upText.length(), 0);
                        span.setSpan(new StyleSpan(Typeface.BOLD), 0, upText.length(), 0);
                    }

                    return span;
                }
            }.execute();
        }

        @Override
        public String getItem(int position) {
            return calendarDayTags.get(position);
        }

        @Override
        public int getCount() {
            return preListSize;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null ) {
                convertView = views.get(position);
            }

            Button button = (Button)convertView.findViewById(R.id.calendar_day_gridcell);
            button.setBackgroundColor(getResources().getColor(R.color.mint));
            return convertView;
        }

        @Override
        /**Click on cell with date and zodiac sign (show dialog with compatibility info)*/
        public void onClick(View view) {
            if (resources.getFutureDlgProps() == null) {
                InputStream is;
                if (isRu()) {
                    is = getResources().openRawResource(R.raw.future_dialog_ru);
                } else {
                    is = getResources().openRawResource(R.raw.future_dialog_en);
                }
                resources.setFutureDlgProps(new Properties());
                AppResources.loadProps(resources.getFutureDlgProps(), is);
            }

            String cellDay_zodiac_birthDay = (String) view.getTag();

            String[] cellInfo = cellDay_zodiac_birthDay.split("-");
            Zodiac zodiac = Zodiac.valueOf(cellInfo[3]);
            Map<Zodiac, Integer> zodiacs = compatibilitier.get(zodiac);

            StringBuilder sb = new StringBuilder();

            String futureZodiac = resources.getZodiacList().get(zodiac.ordinal());
            String zodiacInfo = String.format(resources.getFutureDlgProps().getProperty("dialog_preview"),
                    cellInfo[0], cellInfo[1].toUpperCase(), cellInfo[2], cellInfo[4], cellInfo[5].toUpperCase(), cellInfo[6], futureZodiac);

            sb.append(zodiacInfo);

            List<Zodiac> best = new ArrayList<Zodiac>();
            List<Zodiac> good = new ArrayList<Zodiac>();
            List<Zodiac> eitherWay = new ArrayList<Zodiac>();
            List<Zodiac> poor = new ArrayList<Zodiac>();
            List<Zodiac> challenging = new ArrayList<Zodiac>();

            for (Zodiac z : selectedZodiacSet) {
                if (zodiacs.get(z) == ZodiacCompatibilitier.BEST) {
                    best.add(z);
                } else if (zodiacs.get(z) == ZodiacCompatibilitier.GOOD) {
                    good.add(z);
                } else if (zodiacs.get(z) == ZodiacCompatibilitier.EITHER_WAY) {
                    eitherWay.add(z);
                } else if (zodiacs.get(z) == ZodiacCompatibilitier.POOR) {
                    poor.add(z);
                } else if (zodiacs.get(z) == ZodiacCompatibilitier.CHALLENGING) {
                    challenging.add(z);
                }
            }

            if (!(best.isEmpty() && good.isEmpty() && eitherWay.isEmpty() && poor.isEmpty() && challenging.isEmpty())) {
                sb.append(String.format(resources.getFutureDlgProps().getProperty("dialog_compatibility"), futureZodiac));

                sb.append(evalCompatibilityInfo(resources.getFutureDlgProps().getProperty("best"), best));
                sb.append(evalCompatibilityInfo(resources.getFutureDlgProps().getProperty("good"), good));
                sb.append(evalCompatibilityInfo(resources.getFutureDlgProps().getProperty("neutral"), eitherWay));
                sb.append(evalCompatibilityInfo(resources.getFutureDlgProps().getProperty("poor"), poor));
                sb.append(evalCompatibilityInfo(resources.getFutureDlgProps().getProperty("challenging"), challenging));
            } else {
                sb.append(String.format(resources.getFutureDlgProps().getProperty("dialog_empty"), futureZodiac));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
            builder.setTitle(resources.getFutureDlgProps().getProperty("dialog_title"))
                    .setMessage(sb.toString())
                    .setCancelable(false)
                    .setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            if (!sb.toString().isEmpty()) {
                alert.show();
            }
        }

        private String getMonthAsString(int i) {
            return monthsDefault[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        private String evalCompatibilityInfo(String compatibility, List<Zodiac> compList) {
            String result = "";
            if (!compList.isEmpty()) {
                result = compatibility + " (" + compList.size() + "):" + Util.LINEBREAK;
                for (Zodiac zodiac : compList) {
                    result += resources.getFutureDlgProps().get(zodiac.ordinal()) + ", ";
                }
                result = result.substring(0, result.length() - 2) + Util.LINEBREAK;
            }
            return result;
        }
    }
}