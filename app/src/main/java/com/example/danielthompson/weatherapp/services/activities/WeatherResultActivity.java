package com.example.danielthompson.weatherapp.services.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.danielthompson.weatherapp.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.CITY_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.HUMIDITY_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.PRESSURE_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.RAIN_CHANCE_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.SUMMARY_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.SUNSET_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.TEMP_KEY;
import static com.example.danielthompson.weatherapp.services.activities.SearchActivity.TIME_KEY;

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

    //Since this API has no short summart such as "Rain/Cloudy/Sunny/etc." this fulfills that requirement.
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

        if(intent != null) {
            String city = intent.getStringExtra(CITY_KEY);

            //Capitalize city if it isn't already.
            if(!Character.isUpperCase(city.charAt(0))) {
                city = city.substring(0, 1).toUpperCase() + city.substring(1);
            }

            cityName.setText(city);
            //Should use unicode char for degree symbol, but wouldn't work with formatting for some reason
            temp.setText(String.format("%d°", Math.round(intent.getDoubleExtra(TEMP_KEY, 0.0))));
            pressure.setText(String.format("Pressure: %1$,.2f hPa", intent.getDoubleExtra(PRESSURE_KEY, 0.0)));
            humidity.setText(String.format("Humidity: %1$,.2f%%" ,intent.getDoubleExtra(HUMIDITY_KEY, 0.0)));
            chanceOfRain.setText(String.format("Chance of rain: %d%%", intent.getIntExtra(RAIN_CHANCE_KEY, 0)));
            summary.setText(intent.getStringExtra(SUMMARY_KEY));

            //If its after sunset, use the 'nighttime' background.
            if(intent.getIntExtra(TIME_KEY, 0) >= intent.getIntExtra(SUNSET_KEY, 0)) {
                background.setImageDrawable(getResources().getDrawable(R.drawable.sky_night));
            }

        }
    }
}
