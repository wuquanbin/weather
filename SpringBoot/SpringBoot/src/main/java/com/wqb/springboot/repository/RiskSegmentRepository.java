package com.wqb.springboot.repository;

import com.wqb.springboot.entity.District;
import com.wqb.springboot.entity.RiskSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskSegmentRepository extends JpaRepository<RiskSegment, Long> {

    List<RiskSegment> findByDistrictOrderByPriorityDescIdAsc(District district);
}
