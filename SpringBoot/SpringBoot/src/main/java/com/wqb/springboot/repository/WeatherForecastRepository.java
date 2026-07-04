package com.wqb.springboot.repository;

import com.wqb.springboot.entity.District;
import com.wqb.springboot.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {

    List<WeatherForecast> findByDistrictOrderByForecastDateAsc(District district);
}
