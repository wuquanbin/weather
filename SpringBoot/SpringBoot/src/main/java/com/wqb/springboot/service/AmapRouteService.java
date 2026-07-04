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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmapRouteService {

    private static final Logger log = LoggerFactory.getLogger(AmapRouteService.class);

    private final AmapConfig amapConfig;
    private final RestTemplate restTemplate;

    public AmapRouteService(AmapConfig amapConfig, RestTemplate restTemplate) {
        this.amapConfig = amapConfig;
        this.restTemplate = restTemplate;
    }

    public AmapDtos.DrivingRoute driving(String origin, String destination) {
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/direction/driving")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("strategy", 10)
                .queryParam("extensions", "base")
                .build().toUri();

        log.info("Driving route: {} -> {}", origin, destination);
        try {
            ResponseEntity<String> resp = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = resp.getBody();
            if (body == null || !body.contains("\"status\":\"1\"")) {
                log.warn("Driving route failed: {}", body);
                return null;
            }
            return parseDrivingRoute(body);
        } catch (Exception e) {
            log.error("Driving route error: {}", e.getMessage());
            return null;
        }
    }

    public AmapDtos.WalkingRoute walking(String origin, String destination) {
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/direction/walking")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .build().toUri();

        log.info("Walking route: {} -> {}", origin, destination);
        try {
            ResponseEntity<String> resp = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = resp.getBody();
            if (body == null || !body.contains("\"status\":\"1\"")) {
                log.warn("Walking route failed: {}", body);
                return null;
            }
            return parseWalkingRoute(body);
        } catch (Exception e) {
            log.error("Walking route error: {}", e.getMessage());
            return null;
        }
    }

    public AmapDtos.BicyclingRoute bicycling(String origin, String destination) {
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v4/direction/bicycling")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .build().toUri();

        log.info("Bicycling route: {} -> {}", origin, destination);
        try {
            ResponseEntity<String> resp = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = resp.getBody();
            if (body == null || !body.contains("\"status\":\"1\"")) {
                log.warn("Bicycling route failed: {}", body);
                return null;
            }
            return parseBicyclingRoute(body);
        } catch (Exception e) {
            log.error("Bicycling route error: {}", e.getMessage());
            return null;
        }
    }

    public AmapDtos.TransitRoute transit(String origin, String destination, String city, String cityd) {
        URI uri = UriComponentsBuilder.fromUriString(amapConfig.getBaseUrl() + "/v3/direction/transit/integrated")
                .queryParam("key", amapConfig.getApiKey())
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("city", city)
                .queryParam("cityd", cityd)
                .queryParam("strategy", 0)
                .build().toUri();

        log.info("Transit route: {} -> {} ({} -> {})", origin, destination, city, cityd);
        try {
            ResponseEntity<String> resp = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
            String body = resp.getBody();
            if (body == null || !body.contains("\"status\":\"1\"")) {
                log.warn("Transit route failed: {}", body);
                return null;
            }
            return parseTransitRoute(body);
        } catch (Exception e) {
            log.error("Transit route error: {}", e.getMessage());
            return null;
        }
    }

    // ========== Safe JSON field extractors ==========

    private String extractString(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    // ========== Route parsers ==========

    private AmapDtos.DrivingRoute parseDrivingRoute(String body) {
        AmapDtos.DrivingRoute route = new AmapDtos.DrivingRoute();
        route.origin = extractString(body, "origin");
        route.destination = extractString(body, "destination");
        route.paths = new ArrayList<>();

        List<String> pathBlocks = splitJsonArrayObjects(body, "paths");
        for (String pb : pathBlocks) {
            AmapDtos.DrivingPath path = new AmapDtos.DrivingPath();
            path.distance = extractString(pb, "distance");
            path.duration = extractString(pb, "duration");
            path.trafficLights = extractString(pb, "traffic_lights");
            path.steps = parseDrivingSteps(pb);
            route.paths.add(path);
        }
        return route;
    }

    private List<AmapDtos.DrivingStep> parseDrivingSteps(String pathBlock) {
        List<AmapDtos.DrivingStep> steps = new ArrayList<>();
        List<String> stepBlocks = splitJsonArrayObjects(pathBlock, "steps");
        for (String sb : stepBlocks) {
            AmapDtos.DrivingStep step = new AmapDtos.DrivingStep();
            step.instruction = extractString(sb, "instruction");
            step.road = extractString(sb, "road");
            step.distance = extractString(sb, "distance");
            step.duration = extractString(sb, "duration");
            steps.add(step);
        }
        return steps;
    }

    private AmapDtos.WalkingRoute parseWalkingRoute(String body) {
        AmapDtos.WalkingRoute route = new AmapDtos.WalkingRoute();
        route.origin = extractString(body, "origin");
        route.destination = extractString(body, "destination");
        route.paths = new ArrayList<>();

        List<String> pathBlocks = splitJsonArrayObjects(body, "paths");
        for (String pb : pathBlocks) {
            AmapDtos.WalkingPath path = new AmapDtos.WalkingPath();
            path.distance = extractString(pb, "distance");
            path.duration = extractString(pb, "duration");
            path.steps = new ArrayList<>();
            List<String> stepBlocks = splitJsonArrayObjects(pb, "steps");
            for (String sb : stepBlocks) {
                AmapDtos.WalkingStep step = new AmapDtos.WalkingStep();
                step.instruction = extractString(sb, "instruction");
                step.road = extractString(sb, "road");
                step.distance = extractString(sb, "distance");
                step.duration = extractString(sb, "duration");
                path.steps.add(step);
            }
            route.paths.add(path);
        }
        return route;
    }

    private AmapDtos.BicyclingRoute parseBicyclingRoute(String body) {
        AmapDtos.BicyclingRoute route = new AmapDtos.BicyclingRoute();
        route.origin = extractString(body, "origin");
        route.destination = extractString(body, "destination");
        route.paths = new ArrayList<>();

        List<String> pathBlocks = splitJsonArrayObjects(body, "paths");
        for (String pb : pathBlocks) {
            AmapDtos.BicyclingPath path = new AmapDtos.BicyclingPath();
            path.distance = extractString(pb, "distance");
            path.duration = extractString(pb, "duration");
            path.steps = new ArrayList<>();
            List<String> stepBlocks = splitJsonArrayObjects(pb, "steps");
            for (String sb : stepBlocks) {
                AmapDtos.BicyclingStep step = new AmapDtos.BicyclingStep();
                step.instruction = extractString(sb, "instruction");
                step.road = extractString(sb, "road");
                step.distance = extractString(sb, "distance");
                step.duration = extractString(sb, "duration");
                path.steps.add(step);
            }
            route.paths.add(path);
        }
        return route;
    }

    private AmapDtos.TransitRoute parseTransitRoute(String body) {
        AmapDtos.TransitRoute route = new AmapDtos.TransitRoute();
        route.distance = extractString(body, "distance");
        route.duration = extractString(body, "duration");
        route.transits = new ArrayList<>();

        List<String> transitBlocks = splitJsonArrayObjects(body, "transits");
        for (String tb : transitBlocks) {
            AmapDtos.Transit transit = new AmapDtos.Transit();
            transit.duration = extractString(tb, "duration");
            transit.distance = extractString(tb, "distance");
            transit.cost = extractString(tb, "cost");
            transit.segments = new ArrayList<>();

            List<String> segBlocks = splitJsonArrayObjects(tb, "segments");
            for (String seg : segBlocks) {
                AmapDtos.TransitSegment segment = new AmapDtos.TransitSegment();
                if (seg.contains("\"walking\"")) {
                    int wStart = seg.indexOf("\"walking\"");
                    String wBlock = seg.substring(wStart);
                    segment.walking = new AmapDtos.TransitWalking();
                    segment.walking.origin = extractString(wBlock, "origin");
                    segment.walking.destination = extractString(wBlock, "destination");
                    segment.walking.distance = extractString(wBlock, "distance");
                    segment.walking.duration = extractString(wBlock, "duration");
                }
                if (seg.contains("\"bus\"")) {
                    segment.bus = new AmapDtos.TransitBus();
                    segment.bus.buslines = new ArrayList<>();
                    List<String> busBlocks = splitJsonArrayObjects(seg, "buslines");
                    for (String bb : busBlocks) {
                        AmapDtos.TransitBusLine line = new AmapDtos.TransitBusLine();
                        line.name = extractString(bb, "name");
                        line.departure_stop = extractString(bb, "departure_stop");
                        line.arrival_stop = extractString(bb, "arrival_stop");
                        line.distance = extractString(bb, "distance");
                        line.duration = extractString(bb, "duration");
                        segment.bus.buslines.add(line);
                    }
                }
                transit.segments.add(segment);
            }
            route.transits.add(transit);
        }
        return route;
    }

    /** Split a JSON array into individual object strings. */
    private List<String> splitJsonArrayObjects(String json, String arrayName) {
        List<String> result = new ArrayList<>();
        int arrStart = json.indexOf("\"" + arrayName + "\":[");
        if (arrStart < 0) return result;
        arrStart = json.indexOf('[', arrStart);
        if (arrStart < 0) return result;

        int depth = 0;
        int objStart = -1;
        for (int i = arrStart + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) objStart = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart >= 0) {
                    result.add(json.substring(objStart, i + 1));
                    objStart = -1;
                }
            } else if (c == ']' && depth == 0) {
                break;
            }
        }
        return result;
    }
}
