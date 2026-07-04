package com.wqb.springboot.repository;

import com.wqb.springboot.entity.UserFavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserFavoritePlaceRepository extends JpaRepository<UserFavoritePlace, Long> {
    List<UserFavoritePlace> findByUserId(Long userId);
    void deleteByUserIdAndId(Long userId, Long id);
}
