package com.wqb.springboot.controller;

import com.wqb.springboot.dto.ApiResponse;
import com.wqb.springboot.dto.AuthDtos;
import com.wqb.springboot.entity.AdminUser;
import com.wqb.springboot.repository.AdminUserRepository;
import com.wqb.springboot.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AdminAuthController(AdminUserRepository adminUserRepository,
                               PasswordEncoder passwordEncoder,
                               JwtUtil jwtUtil) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.LoginResponse> login(@RequestBody AuthDtos.LoginRequest request) {
        AdminUser admin = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        admin.setLastLoginTime(LocalDateTime.now());
        adminUserRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole());

        return ApiResponse.ok(new AuthDtos.LoginResponse(
                token,
                admin.getUsername(),
                admin.getRealName(),
                admin.getRole()
        ));
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> adminInfo() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Map<String, Object> info = new HashMap<>();
        info.put("username", admin.getUsername());
        info.put("realName", admin.getRealName());
        info.put("role", admin.getRole());
        info.put("phone", admin.getPhone());
        info.put("email", admin.getEmail());

        return ApiResponse.ok(info);
    }
}
