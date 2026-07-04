package com.wqb.springboot.scheduler;

import com.wqb.springboot.dto.AmapDtos;
import com.wqb.springboot.dto.OpenMeteoDtos;
import com.wqb.springboot.entity.District;
import com.wqb.springboot.entity.WeatherForecast;
import com.wqb.springboot.entity.WeatherObservation;
import com.wqb.springboot.repository.DistrictRepository;
import com.wqb.springboot.repository.WeatherForecastRepository;
import com.wqb.springboot.repository.WeatherObservationRepository;
import com.wqb.springboot.service.AmapWeatherService;
import com.wqb.springboot.service.OpenMeteoWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Component
public class WeatherSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherSyncScheduler.class);
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");
    private static final Map<DayOfWeek, String> WEEK_MAP = Map.of(
            DayOfWeek.MONDAY, "星期一", DayOfWeek.TUESDAY, "星期二",
            DayOfWeek.WEDNESDAY, "星期三", DayOfWeek.THURSDAY, "星期四",
            DayOfWeek.FRIDAY, "星期五", DayOfWeek.SATURDAY, "星期六",
            DayOfWeek.SUNDAY, "星期日"
    );

    private final AmapWeatherService amapWeatherService;
    private final OpenMeteoWeatherService openMeteoWeatherService;
    private final DistrictRepository districtRepository;
    private final WeatherObservationRepository observationRepository;
    private final WeatherForecastRepository forecastRepository;

    public WeatherSyncScheduler(
            AmapWeatherService amapWeatherService,
            OpenMeteoWeatherService openMeteoWeatherService,
            DistrictRepository districtRepository,
            WeatherObservationRepository observationRepository,
            WeatherForecastRepository forecastRepository
    ) {
        this.amapWeatherService = amapWeatherService;
        this.openMeteoWeatherService = openMeteoWeatherService;
        this.districtRepository = districtRepository;
        this.observationRepository = observationRepository;
        this.forecastRepository = forecastRepository;
    }

    @Scheduled(fixedRate = 1800000, initialDelay = 60000)
    @Transactional
    public void syncWeatherData() {
        log.info("=== WeatherSyncScheduler: starting sync ===");
        List<District> districts = districtRepository.findAllByOrderByIdAsc();
        if (districts.isEmpty()) {
            log.warn("No districts configured, skipping sync");
            return;
        }

        // 1. Fetch forecast from Open-Meteo (more reliable)
        syncForecastFromOpenMeteo(districts);

        // 2. Fetch per-district live weather from Amap
        for (District district : districts) {
            try {
                syncDistrictObservation(district);
            } catch (Exception e) {
                log.error("Failed to sync observation for district {}: {}", district.getCode(), e.getMessage());
            }
        }
        log.info("=== WeatherSyncScheduler: sync finished ===");
    }

    private void syncForecastFromOpenMeteo(List<District> districts) {
        for (District district : districts) {
            OpenMeteoDtos.ForecastResponse response = openMeteoWeatherService.fetchForecast(district.getCode(), 16);
            if (response == null || response.daily == null) {
                log.warn("Open-Meteo forecast API failed for {}, skipping", district.getName());
                continue;
            }

            OpenMeteoDtos.Daily daily = response.daily;
            if (daily.time == null || daily.time.isEmpty()) {
                log.warn("Open-Meteo returned empty forecast data for {}", district.getName());
                continue;
            }

            for (int i = 0; i < daily.time.size(); i++) {
                LocalDate date = LocalDate.parse(daily.time.get(i));
                String weekLabel = WEEK_MAP.getOrDefault(date.getDayOfWeek(), "");
                int weatherCode = daily.weather_code != null && i < daily.weather_code.size() ? daily.weather_code.get(i) : 0;
                String weatherType = openMeteoWeatherService.getWeatherType(weatherCode);
                
                BigDecimal minTemp = openMeteoWeatherService.toBigDecimal(
                        daily.temperature_2m_min != null && i < daily.temperature_2m_min.size() ? daily.temperature_2m_min.get(i) : null);
                BigDecimal maxTemp = openMeteoWeatherService.toBigDecimal(
                        daily.temperature_2m_max != null && i < daily.temperature_2m_max.size() ? daily.temperature_2m_max.get(i) : null);
                
                int precipitation = daily.precipitation_probability_max != null && i < daily.precipitation_probability_max.size() 
                        ? daily.precipitation_probability_max.get(i) : 10;
                
                String windDir = "北";
                String windScale = "1";
                if (daily.wind_direction_10m_dominant != null && i < daily.wind_direction_10m_dominant.size()) {
                    windDir = openMeteoWeatherService.getWindDirection(daily.wind_direction_10m_dominant.get(i));
                }
                if (daily.wind_speed_10m_max != null && i < daily.wind_speed_10m_max.size()) {
                    windScale = openMeteoWeatherService.getWindScale(daily.wind_speed_10m_max.get(i));
                }

                String travelAdvice = generateTravelAdvice(weatherType, windDir, windScale);

                List<WeatherForecast> existing = forecastRepository.findByDistrictOrderByForecastDateAsc(district);
                existing.stream()
                        .filter(f -> f.getForecastDate().equals(date))
                        .forEach(f -> forecastRepository.delete(f));

                WeatherForecast entity = new WeatherForecast(
                        district, date, weekLabel, weatherType, minTemp, maxTemp,
                        precipitation, windDir + "风", windScale + "级", travelAdvice
                );
                forecastRepository.save(entity);
            }
            log.info("Forecast sync complete for {} ({} days)", district.getName(), daily.time.size());
        }
    }

    private void syncDistrictObservation(District district) {
        AmapDtos.LiveWeatherResponse response = amapWeatherService.fetchDistrictLiveWeather(district.getCode());
        if (response == null || response.lives == null || response.lives.isEmpty()) return;

        AmapDtos.Live live = response.lives.get(0);
        BigDecimal temp = new BigDecimal(live.temperature);
        String weatherType = live.weather;
        Integer humidity = live.humidity != null ? Integer.parseInt(live.humidity) : 75;

        BigDecimal apparentTemp = temp;
        OpenMeteoDtos.CurrentResponse currentResp = openMeteoWeatherService.fetchCurrent(district.getCode());
        if (currentResp != null && currentResp.current != null && currentResp.current.apparent_temperature != 0) {
            apparentTemp = BigDecimal.valueOf(currentResp.current.apparent_temperature).setScale(1, RoundingMode.HALF_UP);
            log.info("Using real apparent temperature from Open-Meteo: {}°C", apparentTemp);
        }

        String aqi = "良好";
        if (temp.intValue() > 32 && humidity > 70) {
            aqi = "中等";
        }

        WeatherObservation observation = new WeatherObservation(
                district,
                LocalDateTime.now(SHANGHAI).withSecond(0).withNano(0),
                weatherType,
                temp,
                apparentTemp,
                humidity,
                live.winddirection + "风",
                live.windpower + "级",
                aqi,
                estimatePrecipitation(weatherType, weatherType),
                temp.intValue() > 33 ? "偏热" : "较舒适",
                temp.intValue() > 30 ? "中等" : "较低",
                generateTravelAdvice(weatherType, live.winddirection, live.windpower)
        );
        observationRepository.save(observation);
        log.info("Saved live weather for {}: {} {}, {}°C (feels {}°C), humidity {}%",
                district.getName(), weatherType, live.winddirection, live.temperature, apparentTemp, humidity);
    }

    private int estimatePrecipitation(String dayWeather, String nightWeather) {
        String w = (dayWeather != null ? dayWeather : "") + (nightWeather != null ? nightWeather : "");
        if (w.contains("暴") || w.contains("大雨")) return 85;
        if (w.contains("中雨") || w.contains("雷")) return 65;
        if (w.contains("小雨") || w.contains("阵雨")) return 45;
        if (w.contains("多云")) return 25;
        return 10;
    }

    private String generateTravelAdvice(String weather, String wind, String power) {
        if (weather == null) return "适宜出行";
        if (weather.contains("暴") || weather.contains("大雨")) return "建议室内活动，避免户外出行";
        if (weather.contains("雷")) return "雷雨天气建议地铁出行";
        if (weather.contains("雨")) return "雨天路滑建议携带雨具";
        if (weather.contains("晴")) return "天气晴好适合各种出行方式";
        return "适宜出行，注意防晒";
    }
}