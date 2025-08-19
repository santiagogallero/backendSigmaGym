package com.sigma.gym.repository;

import com.sigma.gym.entity.FreezeRulesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreezeRulesRepository extends JpaRepository<FreezeRulesEntity, Long> {
}
