package com.wqb.springboot.controller;

import com.wqb.springboot.dto.ApiResponse;
import com.wqb.springboot.dto.WeatherResponseDtos;
import com.wqb.springboot.service.FoshanWeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final FoshanWeatherService foshanWeatherService;

    public DashboardController(FoshanWeatherService foshanWeatherService) {
        this.foshanWeatherService = foshanWeatherService;
    }

    @GetMapping("/dashboard/overview")
    public ApiResponse<WeatherResponseDtos.DashboardOverview> overview(
            @RequestParam(required = false) String districtCode
    ) {
        return ApiResponse.ok(foshanWeatherService.getDashboardOverview(districtCode));
    }

    @GetMapping("/meta/districts")
    public ApiResponse<List<WeatherResponseDtos.DistrictOption>> districts() {
        return ApiResponse.ok(foshanWeatherService.listDistricts());
    }

    @GetMapping("/weather/current")
    public ApiResponse<WeatherResponseDtos.CurrentWeather> current(
            @RequestParam(required = false) String districtCode
    ) {
        return ApiResponse.ok(foshanWeatherService.getCurrentWeather(districtCode));
    }

    @GetMapping("/weather/forecast")
    public ApiResponse<List<WeatherResponseDtos.ForecastDay>> forecast(
            @RequestParam(required = false) String districtCode
    ) {
        return ApiResponse.ok(foshanWeatherService.getForecast(districtCode));
    }

    @GetMapping("/travel/suggestions")
    public ApiResponse<List<WeatherResponseDtos.TravelSuggestionItem>> travelSuggestions(
            @RequestParam(required = false) String districtCode
    ) {
        return ApiResponse.ok(foshanWeatherService.getTravelSuggestions(districtCode));
    }

    @GetMapping("/travel/places")
    public ApiResponse<List<WeatherResponseDtos.TravelPlaceItem>> recommendedPlaces(
            @RequestParam(required = false) String districtCode
    ) {
        return ApiResponse.ok(foshanWeatherService.getRecommendedPlaces(districtCode));
    }

    @GetMapping("/travel/risk-segments")
    public ApiResponse<List<WeatherResponseDtos.RiskSegmentItem>> riskSegments(
            @RequestParam(required = false) String districtCode
    ) {
        return ApiResponse.ok(foshanWeatherService.getRiskSegments(districtCode));
    }

    @GetMapping("/warnings/active")
    public ApiResponse<List<WeatherResponseDtos.WarningNoticeItem>> activeWarnings() {
        return ApiResponse.ok(foshanWeatherService.listActiveWarnings());
    }

    @GetMapping("/knowledge/documents")
    public ApiResponse<List<WeatherResponseDtos.KnowledgeDocumentItem>> knowledgeDocuments() {
        return ApiResponse.ok(foshanWeatherService.listKnowledgeDocuments());
    }

    @GetMapping("/system/modules")
    public ApiResponse<List<WeatherResponseDtos.ModuleStatusItem>> moduleStatus() {
        return ApiResponse.ok(foshanWeatherService.listModuleStatus());
    }
}
