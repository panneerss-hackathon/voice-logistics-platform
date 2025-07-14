package com.jarvis.tracking.repository;

import com.jarvis.tracking.entity.TrackingEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
  List<TrackingEvent> findByTrackingNumberOrderByEventTimeAsc(String trackingNumber);

  TrackingEvent findTopByTrackingNumberOrderByEventTimeDesc(String trackingNumber);
}
