package com.wqb.springboot.repository;

import com.wqb.springboot.entity.District;
import com.wqb.springboot.entity.TravelPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPlaceRepository extends JpaRepository<TravelPlace, Long> {

    List<TravelPlace> findByDistrictOrderByRecommendLevelDescIdAsc(District district);
}
