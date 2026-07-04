package com.wqb.springboot.service;

import com.wqb.springboot.dto.WeatherResponseDtos;
import com.wqb.springboot.dto.WeatherResponseDtos.*;
import com.wqb.springboot.entity.*;
import com.wqb.springboot.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoshanWeatherService {

    private static final String DEFAULT_PUBLISHER = "\u4f5b\u5c71\u5e02\u6c14\u8c61\u53f0";

    private final DistrictRepository districtRepository;
    private final WeatherObservationRepository weatherObservationRepository;
    private final WeatherForecastRepository weatherForecastRepository;
    private final TravelSuggestionRepository travelSuggestionRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final RiskSegmentRepository riskSegmentRepository;
    private final WarningNoticeRepository warningNoticeRepository;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final LifeIndexRepository lifeIndexRepository;

    public FoshanWeatherService(
            DistrictRepository districtRepository,
            WeatherObservationRepository weatherObservationRepository,
            WeatherForecastRepository weatherForecastRepository,
            TravelSuggestionRepository travelSuggestionRepository,
            TravelPlaceRepository travelPlaceRepository,
            RiskSegmentRepository riskSegmentRepository,
            WarningNoticeRepository warningNoticeRepository,
            KnowledgeDocumentRepository knowledgeDocumentRepository,
            LifeIndexRepository lifeIndexRepository
    ) {
        this.districtRepository = districtRepository;
        this.weatherObservationRepository = weatherObservationRepository;
        this.weatherForecastRepository = weatherForecastRepository;
        this.travelSuggestionRepository = travelSuggestionRepository;
        this.travelPlaceRepository = travelPlaceRepository;
        this.riskSegmentRepository = riskSegmentRepository;
        this.warningNoticeRepository = warningNoticeRepository;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
        this.lifeIndexRepository = lifeIndexRepository;
    }

    // ========== Districts ==========

    public List<DistrictOption> listDistricts() {
        return districtRepository.findAllByOrderByIdAsc().stream()
                .map(d -> new DistrictOption(d.getId(), d.getCode(), d.getName(),
                        d.getServiceArea(), d.getHighlights(), d.getTransportFocus()))
                .toList();
    }

    private District resolveDistrict(String districtCode) {
        if (districtCode != null && !districtCode.isBlank()) {
            return districtRepository.findByCode(districtCode)
                    .orElseThrow(() -> new RuntimeException("\u533a\u57df\u4fe1\u606f\u4e0d\u5b58\u5728: " + districtCode));
        }
        return districtRepository.findAllByOrderByIdAsc().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("\u7cfb\u7edf\u5c1a\u672a\u521d\u59cb\u5316\u533a\u57df\u6570\u636e"));
    }

    // ========== Current Weather ==========

    public CurrentWeather getCurrentWeather(String districtCode) {
        District district = resolveDistrict(districtCode);
        WeatherObservation obs = weatherObservationRepository
                .findFirstByDistrictOrderByObservationTimeDesc(district)
                .orElse(null);

        if (obs == null) {
            return new CurrentWeather(
                    district.getCode(), district.getName(),
                    "\u6682\u65e0", BigDecimal.ZERO, BigDecimal.ZERO, 0,
                    "-", "-", "\u6682\u65e0", 0,
                    "\u6682\u65e0", "\u6682\u65e0", "\u6682\u65e0",
                    null, null, null
            );
        }

        return new CurrentWeather(
                district.getCode(),
                district.getName(),
                obs.getWeatherType(),
                obs.getTemperature(),
                obs.getApparentTemperature(),
                obs.getHumidity(),
                obs.getWindDirection(),
                obs.getWindScale(),
                obs.getAirQuality(),
                obs.getPrecipitationProbability(),
                obs.getComfortLevel(),
                obs.getUvLevel(),
                obs.getTravelIndex(),
                obs.getPressure(),
                obs.getVisibility(),
                obs.getObservationTime()
        );
    }

    // ========== Forecast ==========

    public List<ForecastDay> getForecast(String districtCode) {
        District district = resolveDistrict(districtCode);
        return weatherForecastRepository.findByDistrictOrderByForecastDateAsc(district).stream()
                .map(f -> new ForecastDay(
                        f.getForecastDate(),
                        f.getWeekLabel(),
                        f.getWeatherType(),
                        f.getLowTemperature(),
                        f.getHighTemperature(),
                        f.getPrecipitationProbability(),
                        f.getWindDirection(),
                        f.getWindScale(),
                        f.getTravelAdvice()
                ))
                .toList();
    }

    // ========== Travel Suggestions ==========

    public List<TravelSuggestionItem> getTravelSuggestions(String districtCode) {
        District district = resolveDistrict(districtCode);
        return travelSuggestionRepository.findByDistrictOrderByPriorityDescIdAsc(district).stream()
                .map(s -> new TravelSuggestionItem(
                        s.getScenarioCode(),
                        s.getTitle(),
                        s.getSummary(),
                        s.getRecommendation(),
                        s.getPriorityTag(),
                        s.getIconKey(),
                        s.getPriority()
                ))
                .toList();
    }

    // ========== Recommended Places ==========

    public List<TravelPlaceItem> getRecommendedPlaces(String districtCode) {
        District district = resolveDistrict(districtCode);
        return travelPlaceRepository.findByDistrictOrderByRecommendLevelDescIdAsc(district).stream()
                .map(p -> new TravelPlaceItem(
                        p.getId(),
                        district.getCode(),
                        district.getName(),
                        p.getName(),
                        p.getCategory(),
                        p.getAddress(),
                        p.getLocation(),
                        p.getIndoor(),
                        p.getWeatherTags(),
                        p.getSceneTags(),
                        p.getRecommendLevel(),
                        p.getHighlight(),
                        buildMatchReason(p)
                ))
                .toList();
    }

    private String buildMatchReason(TravelPlace p) {
        StringBuilder sb = new StringBuilder();
        if (p.getIndoor()) {
            sb.append("\u5ba4\u5185\u573a\u6240\uff0c\u96e8\u5929\u4e5f\u9002\u5408\uff1b");
        }
        if (p.getWeatherTags() != null && !p.getWeatherTags().isBlank()) {
            sb.append("\u9002\u5408").append(p.getWeatherTags()).append("\u5929\u6c14\uff1b");
        }
        if (p.getRecommendLevel() >= 4) {
            sb.append("\u9ad8\u63a8\u8350\u666f\u70b9");
        }
        return sb.toString();
    }

    // ========== Risk Segments ==========

    public List<RiskSegmentItem> getRiskSegments(String districtCode) {
        District district = resolveDistrict(districtCode);
        return riskSegmentRepository.findByDistrictOrderByPriorityDescIdAsc(district).stream()
                .map(r -> new RiskSegmentItem(
                        r.getId(),
                        district.getCode(),
                        district.getName(),
                        r.getName(),
                        r.getLocation(),
                        r.getRiskType(),
                        r.getTriggerWeatherTags(),
                        r.getDescription(),
                        r.getAdvice(),
                        r.getPriority()
                ))
                .toList();
    }

    // ========== Active Warnings ==========

    public List<WarningNoticeItem> listActiveWarnings() {
        return warningNoticeRepository.findByStatus("active").stream()
                .map(this::toWarning)
                .toList();
    }

    private WarningNoticeItem toWarning(WarningNotice w) {
        return new WarningNoticeItem(
                w.getWarningType(),
                w.getSeverity(),
                w.getTitle(),
                w.getContent(),
                w.getIssuedAt(),
                w.getExpiresAt(),
                w.getStatus(),
                w.getImpactArea(),
                w.getDefenseGuidance()
        );
    }

    // ========== Knowledge Documents ==========

    public List<KnowledgeDocumentItem> listKnowledgeDocuments() {
        return knowledgeDocumentRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(k -> new KnowledgeDocumentItem(
                        k.getId(),
                        k.getTitle(),
                        k.getCategory(),
                        k.getSummary(),
                        k.getContent(),
                        k.getTags(),
                        k.getSourceType(),
                        k.getRagReady(),
                        k.getLastIndexedAt()
                ))
                .toList();
    }

    // ========== Module Status ==========

    public List<ModuleStatusItem> listModuleStatus() {
        List<ModuleStatusItem> modules = new ArrayList<>();
        modules.add(new ModuleStatusItem("weather-current", "\u5b9e\u65f6\u6c14\u8c61\u6570\u636e", "RUNNING", 100,
                "\u5df2\u63a5\u5165\u4f5b\u5c71\u4e94\u533a\u5b9e\u65f6\u5929\u6c14\u89c2\u6d4b"));
        modules.add(new ModuleStatusItem("weather-forecast", "\u591a\u65e5\u9884\u62a5", "RUNNING", 100,
                "\u652f\u63013\u65e5/\u5c0f\u65f6\u7ea7\u9884\u62a5\u6570\u636e"));
        modules.add(new ModuleStatusItem("warning-center", "\u9884\u8b66\u4e2d\u5fc3", "RUNNING", 100,
                "\u66b4\u96e8/\u53f0\u98ce/\u9ad8\u6e29\u7b49\u9884\u8b66\u63a5\u5165"));
        modules.add(new ModuleStatusItem("travel-engine", "\u667a\u80fd\u51fa\u884c\u5f15\u64ce", "RUNNING", 90,
                "\u573a\u666f\u5316\u51fa\u884c\u5efa\u8bae+\u98ce\u9669\u63d0\u793a"));
        modules.add(new ModuleStatusItem("amap-route", "\u9ad8\u5fb7\u8def\u7ebf\u89c4\u5212", "RUNNING", 100,
                "\u9a7e\u8f66/\u6b65\u884c/\u9a91\u884c/\u516c\u4ea4\u56db\u79cd\u6a21\u5f0f"));
        modules.add(new ModuleStatusItem("amap-poi", "POI\u641c\u7d22", "RUNNING", 100,
                "\u672c\u5730\u666f\u70b9/\u5546\u5708/\u4ea4\u901a\u67a2\u7ebd\u68c0\u7d22"));
        modules.add(new ModuleStatusItem("knowledge-rag", "RAG\u77e5\u8bc6\u5e93", "PENDING", 30,
                "\u6c14\u8c61\u77e5\u8bc6\u6587\u6863\u5df2\u5f55\u5165\uff0c\u5f85\u63a5\u5165\u5411\u91cf\u68c0\u7d22"));
        modules.add(new ModuleStatusItem("wechat-push", "\u5fae\u4fe1\u63a8\u9001", "PENDING", 20,
                "\u8ba2\u9605\u6d88\u606f\u63a8\u9001\u670d\u52a1\u5f85\u5f00\u901a"));
        return modules;
    }

    // ========== Dashboard Overview ==========

    public DashboardOverview getDashboardOverview(String districtCode) {
        District district = resolveDistrict(districtCode);
        DistrictOption districtOption = new DistrictOption(
                district.getId(), district.getCode(), district.getName(),
                district.getServiceArea(), district.getHighlights(), district.getTransportFocus()
        );

        return new DashboardOverview(
                districtOption,
                getCurrentWeather(districtCode),
                getForecast(districtCode),
                getTravelSuggestions(districtCode),
                getRecommendedPlaces(districtCode),
                getRiskSegments(districtCode),
                listActiveWarnings(),
                listKnowledgeDocuments(),
                listModuleStatus()
        );
    }

    // ========== Travel Report ==========

    public TravelReport getTravelReport(
            String districtCode,
            String originAddress,
            String destinationAddress,
            String departureTime,
            String modeCode
    ) {
        District district = resolveDistrict(districtCode);
        CurrentWeather current = getCurrentWeather(districtCode);

        String modeLabel = switch (modeCode) {
            case "walking" -> "\u6b65\u884c";
            case "bicycling" -> "\u9a91\u884c";
            case "transit" -> "\u516c\u4ea4/\u5730\u94c1";
            default -> "\u9a7e\u8f66";
        };

        String weatherSummary = buildWeatherSummary(current);
        String travelAdvice = buildTravelAdvice(current);
        String riskLevel = assessRiskLevel(current, district);

        TravelRouteSummary route = new TravelRouteSummary(
                modeCode, modeLabel,
                originAddress, destinationAddress,
                "", "",
                district.getName(), district.getName(),
                "\u8ba1\u7b97\u4e2d..", "\u8ba1\u7b97\u4e2d..",
                "\u8bf7\u7ed3\u5408\u5b9e\u9645\u8def\u51b5\u89c4\u5212\u8def\u7ebf"
        );

        TravelWeatherSnapshot originWeather = buildSnapshot(district, current);
        TravelWeatherSnapshot destWeather = buildSnapshot(district, current);

        List<RiskSegmentItem> riskSegments = getRiskSegments(districtCode);
        List<TravelPlaceItem> places = getRecommendedPlaces(districtCode);

        return new TravelReport(
                departureTime,
                weatherSummary,
                travelAdvice,
                riskLevel,
                route,
                originWeather,
                destWeather,
                riskSegments,
                places
        );
    }

    private String buildWeatherSummary(CurrentWeather w) {
        if (w == null || w.weatherType() == null) {
            return "\u6682\u65e0\u5929\u6c14\u6570\u636e";
        }
        return String.format("\u5f53\u524d%s\uff0c\u6e29\u5ea6%s\u2103\uff0c\u4f53\u611f%s\u2103\uff0c%s%s\uff0c\u964d\u6c34\u6982\u7387%d%%",
                w.weatherType(), w.temperature(), w.apparentTemperature(),
                w.windDirection(), w.windScale(), w.precipitationProbability());
    }

    private String buildTravelAdvice(CurrentWeather w) {
        if (w == null || w.weatherType() == null) {
            return "\u5efa\u8bae\u5173\u6ce8\u6700\u65b0\u5929\u6c14\u9884\u62a5";
        }
        String type = w.weatherType();
        if (type.contains("\u66b4\u96e8") || type.contains("\u5927\u96e8")) {
            return "\u964d\u6c34\u8f83\u5f3a\uff0c\u5efa\u8bae\u643a\u5e26\u96e8\u5177\uff0c\u4f18\u5148\u9009\u62e9\u5730\u94c1/\u516c\u4ea4\u51fa\u884c\uff0c\u6ce8\u610f\u4f5b\u5c71\u6613\u79ef\u6c34\u8def\u6bb5";
        }
        if (type.contains("\u96e8")) {
            return "\u6709\u964d\u6c34\uff0c\u5efa\u8bae\u643a\u5e26\u96e8\u5177\uff0c\u51fa\u884c\u6ce8\u610f\u8def\u9762\u6e7f\u6ed1";
        }
        if (type.contains("\u53f0\u98ce")) {
            return "\u53f0\u98ce\u5f71\u54cd\uff0c\u5f3a\u70c8\u5efa\u8bae\u51cf\u5c11\u5916\u51fa\uff0c\u8fdc\u79bb\u5e7f\u544a\u724c/\u5927\u6811\uff0c\u6ce8\u610f\u5b89\u5168";
        }
        if (type.contains("\u9ad8\u6e29") || "\u7099\u70ed".equals(type)) {
            return "\u9ad8\u6e29\u5929\u6c14\uff0c\u6ce8\u610f\u9632\u6651\u8865\u6c34\uff0c\u5efa\u8bae\u9009\u62e9\u9634\u51c9\u8def\u7ebf\u6216\u5ba4\u5185\u4e2d\u8f6c\u70b9";
        }
        if (w.precipitationProbability() != null && w.precipitationProbability() < 20) {
            return "\u5929\u6c14\u826f\u597d\uff0c\u9002\u5408\u6b65\u884c/\u9a91\u884c\u7b49\u7eff\u8272\u51fa\u884c\u65b9\u5f0f";
        }
        return "\u8bf7\u6839\u636e\u5b9e\u9645\u5929\u6c14\u60c5\u51b5\u5408\u7406\u5b89\u6392\u51fa\u884c";
    }

    private String assessRiskLevel(CurrentWeather w, District district) {
        boolean hasActiveWarning = !warningNoticeRepository.findByStatus("active").isEmpty();
        if (hasActiveWarning) {
            return "HIGH";
        }
        if (w != null && w.precipitationProbability() != null && w.precipitationProbability() > 60) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private TravelWeatherSnapshot buildSnapshot(District district, CurrentWeather w) {
        return new TravelWeatherSnapshot(
                district.getCode(),
                district.getName(),
                w != null ? w.weatherType() : "-",
                w != null ? w.temperature() : BigDecimal.ZERO,
                w != null ? w.precipitationProbability() : 0,
                w != null ? w.windDirection() : "-",
                w != null ? w.windScale() : "-",
                w != null ? w.travelIndex() : "-"
        );
    }
}
