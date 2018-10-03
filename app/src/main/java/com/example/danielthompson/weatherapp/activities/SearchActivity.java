package com.example.danielthompson.weatherapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.danielthompson.weatherapp.R;
import com.example.danielthompson.weatherapp.WeatherResponse;
import com.example.danielthompson.weatherapp.WeatherServiceHandler;
import com.example.danielthompson.weatherapp.services.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    public static final String SUNSET_KEY = "sunset";

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
        weatherServiceHandler = new WeatherServiceHandler(this);
    }

    @OnClick(R.id.citySearch)
    public void onEditTextClick() {
        if (citySearch.getText().toString().equals(defaultSearchText)) {
            citySearch.getText().clear();
            Timber.d("%s: Clearing default search text.", TAG);
        }
    }

    @OnClick(R.id.getWeather)
    public void onGetWeatherClick() {
        weatherServiceHandler.getWeather(citySearch.getText().toString(), defaultSearchText);
    }

    @OnClick(R.id.locationSearch)
    public void onLocationClick() {
        weatherServiceHandler.getLocation();
    }

    public void setCitySearchText(String text) {
        citySearch.setText(text);
    }


    public void startResultsActivity(WeatherResponse.WeatherDetails details, String locality) {
        Intent intent = new Intent(this, WeatherResultActivity.class);

        intent.putExtra(TEMP_KEY, details.temperature);
        intent.putExtra(SUMMARY_KEY, details.currentCondition);
        intent.putExtra(HUMIDITY_KEY, details.humidity);
        intent.putExtra(PRESSURE_KEY, details.pressure);
        intent.putExtra(RAIN_CHANCE_KEY, Math.floor(details.chanceOfRain));
        intent.putExtra(CITY_KEY, locality);
        intent.putExtra(TIME_KEY, details.time);
        intent.putExtra(SUNSET_KEY, details.sunsetTime);

        startActivity(intent);
    }
}
