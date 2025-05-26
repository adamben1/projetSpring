package com.example.projetSpring.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservationDto {
    private Long id;
    private Long eventId;
    private Long utilisateurId;
    private String ticketType;
    private Integer quantity;
    private Double pricePerTicket;
    private Double priceTotal;
    private LocalDateTime reservationDate;
    private String statut;
}
