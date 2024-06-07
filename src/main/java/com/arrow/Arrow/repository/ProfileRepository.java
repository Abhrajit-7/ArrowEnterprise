package com.arrow.Arrow.repository;

import com.arrow.Arrow.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findByUsername(String username);
}
