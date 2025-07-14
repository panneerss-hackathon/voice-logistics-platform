package com.jarvis.returnservice.controller;

import com.jarvis.returnservice.dto.ReturnRequest;
import com.jarvis.returnservice.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
@Slf4j
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ResponseEntity<String> createReturn(@RequestBody @Valid ReturnRequest request,
                                               @RequestHeader(value = "x-correlation-id", required = false) String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        returnService.handleReturn(request, correlationId);
        return ResponseEntity.ok("Return processed with correlationId: " + correlationId);
    }
}
