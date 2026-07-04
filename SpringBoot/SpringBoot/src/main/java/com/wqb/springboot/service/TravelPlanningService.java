package com.wqb.springboot.service;

import com.wqb.springboot.dto.AmapDtos;
import com.wqb.springboot.dto.WeatherResponseDtos;
import org.springframework.stereotype.Service;

@Service
public class TravelPlanningService {

    private final FoshanWeatherService foshanWeatherService;
    private final AmapGeocodingService geocodingService;
    private final AmapRouteService routeService;

    public TravelPlanningService(
            FoshanWeatherService foshanWeatherService,
            AmapGeocodingService geocodingService,
            AmapRouteService routeService
    ) {
        this.foshanWeatherService = foshanWeatherService;
        this.geocodingService = geocodingService;
        this.routeService = routeService;
    }

    public WeatherResponseDtos.TravelReport buildTravelReport(
            String districtCode,
            String originAddress,
            String destinationAddress,
            String departureTime,
            String modeCode
    ) {
        WeatherResponseDtos.TravelReport baseReport = foshanWeatherService.getTravelReport(
                districtCode,
                originAddress,
                destinationAddress,
                departureTime,
                modeCode
        );

        String originLocation = geocodingService.geocode(originAddress, "佛山");
        String destinationLocation = geocodingService.geocode(destinationAddress, "佛山");

        WeatherResponseDtos.TravelRouteSummary routeSummary = new WeatherResponseDtos.TravelRouteSummary(
                baseReport.route().modeCode(),
                baseReport.route().modeLabel(),
                originAddress,
                destinationAddress,
                originLocation == null ? "" : originLocation,
                destinationLocation == null ? "" : destinationLocation,
                baseReport.route().originDistrictName(),
                baseReport.route().destinationDistrictName(),
                baseReport.route().distanceText(),
                baseReport.route().durationText(),
                baseReport.route().mainInstruction()
        );

        if (originLocation != null && destinationLocation != null) {
            routeSummary = switch (baseReport.route().modeCode()) {
                case "walking" -> buildWalkingRoute(originAddress, destinationAddress, originLocation, destinationLocation, routeSummary);
                case "bicycling" -> buildBicyclingRoute(originAddress, destinationAddress, originLocation, destinationLocation, routeSummary);
                case "transit" -> buildTransitRoute(originAddress, destinationAddress, originLocation, destinationLocation, routeSummary);
                default -> buildDrivingRoute(originAddress, destinationAddress, originLocation, destinationLocation, routeSummary);
            };
        }

        return new WeatherResponseDtos.TravelReport(
                baseReport.departureTime(),
                baseReport.weatherSummary(),
                baseReport.travelAdvice(),
                baseReport.riskLevel(),
                routeSummary,
                baseReport.originWeather(),
                baseReport.destinationWeather(),
                baseReport.riskSegments(),
                baseReport.recommendedPlaces()
        );
    }

    private WeatherResponseDtos.TravelRouteSummary buildDrivingRoute(
            String originAddress,
            String destinationAddress,
            String originLocation,
            String destinationLocation,
            WeatherResponseDtos.TravelRouteSummary fallback
    ) {
        AmapDtos.DrivingRoute route = routeService.driving(originLocation, destinationLocation);
        if (route == null || route.paths == null || route.paths.isEmpty()) {
            return fallback;
        }
        AmapDtos.DrivingPath path = route.paths.getFirst();
        String instruction = path.steps != null && !path.steps.isEmpty() ? path.steps.getFirst().instruction : fallback.mainInstruction();
        return new WeatherResponseDtos.TravelRouteSummary(
                fallback.modeCode(),
                fallback.modeLabel(),
                originAddress,
                destinationAddress,
                originLocation,
                destinationLocation,
                fallback.originDistrictName(),
                fallback.destinationDistrictName(),
                formatDistance(path.distance, fallback.distanceText()),
                formatDuration(path.duration, fallback.durationText()),
                instruction
        );
    }

    private WeatherResponseDtos.TravelRouteSummary buildWalkingRoute(
            String originAddress,
            String destinationAddress,
            String originLocation,
            String destinationLocation,
            WeatherResponseDtos.TravelRouteSummary fallback
    ) {
        AmapDtos.WalkingRoute route = routeService.walking(originLocation, destinationLocation);
        if (route == null || route.paths == null || route.paths.isEmpty()) {
            return fallback;
        }
        AmapDtos.WalkingPath path = route.paths.getFirst();
        String instruction = path.steps != null && !path.steps.isEmpty() ? path.steps.getFirst().instruction : fallback.mainInstruction();
        return new WeatherResponseDtos.TravelRouteSummary(
                fallback.modeCode(),
                fallback.modeLabel(),
                originAddress,
                destinationAddress,
                originLocation,
                destinationLocation,
                fallback.originDistrictName(),
                fallback.destinationDistrictName(),
                formatDistance(path.distance, fallback.distanceText()),
                formatDuration(path.duration, fallback.durationText()),
                instruction
        );
    }

    private WeatherResponseDtos.TravelRouteSummary buildBicyclingRoute(
            String originAddress,
            String destinationAddress,
            String originLocation,
            String destinationLocation,
            WeatherResponseDtos.TravelRouteSummary fallback
    ) {
        AmapDtos.BicyclingRoute route = routeService.bicycling(originLocation, destinationLocation);
        if (route == null || route.paths == null || route.paths.isEmpty()) {
            return fallback;
        }
        AmapDtos.BicyclingPath path = route.paths.getFirst();
        String instruction = path.steps != null && !path.steps.isEmpty() ? path.steps.getFirst().instruction : fallback.mainInstruction();
        return new WeatherResponseDtos.TravelRouteSummary(
                fallback.modeCode(),
                fallback.modeLabel(),
                originAddress,
                destinationAddress,
                originLocation,
                destinationLocation,
                fallback.originDistrictName(),
                fallback.destinationDistrictName(),
                formatDistance(path.distance, fallback.distanceText()),
                formatDuration(path.duration, fallback.durationText()),
                instruction
        );
    }

    private WeatherResponseDtos.TravelRouteSummary buildTransitRoute(
            String originAddress,
            String destinationAddress,
            String originLocation,
            String destinationLocation,
            WeatherResponseDtos.TravelRouteSummary fallback
    ) {
        AmapDtos.TransitRoute route = routeService.transit(originLocation, destinationLocation, "0757", "0757");
        if (route == null) {
            return fallback;
        }
        String instruction = fallback.mainInstruction();
        if (route.transits != null && !route.transits.isEmpty()) {
            AmapDtos.Transit transit = route.transits.getFirst();
            if (transit.segments != null) {
                for (AmapDtos.TransitSegment segment : transit.segments) {
                    if (segment.bus != null && segment.bus.buslines != null && !segment.bus.buslines.isEmpty()) {
                        instruction = "优先乘坐 " + segment.bus.buslines.getFirst().name;
                        break;
                    }
                    if (segment.railway != null && segment.railway.name != null && !segment.railway.name.isBlank()) {
                        instruction = "优先换乘 " + segment.railway.name;
                        break;
                    }
                }
            }
        }
        return new WeatherResponseDtos.TravelRouteSummary(
                fallback.modeCode(),
                fallback.modeLabel(),
                originAddress,
                destinationAddress,
                originLocation,
                destinationLocation,
                fallback.originDistrictName(),
                fallback.destinationDistrictName(),
                formatDistance(route.distance, fallback.distanceText()),
                formatDuration(route.duration, fallback.durationText()),
                instruction
        );
    }

    private String formatDistance(String rawDistance, String fallback) {
        if (rawDistance == null || rawDistance.isBlank()) {
            return fallback;
        }
        try {
            long meters = Long.parseLong(rawDistance);
            if (meters >= 1000) {
                return String.format("%.1f km", meters / 1000.0);
            }
            return meters + " m";
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private String formatDuration(String rawDuration, String fallback) {
        if (rawDuration == null || rawDuration.isBlank()) {
            return fallback;
        }
        try {
            long seconds = Long.parseLong(rawDuration);
            long minutes = Math.max(1, Math.round(seconds / 60.0));
            return minutes + " 分钟";
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
