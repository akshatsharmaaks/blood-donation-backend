package com.blooddonation.controller;

import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<?>> register(
            @RequestBody HospitalService.HospitalRequest dto) {
        return ResponseEntity.ok(hospitalService.registerHospital(dto));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<?>> getProfile() {
        return ResponseEntity.ok(hospitalService.getMyHospital());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllVerified() {
        return ResponseEntity.ok(hospitalService.getAllVerifiedHospitals());
    }

    @GetMapping("/receiver-requests")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<?>> getPendingReceiverRequests() {
        return ResponseEntity.ok(hospitalService.getPendingRequests());
    }

    @PutMapping("/receiver-requests/{id}/fulfill")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<Void>> fulfillRequest(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.fulfillReceiverRequest(id));
    }
}