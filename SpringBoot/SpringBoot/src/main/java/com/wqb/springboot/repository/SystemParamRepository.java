package com.wqb.springboot.repository;

import com.wqb.springboot.entity.SystemParam;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemParamRepository extends JpaRepository<SystemParam, Long> {
    Optional<SystemParam> findByParamKey(String paramKey);
}
