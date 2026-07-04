package com.wqb.springboot.repository;

import com.wqb.springboot.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStatus(String status);
    long countByStatus(String status);
}
