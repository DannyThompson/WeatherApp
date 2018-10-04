package com.example.danielthompson.weatherapp.provider;

import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.danielthompson.weatherapp.activities.SearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Class that is used to return the user's location.
 * Created for further abstraction and separation-of-concerns.
 */
public class LocationProvider {
    private static final long REQUEST_INTERVAL = 1000 * 10;

    private SearchActivity activity;
    private Geocoder coder;

    public LocationProvider(SearchActivity activity, Geocoder coder) {
        this.activity = activity;
        this.coder = coder;
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(REQUEST_INTERVAL);
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        request.setFastestInterval(REQUEST_INTERVAL);
        return request;
    }

    private LocationCallback getLocationCallback() {
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

    /**
     * Verify that location permissions were granted, and then obtain the location of the user.
     */
    public void getLocation() {
        FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            providerClient.requestLocationUpdates(createLocationRequest(), getLocationCallback(), null);
        }
    }

    /**
     * Convert the latitude and longitude from the providerClient to a human-readable city name.
     *
     * @param lat - Latitude.
     * @param lon - Longitude
     * @return city name.
     */
    private String getCityName(double lat, double lon) {
        String cityName = "";
        try {
            cityName = coder.getFromLocation(lat, lon, 1).get(0).getLocality();
        } catch (Exception e) {
            activity.errorRetrivingDataToast(e);
        }
        return cityName;
    }
}
