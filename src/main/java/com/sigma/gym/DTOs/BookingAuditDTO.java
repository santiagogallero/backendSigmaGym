package com.sigma.gym.DTOs;

import com.sigma.gym.entity.BookingAuditEntity;
import com.sigma.gym.entity.BookingAuditEntity.BookingAction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAuditDTO {
    private Long id;
    private Long bookingId;
    private BookingAction action;
    private Long userId;
    private String userName;
    private Long actorId;
    private String actorName;
    private String reason;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime performedAt;
    
    private String metadata;

    public static BookingAuditDTO fromEntity(BookingAuditEntity audit) {
        if (audit == null) {
            return null;
        }

        BookingAuditDTOBuilder builder = BookingAuditDTO.builder()
            .id(audit.getId())
            .bookingId(audit.getBookingId())
            .action(audit.getAction())
            .userId(audit.getUserId())
            .actorId(audit.getActorUserId())
            .reason(audit.getReason())
            .performedAt(audit.getCreatedAt())
            .metadata(audit.getMetadataJson());

        // Add user name if available
        if (audit.getUser() != null) {
            builder.userName(
                audit.getUser().getFirstName() + " " + audit.getUser().getLastName()
            );
        }

        // Add actor name if available (may be different from user in admin cases)
        if (audit.getActorUser() != null) {
            builder.actorName(
                audit.getActorUser().getFirstName() + " " + audit.getActorUser().getLastName()
            );
        }

        return builder.build();
    }
}
