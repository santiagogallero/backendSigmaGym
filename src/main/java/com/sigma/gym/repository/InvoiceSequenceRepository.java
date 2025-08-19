package com.sigma.gym.repository;

import com.sigma.gym.entity.InvoiceSequenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequenceEntity, Long> {

    /**
     * Find sequence by pos and series with pessimistic lock for thread-safe increment
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceSequenceEntity s WHERE s.pos = :pos AND s.series = :series")
    Optional<InvoiceSequenceEntity> findByPosAndSeriesForUpdate(@Param("pos") String pos, 
                                                               @Param("series") String series);

    /**
     * Find sequence by pos and series
     */
    Optional<InvoiceSequenceEntity> findByPosAndSeries(String pos, String series);

    /**
     * Increment last number atomically
     */
    @Modifying
    @Query("UPDATE InvoiceSequenceEntity s SET s.lastNumber = s.lastNumber + 1, s.updatedAt = CURRENT_TIMESTAMP WHERE s.pos = :pos AND s.series = :series")
    int incrementLastNumber(@Param("pos") String pos, @Param("series") String series);
}
