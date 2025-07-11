package com.example.alcoholGameBackend.controller;

import com.example.alcoholGameBackend.dto.ApiResponse;
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

    @PostMapping("/{drawResultId}/reveal")
    public ResponseEntity<ApiResponse> revealPenalty(@PathVariable UUID drawResultId) {
        penaltyService.notifyPenaltyRevealed(drawResultId);
        return ResponseEntity.ok(new ApiResponse("success"));
    }
}