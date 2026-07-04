package com.wqb.springboot.service;

import com.wqb.springboot.config.AmapConfig;
import com.wqb.springboot.dto.AmapDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Calls the Amap Weather REST API for Foshan city-level forecasts.
 * city adcode for Foshan: 440600
 */
@Service
public class AmapWeatherService {

    private static final Logger log = LoggerFactory.getLogger(AmapWeatherService.class);

    private static final Map<String, String> DISTRICT_ADCODES = Map.of(
            "chancheng", "440604",
            "nanhai",    "440605",
            "shunde",    "440606",
            "sanshui",   "440607",
            "gaoming",   "440608"
    );

    private static final String FOSHAN_CITY_ADCODE = "440600";

    private final AmapConfig amapConfig;
    private final RestTemplate restTemplate;

    public AmapWeatherService(AmapConfig amapConfig, RestTemplate restTemplate) {
        this.amapConfig = amapConfig;
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch 4-day forecast for Foshan city (all extensions).
     */
    public AmapDtos.WeatherResponse fetchFoshanForecast() {
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/weather/weatherInfo")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("city", FOSHAN_CITY_ADCODE)
                .queryParam("extensions", "all")
                .queryParam("output", "JSON")
                .build()
                .toUri();

        log.info("Calling Amap weather API: {}", uri);
        AmapDtos.WeatherResponse response = restTemplate.getForObject(uri, AmapDtos.WeatherResponse.class);
        if (response == null || !"1".equals(response.status)) {
            log.warn("Amap weather API failed: {}", response != null ? response.info : "null response");
            return null;
        }
        return response;
    }

    /**
     * Fetch live weather for a specific district adcode using extensions=base.
     * Returns LiveWeatherResponse which contains lives array with real-time data.
     */
    public AmapDtos.LiveWeatherResponse fetchDistrictLiveWeather(String districtCode) {
        String adcode = DISTRICT_ADCODES.getOrDefault(districtCode, FOSHAN_CITY_ADCODE);
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/weather/weatherInfo")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("city", adcode)
                .queryParam("extensions", "base")
                .queryParam("output", "JSON")
                .build()
                .toUri();

        log.info("Calling Amap live weather API for district {}: {}", districtCode, uri);
        AmapDtos.LiveWeatherResponse response = restTemplate.getForObject(uri, AmapDtos.LiveWeatherResponse.class);
        if (response == null || !"1".equals(response.status)) {
            log.warn("Amap live weather API failed for {}: {}", districtCode, response != null ? response.info : "null");
            return null;
        }
        return response;
    }

    /**
     * Fetch weather forecast for a specific district adcode using extensions=base.
     * This returns WeatherResponse but only the first cast (today) is reliable.
     * @deprecated Use fetchDistrictLiveWeather for real-time data and fetchFoshanForecast for forecasts.
     */
    @Deprecated
    public AmapDtos.WeatherResponse fetchDistrictWeather(String districtCode) {
        String adcode = DISTRICT_ADCODES.getOrDefault(districtCode, FOSHAN_CITY_ADCODE);
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/weather/weatherInfo")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("city", adcode)
                .queryParam("extensions", "base")
                .queryParam("output", "JSON")
                .build()
                .toUri();

        log.info("Calling Amap weather API (deprecated) for district {}: {}", districtCode, uri);
        AmapDtos.WeatherResponse response = restTemplate.getForObject(uri, AmapDtos.WeatherResponse.class);
        if (response == null || !"1".equals(response.status)) {
            log.warn("Amap weather API failed for {}: {}", districtCode, response != null ? response.info : "null");
            return null;
        }
        return response;
    }

    public Map<String, String> getDistrictAdcodes() {
        return DISTRICT_ADCODES;
    }

    public String getFoshanCityAdcode() {
        return FOSHAN_CITY_ADCODE;
    }
}
