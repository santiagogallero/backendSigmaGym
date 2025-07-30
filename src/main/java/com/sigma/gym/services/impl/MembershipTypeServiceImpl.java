package com.sigma.gym.services.impl;

import com.sigma.gym.entity.MembershipTypeEntity;
import com.sigma.gym.repository.MembershipTypeRepository;
import com.sigma.gym.services.MembershipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipTypeServiceImpl implements MembershipTypeService {

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Override
    public MembershipTypeEntity create(MembershipTypeEntity membershipType) {
        return membershipTypeRepository.save(membershipType);
    }

    @Override
    public List<MembershipTypeEntity> getAll() {
        return membershipTypeRepository.findAll();
    }

    @Override
    public Optional<MembershipTypeEntity> getById(Long id) {
        return membershipTypeRepository.findById(id);
    }

    @Override
    public Optional<MembershipTypeEntity> getByName(String name) {
        return membershipTypeRepository.findByName(name);
    }

    @Override
    public void deleteById(Long id) {
        membershipTypeRepository.deleteById(id);
    }
}
