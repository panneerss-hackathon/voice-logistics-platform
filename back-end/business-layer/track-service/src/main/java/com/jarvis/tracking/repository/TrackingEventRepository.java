package com.jarvis.tracking.repository;

import com.jarvis.tracking.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
    List<TrackingEvent> findByTrackingNumberOrderByEventTimeAsc(String trackingNumber);
    TrackingEvent findTopByTrackingNumberOrderByEventTimeDesc(String trackingNumber);
}