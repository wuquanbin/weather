package com.wqb.springboot.repository;

import com.wqb.springboot.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findAllByOrderByIdAsc();

    Optional<District> findByCode(String code);
}
