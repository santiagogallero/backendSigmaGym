package com.sigma.gym.services;

import com.sigma.gym.entity.MembershipTypeEntity;

import java.util.List;
import java.util.Optional;

public interface MembershipTypeService {
    MembershipTypeEntity create(MembershipTypeEntity membershipType);
    List<MembershipTypeEntity> getAll();
    Optional<MembershipTypeEntity> getById(Long id);
    Optional<MembershipTypeEntity> getByName(String name);
    void deleteById(Long id);
}
