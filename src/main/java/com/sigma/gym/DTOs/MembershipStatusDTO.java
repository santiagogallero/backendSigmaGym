package com.sigma.gym.DTOs;

import com.sigma.gym.entity.MembershipEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipStatusDTO {
    
    private Long id;
    private MembershipEntity.MembershipStatus status;
    private String planName;
    private String planDescription;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private String lastPaymentId;
    private String externalReference;
    private Boolean active;
    
    public static MembershipStatusDTO fromEntity(MembershipEntity membership) {
        if (membership == null) {
            return MembershipStatusDTO.builder()
                .status(MembershipEntity.MembershipStatus.NONE)
                .active(false)
                .build();
        }
        
        return MembershipStatusDTO.builder()
            .id(membership.getId())
            .status(membership.getStatus())
            .planName(membership.getPlan() != null ? membership.getPlan().getName() : null)
            .planDescription(membership.getPlan() != null ? membership.getPlan().getDescription() : null)
            .startDate(membership.getStartDate())
            .expirationDate(membership.getExpirationDate())
            .lastPaymentId(membership.getLastPaymentId())
            .externalReference(membership.getExternalReference())
            .active(membership.getStatus() == MembershipEntity.MembershipStatus.ACTIVE)
            .build();
    }
}
