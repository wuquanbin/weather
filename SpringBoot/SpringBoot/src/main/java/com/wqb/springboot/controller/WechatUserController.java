package com.wqb.springboot.controller;

import com.wqb.springboot.dto.ApiResponse;
import com.wqb.springboot.dto.AuthDtos;
import com.wqb.springboot.entity.Feedback;
import com.wqb.springboot.entity.User;
import com.wqb.springboot.entity.UserFavoritePlace;
import com.wqb.springboot.repository.FeedbackRepository;
import com.wqb.springboot.repository.UserFavoritePlaceRepository;
import com.wqb.springboot.repository.UserRepository;
import com.wqb.springboot.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/wechat")
public class WechatUserController {

    @Value("${wechat.appid:}")
    private String appId;

    @Value("${wechat.secret:}")
    private String appSecret;

    private final UserRepository userRepository;
    private final UserFavoritePlaceRepository favoritePlaceRepository;
    private final FeedbackRepository feedbackRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    public WechatUserController(UserRepository userRepository,
                                 UserFavoritePlaceRepository favoritePlaceRepository,
                                 FeedbackRepository feedbackRepository,
                                 JwtUtil jwtUtil,
                                 RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.favoritePlaceRepository = favoritePlaceRepository;
        this.feedbackRepository = feedbackRepository;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    private String resolveOpenid(String code) {
        try {
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appId, appSecret, code
            );
            Map<?, ?> result = restTemplate.getForObject(url, Map.class);
            String openid = (String) result.get("openid");
            if (openid != null && !openid.isEmpty()) {
                return openid;
            }
        } catch (Exception e) {
            // WeChat API unavailable
        }
        // Dev fallback
        return "dev_" + code;
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.WechatLoginResponse> login(@RequestBody AuthDtos.WechatLoginRequest request) {
        final String openid = resolveOpenid(request.getCode());

        User user = userRepository.findByOpenid(openid).orElseGet(() -> {
            User newUser = new User();
            newUser.setOpenid(openid);
            newUser.setNickname("WeChat User");
            newUser.setStatus(1);
            return userRepository.save(newUser);
        });

        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getOpenid(), "USER");

        return ApiResponse.ok(new AuthDtos.WechatLoginResponse(
                token, user.getId(), user.getNickname(), user.getAvatarUrl()
        ));
    }

    @PutMapping("/user/profile")
    public ApiResponse<User> updateProfile(@RequestBody User userUpdate) {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String openid = auth.getName();

        User user = userRepository.findByOpenid(openid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userUpdate.getNickname() != null) user.setNickname(userUpdate.getNickname());
        if (userUpdate.getAvatarUrl() != null) user.setAvatarUrl(userUpdate.getAvatarUrl());
        if (userUpdate.getDistrictCode() != null) user.setDistrictCode(userUpdate.getDistrictCode());

        return ApiResponse.ok(userRepository.save(user));
    }

    @GetMapping("/favorites")
    public ApiResponse<List<UserFavoritePlace>> listFavorites() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String openid = auth.getName();
        User user = userRepository.findByOpenid(openid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ApiResponse.ok(favoritePlaceRepository.findByUserId(user.getId()));
    }

    @PostMapping("/favorites")
    public ApiResponse<UserFavoritePlace> addFavorite(@RequestBody UserFavoritePlace place) {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String openid = auth.getName();
        User user = userRepository.findByOpenid(openid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        place.setUser(user);
        return ApiResponse.ok(favoritePlaceRepository.save(place));
    }

    @DeleteMapping("/favorites/{id}")
    public ApiResponse<Void> deleteFavorite(@PathVariable Long id) {
        favoritePlaceRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/feedback")
    public ApiResponse<Feedback> submitFeedback(@RequestBody Feedback feedback) {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("未登录，请先登录");
        }
        String openid = auth.getName();
        User user = userRepository.findByOpenid(openid)
                .orElseThrow(() -> new RuntimeException("User not found"));
        feedback.setUser(user);
        feedback.setStatus("pending");
        if (feedback.getFeedbackType() == null || feedback.getFeedbackType().isBlank()) {
            feedback.setFeedbackType("suggestion");
        }
        return ApiResponse.ok(feedbackRepository.save(feedback));
    }
}
