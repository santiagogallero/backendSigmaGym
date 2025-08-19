package com.sigma.gym.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigma.gym.entity.BookingEntity;
import com.sigma.gym.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long classSessionId;
    private String className;
    private String classType;
    private String trainerName;
    private BookingStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startsAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endsAt;
    
    private Long rescheduledFromBookingId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime rescheduledAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String notes;

    public static BookingDTO fromEntity(BookingEntity booking) {
        if (booking == null) {
            return null;
        }

        BookingDTOBuilder builder = BookingDTO.builder()
            .id(booking.getId())
            .userId(booking.getUserId())
            .classSessionId(booking.getClassSessionId())
            .status(booking.getStatus())
            .rescheduledFromBookingId(booking.getRescheduledFromBookingId())
            .cancelledAt(booking.getCancelledAt())
            .rescheduledAt(booking.getRescheduledAt())
            .createdAt(booking.getCreatedAt())
            .notes(booking.getNotes());

        // Add class session details if available
        if (booking.getClassSession() != null) {
            builder
                .className(booking.getClassSession().getClassName())
                .classType(booking.getClassSession().getClassType())
                .startsAt(booking.getClassSession().getStartsAt())
                .endsAt(booking.getClassSession().getEndsAt());
            
            // Add trainer name if available
            if (booking.getClassSession().getTrainer() != null) {
                builder.trainerName(
                    booking.getClassSession().getTrainer().getFirstName() + " " +
                    booking.getClassSession().getTrainer().getLastName()
                );
            }
        }

        return builder.build();
    }
}
