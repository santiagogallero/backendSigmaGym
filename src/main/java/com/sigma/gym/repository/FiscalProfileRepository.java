package com.sigma.gym.repository;

import com.sigma.gym.entity.FiscalProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FiscalProfileRepository extends JpaRepository<FiscalProfileEntity, Long> {

    /**
     * Find fiscal profile by user ID
     */
    Optional<FiscalProfileEntity> findByUserId(Long userId);

    /**
     * Check if fiscal profile exists for user
     */
    boolean existsByUserId(Long userId);

    /**
     * Find by tax ID for validation
     */
    Optional<FiscalProfileEntity> findByTaxId(String taxId);

    /**
     * Find by document number for validation
     */
    @Query("SELECT f FROM FiscalProfileEntity f WHERE f.documentType = :documentType AND f.documentNumber = :documentNumber")
    Optional<FiscalProfileEntity> findByDocumentTypeAndNumber(@Param("documentType") String documentType, 
                                                             @Param("documentNumber") String documentNumber);
}
