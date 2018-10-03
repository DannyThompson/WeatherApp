package com.example.danielthompson.weatherapp;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.example.danielthompson.weatherapp.activities.SearchActivity;
import com.example.danielthompson.weatherapp.services.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.example.danielthompson.weatherapp.activities.SearchActivity.API_BASE;
import static com.example.danielthompson.weatherapp.activities.SearchActivity.TAG;

public class WeatherServiceHandler {
    private static final long REQUEST_INTERVAL = 1000;

    private FusedLocationProviderClient providerClient;
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
                errorRetrivingDataToast(e);
            }
        }

        if (latitude != Double.MAX_VALUE && longitude != Double.MAX_VALUE) {
            callWeatherService(latitude, longitude, locality);
        }
    }

    private void errorRetrivingDataToast(Exception e) {
        Toast toast = Toast.makeText(activity,
                "No data available for query. Try again.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Timber.d(e, "%s: Error retrieving city data.", TAG);
    }

    public LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLocations().get(0);
                activity.setCitySearchText(getCityName(location.getLatitude(), location.getLongitude()));
            }
        };
    }

    private String getCityName(double lat, double lon) {
        String cityName = "";
        try {
            cityName = coder.getFromLocation(lat, lon, 1).get(0).getLocality();
        } catch (Exception e) { //Catch exception, and let user know to try again.
            errorRetrivingDataToast(e);
        }
        return cityName;
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(REQUEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        request.setFastestInterval(REQUEST_INTERVAL);
        return request;
    }

    public void getLocation() {
        providerClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Timber.d("DMT: requesting location.");
            providerClient.requestLocationUpdates(createLocationRequest(), getLocationCallback(), null);
        }
    }

        private void callWeatherService (Double lat, Double lon,final String locality){
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
                            activity.startResultsActivity(body.details, locality);
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
