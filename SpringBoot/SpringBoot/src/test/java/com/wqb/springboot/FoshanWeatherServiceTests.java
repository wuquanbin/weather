package com.wqb.springboot;

import com.wqb.springboot.dto.WeatherResponseDtos;
import com.wqb.springboot.service.FoshanWeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FoshanWeatherServiceTests {

    @Autowired
    private FoshanWeatherService foshanWeatherService;

    @Test
    void shouldReturnDashboardOverview() {
        WeatherResponseDtos.DashboardOverview overview = foshanWeatherService.getDashboardOverview("chancheng");

        assertNotNull(overview);
        assertEquals("chancheng", overview.district().code());
        assertNotNull(overview.currentWeather());
        assertFalse(overview.forecast().isEmpty());
        assertFalse(overview.travelSuggestions().isEmpty());
    }
}
