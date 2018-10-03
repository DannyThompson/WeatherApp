package com.example.danielthompson.weatherapp.services.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.danielthompson.weatherapp.R;
import com.example.danielthompson.weatherapp.WeatherResponse;
import com.example.danielthompson.weatherapp.services.WeatherService;

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

public class SearchActivity extends AppCompatActivity {
    public static final String API_BASE = "https://api.darksky.net/forecast/35ea4a8e06ddf0e326155550860ee7da/";
    public static final String TAG = "SearchActivityTag";
    public static final String TEMP_KEY = "temp";
    public static final String RAIN_CHANCE_KEY = "rain";
    public static final String SUMMARY_KEY = "summary";
    public static final String HUMIDITY_KEY = "humidity";
    public static final String PRESSURE_KEY = "pressure";
    public static final String CITY_KEY = "city";


    @BindView(R.id.citySearch)
    EditText citySearch;

    String defaultSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        defaultSearchText = getResources().getString(R.string.search_hint);
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
        String locality = citySearch.getText().toString();
        Double latitude = Double.MAX_VALUE;
        Double longitude = Double.MAX_VALUE;

        if (!locality.equals("") && !locality.equals(defaultSearchText)) {
            Geocoder coder = new Geocoder(this);
            List<Address> address;

            try {
                address = coder.getFromLocationName(locality, 5);
                Address location = address.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (TextUtils.isDigitsOnly(locality)) {
                    locality = location.getLocality();
                }

            } catch (Exception e) { //Catch exception, and let user know to try again.
                Toast toast = Toast.makeText(SearchActivity.this,
                        "No data available for query. Try again.",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                Timber.d(e, "%s: Error retrieving city data.", TAG);
            }
        }

        if (latitude != Double.MAX_VALUE && longitude != Double.MAX_VALUE) {
            callWeatherService(latitude, longitude, locality);
        }
    }

    public void callWeatherService(Double lat, Double lon, final String locality) {
        Timber.d("%s: calling weather service", TAG);

        String latlon = lat.toString() + "," + lon.toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<WeatherResponse> weather = weatherService.getWeather(latlon);

        weather.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse body = response.body();
                    if (body != null && body.details != null) {
                        WeatherResponse.WeatherDetails details = body.details;
                        Intent intent = new Intent();
                        intent.putExtra(TEMP_KEY, details.temperature);
                        intent.putExtra(SUMMARY_KEY, details.currentCondition);
                        intent.putExtra(HUMIDITY_KEY, details.humidity);
                        intent.putExtra(PRESSURE_KEY, details.pressure);
                        intent.putExtra(RAIN_CHANCE_KEY, Math.floor(details.chanceOfRain));
                        intent.putExtra(CITY_KEY, locality);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Timber.d("%s: Error getting weather response.", TAG);
            }
        });

    }
}
