package com.example.danielthompson.weatherapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.danielthompson.weatherapp.R;
import com.example.danielthompson.weatherapp.services.WeatherResponse;
import com.example.danielthompson.weatherapp.services.WeatherServiceHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Functions as the main activity to perform searches.
 * One can search by city name, or if that yields no results, a postal code.
 * (Ex: tested this by using my hometown of Brentwood. No results, but its zip 94513 did yield results).
 */

public class SearchActivity extends AppCompatActivity {
    public static final String API_BASE = "https://api.darksky.net/forecast/35ea4a8e06ddf0e326155550860ee7da/";
    public static final String TAG = "SearchActivityTag";
    public static final String TEMP_KEY = "temp";
    public static final String RAIN_CHANCE_KEY = "rain";
    public static final String SUMMARY_KEY = "summary";
    public static final String HUMIDITY_KEY = "humidity";
    public static final String PRESSURE_KEY = "pressure";
    public static final String CITY_KEY = "city";
    public static final String TIME_KEY = "time";
    public static final String TIME_ZONE_KEY = "timezone";

    @BindView(R.id.citySearch)
    EditText citySearch;

    private String defaultSearchText;

    private WeatherServiceHandler weatherServiceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());


        defaultSearchText = getResources().getString(R.string.search_hint);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherServiceHandler = new WeatherServiceHandler(this, retrofit);

    }

    @OnClick(R.id.citySearch)
    public void onEditTextClick() {
        if (citySearch.getText().toString().equals(defaultSearchText)) {
            citySearch.getText().clear();
            Timber.d("%s: Clearing default search text.", TAG);
        }
    }

    /**
     * Upon clicking "Get Weather, this will have the service handler obtain the data for the weather.
     */
    @OnClick(R.id.getWeather)
    public void onGetWeatherClick() {
        Toast toast = Toast.makeText(this,
                "Retrieving weather data.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        //The API call was causing some frames to be skipped, so run this on a new thread
        //Could potential be improved with RxJava in the future?
        new Thread(() ->  weatherServiceHandler.getWeather(citySearch.getText().toString(),
                defaultSearchText)).start();

    }

    @OnClick(R.id.locationSearch)
    public void onLocationClick() {
        weatherServiceHandler.getLocation();
    }

    @OnClick(R.id.clear)
    public void onClickClear() {
        citySearch.getText().clear();
    }

    public void setCitySearchText(String text) {
        citySearch.setText(text);
    }

    /**
     * Start the WeatherResultActivity with the details from the WeatherResponse.
     *
     * @param details  - The details of the weather (temp, humidity, etc.) retrieved from the API.
     * @param locality - The city that the weather is being requested for.
     */
    public void startResultsActivity(WeatherResponse.WeatherDetails details, String timezone, String locality) {
        Intent intent = new Intent(this, WeatherResultActivity.class);

        intent.putExtra(TEMP_KEY, details.temperature);
        intent.putExtra(SUMMARY_KEY, details.currentCondition);
        intent.putExtra(HUMIDITY_KEY, details.humidity);
        intent.putExtra(PRESSURE_KEY, details.pressure);
        intent.putExtra(RAIN_CHANCE_KEY, details.chanceOfRain);
        intent.putExtra(CITY_KEY, locality);
        intent.putExtra(TIME_KEY, details.time);
        intent.putExtra(TIME_ZONE_KEY, timezone);

        startActivity(intent);
    }

    /**
     * Show a toast, notifying the user that no data was retrieved, for one reason or another.
     * Then logs the exception.
     *
     * @param e - Exception
     */
    public void errorRetrivingDataToast(Exception e) {
        Toast toast = Toast.makeText(this,
                "No data available for query. Try again.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Timber.d(e, "%s: Error retrieving city data.", TAG);
    }
}
