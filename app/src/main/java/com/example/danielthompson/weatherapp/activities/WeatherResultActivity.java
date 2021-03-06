package com.example.danielthompson.weatherapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.danielthompson.weatherapp.R;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.danielthompson.weatherapp.activities.SearchActivity.CITY_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.HUMIDITY_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.PRESSURE_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.RAIN_CHANCE_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.SUMMARY_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.TEMP_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.TIME_KEY;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.TIME_ZONE_KEY;

/**
 * Activity to display the results of the weather search.
 */
public class WeatherResultActivity extends AppCompatActivity {

    @BindView(R.id.cityName)
    TextView cityName;

    @BindView(R.id.temp)
    TextView temp;

    @BindView(R.id.pressure)
    TextView pressure;

    @BindView(R.id.humidity)
    TextView humidity;

    @BindView(R.id.chanceOfRain)
    TextView chanceOfRain;

    //Since this API has no short summary such as "Rain/Cloudy/Sunny/etc." this fulfills that requirement.
    @BindView(R.id.summary)
    TextView summary;

    @BindView(R.id.resultsBackground)
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {
            String city = intent.getStringExtra(CITY_KEY);

            //For now, capitalize the first letter. Multiple word cities will require a different method
            if (!Character.isUpperCase(city.charAt(0))) {
                city = city.substring(0, 1).toUpperCase() + city.substring(1);
            }

            cityName.setText(city);
            //Should use unicode char for degree symbol, but wouldn't work with formatting for some reason
            temp.setText(String.format("%d°", Math.round(intent.getDoubleExtra(TEMP_KEY, 0.0))));
            pressure.setText(String.format("Pressure: %1$,.2f hPa", intent.getDoubleExtra(PRESSURE_KEY, 0.0)));
            humidity.setText(String.format("Humidity: %1$,.2f%%", intent.getDoubleExtra(HUMIDITY_KEY, 0.0)));
            //Note: Rain is a probability. So it is out of a total of 1.
            chanceOfRain.setText(String.format("Rain probability: %1$,.2f", intent.getDoubleExtra(RAIN_CHANCE_KEY, 0.0)));
            summary.setText(intent.getStringExtra(SUMMARY_KEY));

            setResultBackground(intent.getIntExtra(TIME_KEY, 0) * 1000L,
                    intent.getStringExtra(TIME_ZONE_KEY));
        }

        showActionBar();
    }

    /**
     * Use a custom actionbar so a backarrow can be used.
     */
    private void showActionBar() {
        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_actionbar, null);
        v.findViewById(R.id.backButton).setOnClickListener(l -> onBackPressed());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    }

    /**
     * Set the background of the results page to either "day" or "night" theme.
     *
     * Since we don't have access to "today's" sunset time, a hardcoded "sunset time" of 7pm
     * is being set. This is to simulate the results page having either the "day" or "night"
     * background depending on the time.
     *
     * Realistically, this would be based on the actual sunset time, but that isn't provided
     * in the simple details, and is only provided in the 'daily' results.
     *
     * @param currentHourLong - The current hour in the given location in unix time
     */
    private void setResultBackground(long currentHourLong, String timezone) {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(currentHourLong);
        time.setTimeZone(TimeZone.getTimeZone(timezone));
        int hour = time.get(Calendar.HOUR_OF_DAY); //Hour of day in 24hour time.

        //If its later than 7pm or earlier than 7am, show the 'night' background
        if (hour >= 19 || hour <= 7) {
            background.setImageDrawable(getResources().getDrawable(R.drawable.sky_night));
        }
    }

}
