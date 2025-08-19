package com.sigma.gym.repository;

import com.sigma.gym.entity.MembershipPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlanEntity, Long> {
    
    List<MembershipPlanEntity> findByActiveTrue();
    
    Optional<MembershipPlanEntity> findByNameAndActiveTrue(String name);
    
    @Query("SELECT mp FROM MembershipPlanEntity mp WHERE mp.active = true ORDER BY mp.price ASC")
    List<MembershipPlanEntity> findActiveOrderByPriceAsc();
}
