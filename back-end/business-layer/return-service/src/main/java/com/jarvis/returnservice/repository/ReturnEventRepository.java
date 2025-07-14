package com.jarvis.returnservice.repository;

import com.jarvis.returnservice.entity.ReturnEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnEventRepository extends JpaRepository<ReturnEvent, Long> {
}
