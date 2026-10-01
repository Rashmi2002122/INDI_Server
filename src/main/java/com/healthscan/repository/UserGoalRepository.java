package com.healthscan.repository;

import com.healthscan.entity.UserGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoalEntity, Long> {
    // Assuming a single user, we can fetch the first record
    UserGoalEntity findTopByOrderByIdDesc();
}
