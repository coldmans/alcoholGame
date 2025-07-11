package com.example.alcoholGameBackend.controller;

import com.example.alcoholGameBackend.dto.DrawResultResponse;
import com.example.alcoholGameBackend.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drawResults")
@RequiredArgsConstructor
public class DrawResultController {

    private final PenaltyService penaltyService;

    @GetMapping("/{drawResultId}")
    public ResponseEntity<DrawResultResponse> getDrawResult(@PathVariable UUID drawResultId) {
        DrawResultResponse response = penaltyService.getDrawResult(drawResultId);
        return ResponseEntity.ok(response);
    }
}