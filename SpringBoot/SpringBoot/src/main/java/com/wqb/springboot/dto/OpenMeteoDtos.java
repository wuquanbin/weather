package com.wqb.springboot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public final class OpenMeteoDtos {

    private OpenMeteoDtos() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastResponse {
        public String timezone;
        public Daily daily;
        public CurrentWeather current_weather;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Daily {
        public List<String> time;
        public List<Integer> weather_code;
        public List<Double> temperature_2m_max;
        public List<Double> temperature_2m_min;
        public List<Integer> precipitation_probability_max;
        public List<Integer> wind_direction_10m_dominant;
        public List<Double> wind_speed_10m_max;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeather {
        public double temperature;
        public int weathercode;
        public double windspeed;
        public int winddirection;
        public String time;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentResponse {
        public double latitude;
        public double longitude;
        public CurrentUnits current_units;
        public Current current;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentUnits {
        public String temperature_2m;
        public String apparent_temperature;
        public String relative_humidity_2m;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        public String time;
        public double temperature_2m;
        public double apparent_temperature;
        public int relative_humidity_2m;
    }
}