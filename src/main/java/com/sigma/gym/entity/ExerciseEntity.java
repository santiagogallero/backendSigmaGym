package com.sigma.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercises")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 50)
    private String category;

    @Column(length = 100)
    private String equipment;

    @Column(name = "duration_minutes")
    private Integer duration;

    @Column(name = "suggested_sets")
    private Integer sets;

    @Column(name = "suggested_reps")
    private Integer reps;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    // ✅ Relación con clave foránea clara
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
    @JsonIgnore // ✅ Evitar serialización circular
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ✅ Métodos helper que respetan SRP
    public Long getCreatedByUserId() {
        return createdBy != null ? createdBy.getId() : null;
    }


}
