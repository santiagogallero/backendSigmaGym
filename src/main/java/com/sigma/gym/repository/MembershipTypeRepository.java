package com.sigma.gym.repository;

import com.sigma.gym.entity.MembershipTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipTypeRepository extends JpaRepository<MembershipTypeEntity, Long> {
    Optional<MembershipTypeEntity> findByName(String name);
    boolean existsByName(String name);
}
