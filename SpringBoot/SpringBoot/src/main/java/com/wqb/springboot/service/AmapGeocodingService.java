package com.wqb.springboot.service;

import com.wqb.springboot.config.AmapConfig;
import com.wqb.springboot.dto.AmapDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class AmapGeocodingService {

    private static final Logger log = LoggerFactory.getLogger(AmapGeocodingService.class);

    private final AmapConfig amapConfig;
    private final RestTemplate restTemplate;

    public AmapGeocodingService(AmapConfig amapConfig, RestTemplate restTemplate) {
        this.amapConfig = amapConfig;
        this.restTemplate = restTemplate;
    }

    /**
     * Geocode an address to lng,lat coordinates.
     * Uses raw string response to handle Amap returning [] for some fields.
     */
    public String geocode(String address, String city) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/geocode/geo")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("address", address)
                .queryParam("output", "JSON");
        if (city != null && !city.isBlank()) {
            builder.queryParam("city", city);
        }
        URI uri = builder.build().toUri();

        log.info("Geocoding: {}", uri);
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = response.getBody();
            if (body == null || !body.contains("\"status\":\"1\"")) {
                log.warn("Geocoding failed for '{}': {}", address, body);
                return null;
            }
            return extractJsonValue(body, "location");
        } catch (Exception e) {
            log.error("Geocoding error for '{}': {}", address, e.getMessage());
            return null;
        }
    }

    /**
     * Reverse geocode coordinates to an address.
     */
    public AmapDtos.Regeocode reverseGeocode(String location) {
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/geocode/regeo")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("location", location)
                .queryParam("output", "JSON")
                .build()
                .toUri();

        log.info("Reverse geocoding: {}", uri);
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = response.getBody();
            if (body == null || !body.contains("\"status\":\"1\"")) {
                log.warn("Reverse geocoding failed for '{}': {}", location, body);
                return null;
            }
            AmapDtos.Regeocode regeocode = new AmapDtos.Regeocode();
            regeocode.formattedAddress = extractJsonValue(body, "formatted_address");
            AmapDtos.AddressComponent comp = new AmapDtos.AddressComponent();
            comp.province = extractFromBody(body, "province");
            comp.city = extractFromBody(body, "city");
            comp.district = extractFromBody(body, "district");
            comp.adcode = extractFromBody(body, "adcode");
            regeocode.addressComponent = comp;
            return regeocode;
        } catch (Exception e) {
            log.error("Reverse geocoding error for '{}': {}", location, e.getMessage());
            return null;
        }
    }

    /** Extract a string value for a key from the top-level addressComponent block. */
    private String extractFromBody(String json, String key) {
        int compIdx = json.indexOf("\"addressComponent\"");
        if (compIdx < 0) return "";
        String sub = json.substring(compIdx);
        return extractJsonValue(sub, key);
    }

    /** Extract "key":"value" from JSON, skipping arrays/objects. */
    private String extractJsonValue(String json, String key) {
        String needle = "\"" + key + "\":";
        int idx = json.indexOf(needle);
        if (idx < 0) return "";
        int valStart = idx + needle.length();
        if (valStart >= json.length()) return "";
        char first = json.charAt(valStart);
        if (first == '"') {
            // string value
            int endQuote = json.indexOf('"', valStart + 1);
            if (endQuote < 0) return "";
            return json.substring(valStart + 1, endQuote);
        }
        // array or object - return empty
        return "";
    }
}
