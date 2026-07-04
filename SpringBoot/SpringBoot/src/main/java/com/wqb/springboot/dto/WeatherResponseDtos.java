package com.wqb.springboot.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class WeatherResponseDtos {

    private WeatherResponseDtos() {
    }

    public record DistrictOption(
            Long id,
            String code,
            String name,
            String serviceArea,
            String highlights,
            String transportFocus
    ) {
    }

    public record CurrentWeather(
            String districtCode,
            String districtName,
            String weatherType,
            BigDecimal temperature,
            BigDecimal apparentTemperature,
            Integer humidity,
            String windDirection,
            String windScale,
            String airQuality,
            Integer precipitationProbability,
            String comfortLevel,
            String uvLevel,
            String travelIndex,
            BigDecimal pressure,
            BigDecimal visibility,
            LocalDateTime observationTime
    ) {
    }

    public record ForecastDay(
            LocalDate forecastDate,
            String weekLabel,
            String weatherType,
            BigDecimal lowTemperature,
            BigDecimal highTemperature,
            Integer precipitationProbability,
            String windDirection,
            String windScale,
            String travelAdvice
    ) {
    }

    public record TravelSuggestionItem(
            String scenarioCode,
            String title,
            String summary,
            String recommendation,
            String priorityTag,
            String iconKey,
            Integer priority
    ) {
    }

    public record TravelPlaceItem(
            Long id,
            String districtCode,
            String districtName,
            String name,
            String category,
            String address,
            String location,
            Boolean indoor,
            String weatherTags,
            String sceneTags,
            Integer recommendLevel,
            String highlight,
            String matchReason
    ) {
    }

    public record RiskSegmentItem(
            Long id,
            String districtCode,
            String districtName,
            String name,
            String location,
            String riskType,
            String triggerWeatherTags,
            String description,
            String advice,
            Integer priority
    ) {
    }

    public record WarningNoticeItem(
            String warningType,
            String severity,
            String title,
            String content,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt,
            String status,
            String impactArea,
            String defenseGuidance
    ) {
    }

    public record KnowledgeDocumentItem(
            Long id,
            String title,
            String category,
            String summary,
            String content,
            String tags,
            String sourceType,
            Boolean ragReady,
            LocalDateTime lastIndexedAt
    ) {
    }

    public record ModuleStatusItem(
            String code,
            String name,
            String status,
            Integer progress,
            String description
    ) {
    }

    public record TravelWeatherSnapshot(
            String districtCode,
            String districtName,
            String weatherType,
            BigDecimal temperature,
            Integer precipitationProbability,
            String windDirection,
            String windScale,
            String travelIndex
    ) {
    }

    public record TravelRouteSummary(
            String modeCode,
            String modeLabel,
            String originAddress,
            String destinationAddress,
            String originLocation,
            String destinationLocation,
            String originDistrictName,
            String destinationDistrictName,
            String distanceText,
            String durationText,
            String mainInstruction
    ) {
    }

    public record TravelReport(
            String departureTime,
            String weatherSummary,
            String travelAdvice,
            String riskLevel,
            TravelRouteSummary route,
            TravelWeatherSnapshot originWeather,
            TravelWeatherSnapshot destinationWeather,
            List<RiskSegmentItem> riskSegments,
            List<TravelPlaceItem> recommendedPlaces
    ) {
    }

    public record DashboardOverview(
            DistrictOption district,
            CurrentWeather currentWeather,
            List<ForecastDay> forecast,
            List<TravelSuggestionItem> travelSuggestions,
            List<TravelPlaceItem> recommendedPlaces,
            List<RiskSegmentItem> riskSegments,
            List<WarningNoticeItem> activeWarnings,
            List<KnowledgeDocumentItem> knowledgeDocuments,
            List<ModuleStatusItem> moduleStatus
    ) {
    }
}
