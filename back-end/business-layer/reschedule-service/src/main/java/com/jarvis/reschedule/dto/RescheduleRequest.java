package com.jarvis.reschedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class RescheduleRequest {
  @NotBlank private String trackingNumber;

  @NotNull private LocalDate newDeliveryDate;

  @NotNull private LocalTime newDeliveryTime;
}
