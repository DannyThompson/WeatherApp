package com.example.danielthompson.weatherapp.services;

import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.example.danielthompson.weatherapp.activities.SearchActivity;
import com.example.danielthompson.weatherapp.provider.LocationProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.example.danielthompson.weatherapp.activities.SearchActivity.API_BASE;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.TAG;

/**
 * Handles the workings of the WeatherService and actual retrieval of the weather data.
 * Also uses a location provider if the user wishes to use the location button.
 */

public class WeatherServiceHandler {

    private SearchActivity activity;
    private Geocoder coder;

    public WeatherServiceHandler(SearchActivity activity) {
        this.activity = activity;
        coder = new Geocoder(activity);
    }

    public void getWeather(String cityText, String defaultSearchText) {
        String locality = cityText;
        Double latitude = Double.MAX_VALUE;
        Double longitude = Double.MAX_VALUE;

        if (!locality.equals("") && !locality.equals(defaultSearchText)) {
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
                activity.errorRetrivingDataToast(e);
            }
        }

        if (latitude != Double.MAX_VALUE && longitude != Double.MAX_VALUE) {
            callWeatherService(latitude, longitude, locality);
        }
    }

    public void getLocation() {
        LocationProvider provider = new LocationProvider(activity, coder);
        provider.getLocation();
    }

    /**
     * Utilizes the WeatherService to retrieve the data asynchronously from the API.
     * Then has the activity start a new Result-based activity to display the results.
     *
     * @param lat      - Latitude of the city being searched.
     * @param lon      - Longitude of the city being Searched.
     * @param locality - The city name, or, possibly the zipcode which will be converted to city name.
     */
    private void callWeatherService(Double lat, Double lon, final String locality) {
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
                        activity.startResultsActivity(body.details, body.timezone, locality);
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
