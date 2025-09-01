package com.workconnect.api.repository;

import com.workconnect.api.entity.Badge;
import com.workconnect.api.entity.User;
import com.workconnect.api.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    boolean existsByUserAndBadge(User user, Badge badge);

    List<UserBadge> findByUser(User user);
}
