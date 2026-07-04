package com.wqb.springboot.repository;

import com.wqb.springboot.entity.WarningNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarningNoticeRepository extends JpaRepository<WarningNotice, Long> {

    List<WarningNotice> findByStatus(String status);
    long countByStatus(String status);
}
