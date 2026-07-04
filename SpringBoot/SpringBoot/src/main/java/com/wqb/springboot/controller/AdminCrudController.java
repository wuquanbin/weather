package com.wqb.springboot.controller;

import com.wqb.springboot.dto.ApiResponse;
import com.wqb.springboot.entity.*;
import com.wqb.springboot.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminCrudController {

    private final DistrictRepository districtRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final RiskSegmentRepository riskSegmentRepository;
    private final WarningNoticeRepository warningNoticeRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final AdminUserRepository adminUserRepository;
    private final OperationLogRepository operationLogRepository;
    private final SystemParamRepository systemParamRepository;
    private final LifeIndexRepository lifeIndexRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminCrudController(
            DistrictRepository districtRepository,
            TravelPlaceRepository travelPlaceRepository,
            RiskSegmentRepository riskSegmentRepository,
            WarningNoticeRepository warningNoticeRepository,
            UserRepository userRepository,
            FeedbackRepository feedbackRepository,
            AdminUserRepository adminUserRepository,
            OperationLogRepository operationLogRepository,
            SystemParamRepository systemParamRepository,
            LifeIndexRepository lifeIndexRepository,
            PasswordEncoder passwordEncoder) {
        this.districtRepository = districtRepository;
        this.travelPlaceRepository = travelPlaceRepository;
        this.riskSegmentRepository = riskSegmentRepository;
        this.warningNoticeRepository = warningNoticeRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
        this.adminUserRepository = adminUserRepository;
        this.operationLogRepository = operationLogRepository;
        this.systemParamRepository = systemParamRepository;
        this.lifeIndexRepository = lifeIndexRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========== District CRUD ==========
    @GetMapping("/districts")
    public ApiResponse<List<District>> listDistricts() {
        return ApiResponse.ok(districtRepository.findAll());
    }

    @PostMapping("/districts")
    public ApiResponse<District> createDistrict(@RequestBody District district) {
        return ApiResponse.ok(districtRepository.save(district));
    }

    @PutMapping("/districts/{id}")
    public ApiResponse<District> updateDistrict(@PathVariable Long id, @RequestBody District district) {
        District existing = districtRepository.findById(id).orElseThrow(() -> new RuntimeException("区域不存在"));
        existing.setName(district.getName());
        existing.setCode(district.getCode());
        existing.setServiceArea(district.getServiceArea());
        existing.setHighlights(district.getHighlights());
        existing.setTransportFocus(district.getTransportFocus());
        existing.setLatitude(district.getLatitude());
        existing.setLongitude(district.getLongitude());
        existing.setAdminCode(district.getAdminCode());
        existing.setParentRegion(district.getParentRegion());
        return ApiResponse.ok(districtRepository.save(existing));
    }

    @DeleteMapping("/districts/{id}")
    public ApiResponse<Void> deleteDistrict(@PathVariable Long id) {
        districtRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ========== Travel Place CRUD ==========
    @GetMapping("/places")
    public ApiResponse<List<TravelPlace>> listPlaces() {
        return ApiResponse.ok(travelPlaceRepository.findAll());
    }

    @PostMapping("/places")
    public ApiResponse<TravelPlace> createPlace(@RequestBody TravelPlace place) {
        return ApiResponse.ok(travelPlaceRepository.save(place));
    }

    @PutMapping("/places/{id}")
    public ApiResponse<TravelPlace> updatePlace(@PathVariable Long id, @RequestBody TravelPlace place) {
        TravelPlace existing = travelPlaceRepository.findById(id).orElseThrow(() -> new RuntimeException("地点不存在"));
        existing.setName(place.getName());
        existing.setAddress(place.getAddress());
        existing.setLocation(place.getLocation());
        existing.setIndoor(place.getIndoor());
        existing.setWeatherTags(place.getWeatherTags());
        existing.setSceneTags(place.getSceneTags());
        existing.setRecommendLevel(place.getRecommendLevel());
        existing.setHighlight(place.getHighlight());
        return ApiResponse.ok(travelPlaceRepository.save(existing));
    }

    @DeleteMapping("/places/{id}")
    public ApiResponse<Void> deletePlace(@PathVariable Long id) {
        travelPlaceRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ========== Risk Segment CRUD ==========
    @GetMapping("/risk-segments")
    public ApiResponse<List<RiskSegment>> listRiskSegments() {
        return ApiResponse.ok(riskSegmentRepository.findAll());
    }

    @PostMapping("/risk-segments")
    public ApiResponse<RiskSegment> createRiskSegment(@RequestBody RiskSegment segment) {
        return ApiResponse.ok(riskSegmentRepository.save(segment));
    }

    @PutMapping("/risk-segments/{id}")
    public ApiResponse<RiskSegment> updateRiskSegment(@PathVariable Long id, @RequestBody RiskSegment segment) {
        RiskSegment existing = riskSegmentRepository.findById(id).orElseThrow(() -> new RuntimeException("路段不存在"));
        existing.setName(segment.getName());
        existing.setLocation(segment.getLocation());
        existing.setRiskType(segment.getRiskType());
        existing.setTriggerWeatherTags(segment.getTriggerWeatherTags());
        existing.setDescription(segment.getDescription());
        existing.setAdvice(segment.getAdvice());
        existing.setPriority(segment.getPriority());
        return ApiResponse.ok(riskSegmentRepository.save(existing));
    }

    @DeleteMapping("/risk-segments/{id}")
    public ApiResponse<Void> deleteRiskSegment(@PathVariable Long id) {
        riskSegmentRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ========== Warning Notice CRUD ==========
    @GetMapping("/warnings")
    public ApiResponse<List<WarningNotice>> listWarnings() {
        return ApiResponse.ok(warningNoticeRepository.findAll());
    }

    @PostMapping("/warnings")
    public ApiResponse<WarningNotice> createWarning(@RequestBody WarningNotice warning) {
        return ApiResponse.ok(warningNoticeRepository.save(warning));
    }

    @PutMapping("/warnings/{id}")
    public ApiResponse<WarningNotice> updateWarning(@PathVariable Long id, @RequestBody WarningNotice warning) {
        WarningNotice existing = warningNoticeRepository.findById(id).orElseThrow(() -> new RuntimeException("预警不存在"));
        existing.setTitle(warning.getTitle());
        existing.setContent(warning.getContent());
        existing.setSeverity(warning.getSeverity());
        existing.setWarningType(warning.getWarningType());
        existing.setStatus(warning.getStatus());
        existing.setIssuedAt(warning.getIssuedAt());
        existing.setExpiresAt(warning.getExpiresAt());
        existing.setImpactArea(warning.getImpactArea());
        existing.setDefenseGuidance(warning.getDefenseGuidance());
        return ApiResponse.ok(warningNoticeRepository.save(existing));
    }

    @DeleteMapping("/warnings/{id}")
    public ApiResponse<Void> deleteWarning(@PathVariable Long id) {
        warningNoticeRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ========== User Management ==========
    @GetMapping("/users")
    public ApiResponse<List<User>> listUsers() {
        return ApiResponse.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        return ApiResponse.ok(userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在")));
    }

    // ========== Feedback Management ==========
    @GetMapping("/feedbacks")
    public ApiResponse<List<Feedback>> listFeedbacks() {
        return ApiResponse.ok(feedbackRepository.findAll());
    }

    @PutMapping("/feedbacks/{id}")
    public ApiResponse<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        Feedback existing = feedbackRepository.findById(id).orElseThrow(() -> new RuntimeException("反馈不存在"));
        existing.setStatus(feedback.getStatus());
        existing.setReply(feedback.getReply());
        return ApiResponse.ok(feedbackRepository.save(existing));
    }

    // ========== Admin User CRUD ==========
    @GetMapping("/admins")
    public ApiResponse<List<AdminUser>> listAdmins() {
        return ApiResponse.ok(adminUserRepository.findAll());
    }

    @PostMapping("/admins")
    public ApiResponse<AdminUser> createAdmin(@RequestBody AdminUser admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return ApiResponse.ok(adminUserRepository.save(admin));
    }

    @PutMapping("/admins/{id}")
    public ApiResponse<AdminUser> updateAdmin(@PathVariable Long id, @RequestBody AdminUser admin) {
        AdminUser existing = adminUserRepository.findById(id).orElseThrow(() -> new RuntimeException("管理员不存在"));
        existing.setRealName(admin.getRealName());
        existing.setPhone(admin.getPhone());
        existing.setEmail(admin.getEmail());
        existing.setRole(admin.getRole());
        existing.setStatus(admin.getStatus());
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        return ApiResponse.ok(adminUserRepository.save(existing));
    }

    @DeleteMapping("/admins/{id}")
    public ApiResponse<Void> deleteAdmin(@PathVariable Long id) {
        adminUserRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ========== System Params ==========
    @GetMapping("/system-params")
    public ApiResponse<List<SystemParam>> listSystemParams() {
        return ApiResponse.ok(systemParamRepository.findAll());
    }

    @PostMapping("/system-params")
    public ApiResponse<SystemParam> createSystemParam(@RequestBody SystemParam param) {
        return ApiResponse.ok(systemParamRepository.save(param));
    }

    @PutMapping("/system-params/{id}")
    public ApiResponse<SystemParam> updateSystemParam(@PathVariable Long id, @RequestBody SystemParam param) {
        SystemParam existing = systemParamRepository.findById(id).orElseThrow(() -> new RuntimeException("参数不存在"));
        existing.setParamValue(param.getParamValue());
        existing.setDescription(param.getDescription());
        existing.setGroupName(param.getGroupName());
        return ApiResponse.ok(systemParamRepository.save(existing));
    }

    @DeleteMapping("/system-params/{id}")
    public ApiResponse<Void> deleteSystemParam(@PathVariable Long id) {
        systemParamRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    // ========== Operation Logs ==========
    @GetMapping("/operation-logs")
    public ApiResponse<List<OperationLog>> listOperationLogs() {
        return ApiResponse.ok(operationLogRepository.findAll());
    }

    // ========== Life Index CRUD ==========
    @GetMapping("/life-index")
    public ApiResponse<List<LifeIndex>> listLifeIndex() {
        return ApiResponse.ok(lifeIndexRepository.findAll());
    }

    @PostMapping("/life-index")
    public ApiResponse<LifeIndex> createLifeIndex(@RequestBody LifeIndex index) {
        return ApiResponse.ok(lifeIndexRepository.save(index));
    }

    @PutMapping("/life-index/{id}")
    public ApiResponse<LifeIndex> updateLifeIndex(@PathVariable Long id, @RequestBody LifeIndex index) {
        LifeIndex existing = lifeIndexRepository.findById(id).orElseThrow(() -> new RuntimeException("指数不存在"));
        existing.setIndexType(index.getIndexType());
        existing.setLevel(index.getLevel());
        existing.setDescription(index.getDescription());
        existing.setAdvice(index.getAdvice());
        return ApiResponse.ok(lifeIndexRepository.save(existing));
    }

    @DeleteMapping("/life-index/{id}")
    public ApiResponse<Void> deleteLifeIndex(@PathVariable Long id) {
        lifeIndexRepository.deleteById(id);
        return ApiResponse.ok(null);
    }
}
