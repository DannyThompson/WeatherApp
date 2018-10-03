package com.example.danielthompson.weatherapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("currently")
    @Expose
    public WeatherDetails details = new WeatherDetails();

    public class WeatherDetails {

        public Double temperature;
        public Double pressure;
        public Double humidity;
        public int time;
        public int sunsetTime;

        @SerializedName("precipProbability")
        public Double chanceOfRain;
        @SerializedName("summary")
        public String currentCondition;
    }

}
