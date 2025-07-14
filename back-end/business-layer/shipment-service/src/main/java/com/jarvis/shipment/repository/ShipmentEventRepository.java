package com.jarvis.shipment.repository;

import com.jarvis.shipment.entity.ShipmentEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, Long> {
  List<ShipmentEvent> findByTrackingNumberOrderByEventTimeAsc(String trackingNumber);

  ShipmentEvent findTopByTrackingNumberOrderByEventTimeDesc(String trackingNumber);
}
