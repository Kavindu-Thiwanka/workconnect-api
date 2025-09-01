package com.workconnect.api.repository;

import com.workconnect.api.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Badge findByName(String name);

}
