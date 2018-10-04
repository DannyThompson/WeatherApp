package com.example.danielthompson.weatherapp.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class containing the data from the call to the weather service.
 */
public class WeatherResponse {

    @SerializedName("currently")
    @Expose
    public WeatherDetails details = new WeatherDetails();

    public class WeatherDetails {

        public Double temperature;
        public Double pressure;
        public Double humidity;
        public int time;

        @SerializedName("precipProbability")
        public Double chanceOfRain;
        @SerializedName("summary")
        public String currentCondition;
    }

}
