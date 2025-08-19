package com.sigma.gym.repository;

import com.sigma.gym.entity.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {

    /**
     * Find invoice by payment ID
     */
    Optional<InvoiceEntity> findByPaymentId(Long paymentId);

    /**
     * Check if invoice exists for payment
     */
    boolean existsByPaymentId(Long paymentId);

    /**
     * Find invoices by user ID with pagination
     */
    Page<InvoiceEntity> findByUserIdOrderByIssueDateDesc(Long userId, Pageable pageable);

    /**
     * Find invoices by user ID and status
     */
    List<InvoiceEntity> findByUserIdAndStatus(Long userId, InvoiceEntity.InvoiceStatus status);

    /**
     * Find invoice by invoice number
     */
    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);

    /**
     * Find invoices by date range
     */
    @Query("SELECT i FROM InvoiceEntity i WHERE i.issueDate >= :startDate AND i.issueDate <= :endDate ORDER BY i.issueDate DESC")
    List<InvoiceEntity> findByIssueDateBetween(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * Count invoices by status
     */
    long countByStatus(InvoiceEntity.InvoiceStatus status);

    /**
     * Find invoices by user ID and date range
     */
    @Query("SELECT i FROM InvoiceEntity i WHERE i.userId = :userId AND i.issueDate >= :startDate AND i.issueDate <= :endDate ORDER BY i.issueDate DESC")
    List<InvoiceEntity> findByUserIdAndIssueDateBetween(@Param("userId") Long userId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
}
