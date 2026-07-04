package com.wqb.springboot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class AmapDtos {

    private AmapDtos() {}

    // ========== Weather ==========

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherResponse {
        public String status;
        public String info;
        public List<Forecast> forecasts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        public String city;
        public String adcode;
        public String province;
        public String reporttime;
        public List<Cast> casts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cast {
        public String date;
        public String week;
        public String dayweather;
        public String nightweather;
        public String daytemp;
        public String nighttemp;
        public String daywind;
        public String nightwind;
        public String daypower;
        public String nightpower;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LiveWeatherResponse {
        public String status;
        public String info;
        public List<Live> lives;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Live {
        public String province;
        public String city;
        public String adcode;
        public String weather;
        public String temperature;
        public String winddirection;
        public String windpower;
        public String humidity;
        public String reporttime;
        public String temperature_float;
        public String humidity_float;
    }

    // ========== Geocoding ==========

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeocodeResponse {
        public String status;
        public String info;
        public List<Geocode> geocodes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geocode {
        @JsonProperty("formatted_address")
        public String formattedAddress;
        public String province;
        public String city;
        public String adcode;
        public String location;
        public String level;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RegeocodeResponse {
        public String status;
        public String info;
        public Regeocode regeocode;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Regeocode {
        @JsonProperty("formatted_address")
        public String formattedAddress;
        public AddressComponent addressComponent;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AddressComponent {
        public String province;
        public String city;
        public String district;
        public String adcode;
    }

    // ========== Route Planning ==========

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DrivingResponse {
        public String status;
        public String info;
        public DrivingRoute route;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DrivingRoute {
        public String origin;
        public String destination;
        public List<DrivingPath> paths;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DrivingPath {
        public String distance;
        public String duration;
        @JsonProperty("traffic_lights")
        public String trafficLights;
        public List<DrivingStep> steps;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DrivingStep {
        public String instruction;
        public String road;
        public String distance;
        public String duration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingResponse {
        public String status;
        public String info;
        public WalkingRoute route;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingRoute {
        public String origin;
        public String destination;
        public List<WalkingPath> paths;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingPath {
        public String distance;
        public String duration;
        public List<WalkingStep> steps;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WalkingStep {
        public String instruction;
        public String road;
        public String distance;
        public String duration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BicyclingResponse {
        public String status;
        public String info;
        public BicyclingRoute route;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BicyclingRoute {
        public String origin;
        public String destination;
        public List<BicyclingPath> paths;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BicyclingPath {
        public String distance;
        public String duration;
        public List<BicyclingStep> steps;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BicyclingStep {
        public String instruction;
        public String road;
        public String distance;
        public String duration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitResponse {
        public String status;
        public String info;
        public List<TransitRoute> route;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitRoute {
        public String distance;
        public String duration;
        @JsonProperty("transits")
        public List<Transit> transits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Transit {
        public String duration;
        public String distance;
        public String cost;
        public List<TransitSegment> segments;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitSegment {
        public TransitWalking walking;
        public TransitBus bus;
        public TransitRailway railway;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitWalking {
        public String origin;
        public String destination;
        public String distance;
        public String duration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitBus {
        public List<TransitBusLine> buslines;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitBusLine {
        public String name;
        public String departure_stop;
        public String arrival_stop;
        public String distance;
        public String duration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitRailway {
        public String name;
        public String departure_stop;
        public String arrival_stop;
        public String distance;
        public String duration;
    }

    // ========== POI Search ==========

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PoiSearchResponse {
        public String status;
        public String info;
        public String count;
        public List<Poi> pois;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Poi {
        public String id;
        public String name;
        public String type;
        @JsonProperty("typecode")
        public String typeCode;
        public String address;
        public String location;
        @JsonProperty("tel")
        public String tel;
        @JsonProperty("cityname")
        public String cityName;
        @JsonProperty("adname")
        public String districtName;
        public String distance;
    }

    // ========== Distance ==========

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DistanceResponse {
        public String status;
        public String info;
        public List<DistanceResult> results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DistanceResult {
        public String distance;
        public String duration;
    }
}
