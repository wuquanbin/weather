package com.wqb.springboot.service;

import com.wqb.springboot.config.AmapConfig;
import com.wqb.springboot.dto.AmapDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
public class AmapPoiService {

    private static final Logger log = LoggerFactory.getLogger(AmapPoiService.class);

    private final AmapConfig amapConfig;
    private final RestTemplate restTemplate;

    public AmapPoiService(AmapConfig amapConfig, RestTemplate restTemplate) {
        this.amapConfig = amapConfig;
        this.restTemplate = restTemplate;
    }

    /**
     * Text search for POIs near a location.
     * @param keywords search keywords
     * @param location "lng,lat" center point (optional)
     * @param city city name or adcode
     * @param types POI type codes (optional)
     * @param radius search radius in meters (optional, used with location)
     */
    public List<AmapDtos.Poi> search(String keywords, String location, String city, String types, String radius) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/place/text")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("output", "JSON")
                .queryParam("offset", 20);

        if (keywords != null && !keywords.isBlank()) {
            builder.queryParam("keywords", keywords);
        }
        if (location != null && !location.isBlank()) {
            builder.queryParam("location", location);
        }
        if (city != null && !city.isBlank()) {
            builder.queryParam("city", city);
        }
        if (types != null && !types.isBlank()) {
            builder.queryParam("types", types);
        }
        if (radius != null && !radius.isBlank()) {
            builder.queryParam("radius", radius);
        }

        URI uri = builder.build().toUri();
        log.info("POI search: {}", uri);

        AmapDtos.PoiSearchResponse response = restTemplate.getForObject(uri, AmapDtos.PoiSearchResponse.class);
        if (response == null || !"1".equals(response.status) || response.pois == null) {
            log.warn("POI search failed: {}", response != null ? response.info : "null");
            return Collections.emptyList();
        }
        return response.pois;
    }

    /**
     * Nearby search for POIs.
     * @param keywords search keywords
     * @param location "lng,lat" center point
     * @param types POI type codes (optional)
     * @param radius search radius in meters
     */
    public List<AmapDtos.Poi> aroundSearch(String keywords, String location, String types, String radius) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/place/around")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("output", "JSON")
                .queryParam("offset", 20);

        if (keywords != null && !keywords.isBlank()) {
            builder.queryParam("keywords", keywords);
        }
        if (location != null && !location.isBlank()) {
            builder.queryParam("location", location);
        }
        if (types != null && !types.isBlank()) {
            builder.queryParam("types", types);
        }
        if (radius != null && !radius.isBlank()) {
            builder.queryParam("radius", radius);
        }

        URI uri = builder.build().toUri();
        log.info("POI around search: {}", uri);

        AmapDtos.PoiSearchResponse response = restTemplate.getForObject(uri, AmapDtos.PoiSearchResponse.class);
        if (response == null || !"1".equals(response.status) || response.pois == null) {
            log.warn("POI around search failed: {}", response != null ? response.info : "null");
            return Collections.emptyList();
        }
        return response.pois;
    }
}
