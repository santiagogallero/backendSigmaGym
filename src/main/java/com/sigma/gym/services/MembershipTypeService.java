package com.sigma.gym.services;

import com.sigma.gym.entity.MembershipType;

import java.util.List;
import java.util.Optional;

public interface MembershipTypeService {
    MembershipType create(MembershipType membershipType);
    List<MembershipType> getAll();
    Optional<MembershipType> getById(Long id);
    Optional<MembershipType> getByName(String name);
    void deleteById(Long id);
}
