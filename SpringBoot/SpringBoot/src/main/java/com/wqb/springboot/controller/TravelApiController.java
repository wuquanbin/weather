package com.wqb.springboot.controller;

import com.wqb.springboot.dto.AmapDtos;
import com.wqb.springboot.dto.ApiResponse;
import com.wqb.springboot.dto.WeatherResponseDtos;
import com.wqb.springboot.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/travel")
public class TravelApiController {

    private final AmapRouteService routeService;
    private final AmapPoiService poiService;
    private final AmapGeocodingService geocodingService;
    private final AmapWeatherService weatherService;
    private final TravelPlanningService travelPlanningService;

    public TravelApiController(
            AmapRouteService routeService,
            AmapPoiService poiService,
            AmapGeocodingService geocodingService,
            AmapWeatherService weatherService,
            TravelPlanningService travelPlanningService
    ) {
        this.routeService = routeService;
        this.poiService = poiService;
        this.geocodingService = geocodingService;
        this.weatherService = weatherService;
        this.travelPlanningService = travelPlanningService;
    }

    // ========== Geocoding ==========

    @GetMapping("/geocode")
    public ApiResponse<String> geocode(@RequestParam String address, @RequestParam(required = false) String city) {
        String location = geocodingService.geocode(address, city);
        if (location == null) {
            return ApiResponse.fail("Geocoding failed", null);
        }
        return ApiResponse.ok(location);
    }

    @GetMapping("/reverse-geocode")
    public ApiResponse<AmapDtos.Regeocode> reverseGeocode(@RequestParam String location) {
        AmapDtos.Regeocode result = geocodingService.reverseGeocode(location);
        if (result == null) {
            return ApiResponse.fail("Reverse geocoding failed", null);
        }
        return ApiResponse.ok(result);
    }

    // ========== Route Planning ==========

    @GetMapping("/route/driving")
    public ApiResponse<AmapDtos.DrivingRoute> drivingRoute(
            @RequestParam String origin, @RequestParam String destination) {
        AmapDtos.DrivingRoute route = routeService.driving(origin, destination);
        if (route == null) {
            return ApiResponse.fail("Driving route planning failed", null);
        }
        return ApiResponse.ok(route);
    }

    @GetMapping("/route/walking")
    public ApiResponse<AmapDtos.WalkingRoute> walkingRoute(
            @RequestParam String origin, @RequestParam String destination) {
        AmapDtos.WalkingRoute route = routeService.walking(origin, destination);
        if (route == null) {
            return ApiResponse.fail("Walking route planning failed", null);
        }
        return ApiResponse.ok(route);
    }

    @GetMapping("/route/bicycling")
    public ApiResponse<AmapDtos.BicyclingRoute> bicyclingRoute(
            @RequestParam String origin, @RequestParam String destination) {
        AmapDtos.BicyclingRoute route = routeService.bicycling(origin, destination);
        if (route == null) {
            return ApiResponse.fail("Bicycling route planning failed", null);
        }
        return ApiResponse.ok(route);
    }

    @GetMapping("/route/transit")
    public ApiResponse<AmapDtos.TransitRoute> transitRoute(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(defaultValue = "0757") String city,
            @RequestParam(defaultValue = "0757") String cityd) {
        AmapDtos.TransitRoute route = routeService.transit(origin, destination, city, cityd);
        if (route == null) {
            return ApiResponse.fail("Transit route planning failed", null);
        }
        return ApiResponse.ok(route);
    }

    // ========== POI Search ==========

    @GetMapping("/poi/search")
    public ApiResponse<List<AmapDtos.Poi>> poiSearch(
            @RequestParam String keywords,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "\u4f5b\u5c71") String city,
            @RequestParam(required = false) String types,
            @RequestParam(required = false) String radius) {
        return ApiResponse.ok(poiService.search(keywords, location, city, types, radius));
    }

    @GetMapping("/poi/around")
    public ApiResponse<List<AmapDtos.Poi>> poiAround(
            @RequestParam String keywords,
            @RequestParam String location,
            @RequestParam(required = false) String types,
            @RequestParam(required = false, defaultValue = "3000") String radius) {
        return ApiResponse.ok(poiService.aroundSearch(keywords, location, types, radius));
    }

    // ========== Weather (direct Amap call, not from DB) ==========

    @GetMapping("/weather/amap")
    public ApiResponse<AmapDtos.LiveWeatherResponse> amapWeather(
            @RequestParam(required = false) String districtCode) {
        AmapDtos.LiveWeatherResponse response;
        if (districtCode != null && !districtCode.isBlank()) {
            response = weatherService.fetchDistrictLiveWeather(districtCode);
        } else {
            response = weatherService.fetchDistrictLiveWeather("chancheng");
        }
        if (response == null) {
            return ApiResponse.fail("Amap weather API failed", null);
        }
        return ApiResponse.ok(response);
    }

    // ========== Module Status ==========

    @GetMapping("/modules")
    public ApiResponse<Map<String, String>> modules() {
        return ApiResponse.ok(Map.of(
                "amap-weather", "CONNECTED",
                "amap-geocoding", "CONNECTED",
                "amap-route-driving", "CONNECTED",
                "amap-route-walking", "CONNECTED",
                "amap-route-bicycling", "CONNECTED",
                "amap-route-transit", "CONNECTED",
                "amap-poi", "CONNECTED"
        ));
    }

    @GetMapping("/report")
    public ApiResponse<WeatherResponseDtos.TravelReport> travelReport(
            @RequestParam String districtCode,
            @RequestParam String originAddress,
            @RequestParam String destinationAddress,
            @RequestParam String departureTime,
            @RequestParam(defaultValue = "driving") String modeCode
    ) {
        return ApiResponse.ok(travelPlanningService.buildTravelReport(
                districtCode,
                originAddress,
                destinationAddress,
                departureTime,
                modeCode
        ));
    }
}
