package com.blooddonation.controller;

import com.blooddonation.dto.request.DonorProfileRequest;
import com.blooddonation.dto.request.DonorRequestCreate;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DonorResponse;
import com.blooddonation.dto.response.DonorRequestResponse;
import com.blooddonation.enums.BloodGroup;
import com.blooddonation.service.DonationHistoryService;
import com.blooddonation.service.DonorRequestService;
import com.blooddonation.service.DonorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donor")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;
    private final DonationHistoryService donationHistoryService;
    private final DonorRequestService donorRequestService;

    // ── Profile ───────────────────────────────────────────────────────────

    @PostMapping("/profile")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<DonorResponse>> createProfile(
            @Valid @RequestBody DonorProfileRequest request) {
        return ResponseEntity.ok(donorService.createProfile(request));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<DonorResponse>> updateProfile(
            @Valid @RequestBody DonorProfileRequest request) {
        return ResponseEntity.ok(donorService.updateProfile(request));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<DonorResponse>> getMyProfile() {
        return ResponseEntity.ok(donorService.getMyProfile());
    }

    // ── Search ────────────────────────────────────────────────────────────

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DonorResponse>>> searchDonors(
            @RequestParam BloodGroup bloodGroup,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        return ResponseEntity.ok(donorService.searchDonors(bloodGroup, city, lat, lon));
    }

    // ── Eligibility ───────────────────────────────────────────────────────

    @GetMapping("/eligibility")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<Long>> getEligibility() {
        return ResponseEntity.ok(donorService.getDaysUntilEligible());
    }

    // ── Donation history ──────────────────────────────────────────────────

    @PostMapping("/donation-history")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<?>> recordDonation(
            @RequestBody DonationHistoryService.DonationRecordRequest request) {
        return ResponseEntity.ok(donationHistoryService.recordDonation(request));
    }

    @GetMapping("/donation-history")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<?>> getHistory() {
        return ResponseEntity.ok(donationHistoryService.getMyHistory());
    }

    // ── Donor offers ──────────────────────────────────────────────────────

    @PostMapping("/offer")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<?>> createOffer(
            @Valid @RequestBody DonorRequestCreate dto) {
        return ResponseEntity.ok(donorRequestService.createOffer(dto));
    }

    @GetMapping("/offers")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<?>> getMyOffers() {
        return ResponseEntity.ok(donorRequestService.getMyOffers());
    }

    @PutMapping("/offers/{offerId}/withdraw")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<Void>> withdrawOffer(
            @PathVariable Long offerId) {
        return ResponseEntity.ok(donorRequestService.withdrawOffer(offerId));
    }

    @GetMapping("/offers/open")
    public ResponseEntity<ApiResponse<?>> getOpenOffers() {
        return ResponseEntity.ok(donorRequestService.getOpenOffers());
    }
}