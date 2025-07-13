package com.jarvis.tracking.service;

import com.jarvis.tracking.entity.TrackingEvent;
import com.jarvis.tracking.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingEventRepository repository;

    public TrackingEvent getLatestEvent(String trackingNumber) {
        return repository.findTopByTrackingNumberOrderByEventTimeDesc(trackingNumber);
    }

    public List<TrackingEvent> getHistory(String trackingNumber) {
        return repository.findByTrackingNumberOrderByEventTimeAsc(trackingNumber);
    }
}