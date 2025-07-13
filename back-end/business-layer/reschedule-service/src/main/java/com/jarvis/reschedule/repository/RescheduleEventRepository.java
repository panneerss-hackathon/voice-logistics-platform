package com.jarvis.reschedule.repository;

import com.jarvis.reschedule.entity.RescheduleEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RescheduleEventRepository extends JpaRepository<RescheduleEvent, Long> {
  List<RescheduleEvent> findByTrackingNumberOrderByEventTimeAsc(String trackingNumber);
}
