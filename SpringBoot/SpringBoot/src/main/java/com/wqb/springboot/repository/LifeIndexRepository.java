package com.wqb.springboot.repository;

import com.wqb.springboot.entity.LifeIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LifeIndexRepository extends JpaRepository<LifeIndex, Long> {
    List<LifeIndex> findByDistrictIdAndIndexDate(Long districtId, LocalDate indexDate);
    List<LifeIndex> findByDistrictCodeAndIndexDate(String districtCode, LocalDate indexDate);
}
