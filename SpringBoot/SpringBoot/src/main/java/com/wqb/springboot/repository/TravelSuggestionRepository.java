package com.wqb.springboot.repository;

import com.wqb.springboot.entity.District;
import com.wqb.springboot.entity.TravelSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelSuggestionRepository extends JpaRepository<TravelSuggestion, Long> {

    List<TravelSuggestion> findByDistrictOrderByPriorityDescIdAsc(District district);
}
