package com.blooddonation.controller;

import com.blooddonation.dto.request.BloodRequestCreate;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DonorResponse;
import com.blooddonation.service.ReceiverRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.blooddonation.service.DonorRequestService;


import java.util.List;

@RestController
@RequestMapping("/api/receiver")
@RequiredArgsConstructor
public class ReceiverController {

    private final DonorRequestService donorRequestService;
    private final ReceiverRequestService receiverRequestService;

    @PostMapping("/request")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<?>> createRequest(
            @Valid @RequestBody BloodRequestCreate dto) {
        return ResponseEntity.ok(receiverRequestService.createRequest(dto));
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<?>> getMyRequests() {
        return ResponseEntity.ok(receiverRequestService.getMyRequests());
    }

    @GetMapping("/requests/{id}")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<?>> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(receiverRequestService.getRequestById(id));
    }

    @PutMapping("/requests/{id}/cancel")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<Void>> cancelRequest(@PathVariable Long id) {
        return ResponseEntity.ok(receiverRequestService.cancelRequest(id));
    }

    @GetMapping("/requests/{id}/matched-donors")
    @PreAuthorize("hasAnyRole('RECEIVER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<DonorResponse>>> getMatchedDonors(
            @PathVariable Long id) {
        return ResponseEntity.ok(receiverRequestService.getMatchedDonors(id));
    }

    @GetMapping("/requests/{id}/offers")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<?>> getOffersForRequest(@PathVariable Long id) {
        return ResponseEntity.ok(donorRequestService.getOffersForRequest(id));
    }

    @PutMapping("/offers/{offerId}/accept")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<Void>> acceptOffer(@PathVariable Long offerId) {
        return ResponseEntity.ok(donorRequestService.acceptOffer(offerId));
    }

    @PutMapping("/offers/{offerId}/reject")
    @PreAuthorize("hasRole('RECEIVER')")
    public ResponseEntity<ApiResponse<Void>> rejectOffer(@PathVariable Long offerId) {
        return ResponseEntity.ok(donorRequestService.rejectOffer(offerId));
    }
}