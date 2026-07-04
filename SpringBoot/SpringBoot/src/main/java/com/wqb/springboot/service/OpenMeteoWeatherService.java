package com.wqb.springboot.service;

import com.wqb.springboot.dto.OpenMeteoDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenMeteoWeatherService {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherService.class);

    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    private static final Map<String, double[]> DISTRICT_COORDS = Map.of(
            "chancheng", new double[]{23.0225, 113.1213},
            "nanhai",    new double[]{23.0145, 113.1178},
            "shunde",    new double[]{22.8055, 113.2105},
            "sanshui",   new double[]{23.1955, 112.8815},
            "gaoming",   new double[]{22.8555, 112.5215}
    );

    private static final Map<Integer, String> WEATHER_CODE_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "晴");
        map.put(1, "晴");
        map.put(2, "多云");
        map.put(3, "阴");
        map.put(45, "雾");
        map.put(48, "雾凇");
        map.put(51, "毛毛雨");
        map.put(53, "小雨");
        map.put(55, "小雨");
        map.put(56, "冻雨");
        map.put(57, "冻雨");
        map.put(61, "小雨");
        map.put(63, "中雨");
        map.put(65, "大雨");
        map.put(66, "冻雨");
        map.put(67, "冻雨");
        map.put(71, "小雪");
        map.put(73, "中雪");
        map.put(75, "大雪");
        map.put(77, "雪粒");
        map.put(80, "阵雨");
        map.put(81, "阵雨");
        map.put(82, "雷阵雨");
        map.put(85, "阵雪");
        map.put(86, "阵雪");
        map.put(95, "雷阵雨");
        map.put(96, "雷阵雨");
        map.put(99, "雷阵雨");
        WEATHER_CODE_MAP = Map.copyOf(map);
    }

    private final RestTemplate restTemplate;

    public OpenMeteoWeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OpenMeteoDtos.CurrentResponse fetchCurrent(String districtCode) {
        double[] coords = DISTRICT_COORDS.getOrDefault(districtCode, DISTRICT_COORDS.get("chancheng"));

        URI uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("latitude", coords[0])
                .queryParam("longitude", coords[1])
                .queryParam("current", "temperature_2m,apparent_temperature,relative_humidity_2m")
                .queryParam("timezone", "Asia/Shanghai")
                .build()
                .toUri();

        log.info("Calling Open-Meteo current API: {}", uri);
        try {
            return restTemplate.getForObject(uri, OpenMeteoDtos.CurrentResponse.class);
        } catch (Exception e) {
            log.warn("Open-Meteo current API failed: {}", e.getMessage());
            return null;
        }
    }

    public OpenMeteoDtos.ForecastResponse fetchForecast(String districtCode, int days) {
        double[] coords = DISTRICT_COORDS.getOrDefault(districtCode, DISTRICT_COORDS.get("chancheng"));
        
        URI uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("latitude", coords[0])
                .queryParam("longitude", coords[1])
                .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_direction_10m_dominant,wind_speed_10m_max")
                .queryParam("timezone", "Asia/Shanghai")
                .queryParam("forecast_days", days)
                .build()
                .toUri();

        log.info("Calling Open-Meteo forecast API: {}", uri);
        try {
            return restTemplate.getForObject(uri, OpenMeteoDtos.ForecastResponse.class);
        } catch (Exception e) {
            log.warn("Open-Meteo API failed: {}", e.getMessage());
            return null;
        }
    }

    public String getWeatherType(int weatherCode) {
        return WEATHER_CODE_MAP.getOrDefault(weatherCode, "晴");
    }

    public String getWindDirection(int degree) {
        if (degree >= 340 || degree < 20) return "北";
        if (degree >= 20 && degree < 60) return "东北";
        if (degree >= 60 && degree < 100) return "东";
        if (degree >= 100 && degree < 140) return "东南";
        if (degree >= 140 && degree < 180) return "南";
        if (degree >= 180 && degree < 220) return "西南";
        if (degree >= 220 && degree < 260) return "西";
        if (degree >= 260 && degree < 300) return "西北";
        if (degree >= 300 && degree < 340) return "北";
        return "北";
    }

    public String getWindScale(double speed) {
        if (speed < 2) return "1";
        if (speed < 4) return "2";
        if (speed < 6) return "3";
        if (speed < 8) return "4";
        if (speed < 11) return "5";
        if (speed < 14) return "6";
        if (speed < 18) return "7";
        if (speed < 22) return "8";
        return "9";
    }

    public BigDecimal toBigDecimal(Double value) {
        if (value == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP);
    }
}