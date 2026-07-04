package com.wqb.springboot.repository;

import com.wqb.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOpenid(String openid);
    long countByStatus(Integer status);
    boolean existsByOpenid(String openid);
}
