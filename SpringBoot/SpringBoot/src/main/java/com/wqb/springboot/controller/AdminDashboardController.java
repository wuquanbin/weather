package com.wqb.springboot.controller;

import com.wqb.springboot.dto.ApiResponse;
import com.wqb.springboot.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final WarningNoticeRepository warningNoticeRepository;
    private final FeedbackRepository feedbackRepository;
    private final DistrictRepository districtRepository;

    public AdminDashboardController(UserRepository userRepository,
                                    WarningNoticeRepository warningNoticeRepository,
                                    FeedbackRepository feedbackRepository,
                                    DistrictRepository districtRepository) {
        this.userRepository = userRepository;
        this.warningNoticeRepository = warningNoticeRepository;
        this.feedbackRepository = feedbackRepository;
        this.districtRepository = districtRepository;
    }

    @GetMapping("/dashboard/stats")
    public ApiResponse<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeWarnings", warningNoticeRepository.countByStatus("active"));
        stats.put("totalFeedback", feedbackRepository.count());
        stats.put("pendingFeedback", feedbackRepository.countByStatus("pending"));
        stats.put("totalDistricts", districtRepository.count());
        return ApiResponse.ok(stats);
    }
}
