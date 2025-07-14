package com.jarvis.tracking.controller;

import com.jarvis.tracking.entity.TrackingEvent;
import com.jarvis.tracking.service.TrackingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

  private final TrackingService trackingService;

  @GetMapping("/{trackingNumber}")
  public TrackingEvent getLatest(@PathVariable String trackingNumber) {
    return trackingService.getLatestEvent(trackingNumber);
  }

  @GetMapping("/{trackingNumber}/history")
  public List<TrackingEvent> getHistory(@PathVariable String trackingNumber) {
    return trackingService.getHistory(trackingNumber);
  }
}
