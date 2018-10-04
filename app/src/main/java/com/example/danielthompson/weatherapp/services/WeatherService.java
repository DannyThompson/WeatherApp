package com.example.danielthompson.weatherapp.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit service interface for getting weather api response.
 * Went with https://darksky.net for the website to obtain info from.
 */
public interface WeatherService {

    @GET("{latlon}")
    Call<WeatherResponse> getWeather(@Path("latlon") String latlon);
}
