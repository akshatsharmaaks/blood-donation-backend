package com.blooddonation.controller;

import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.enums.RequestStatus;
import com.blooddonation.model.User;
import com.blooddonation.repository.*;
import com.blooddonation.service.ReceiverRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.blooddonation.service.DonorRequestService;


import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DonorRequestService donorRequestService;
    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final ReceiverRequestRepository requestRepository;
    private final HospitalRepository hospitalRepository;
    private final ReceiverRequestService receiverRequestService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        Map<String, Object> stats = Map.of(
                "totalUsers",        userRepository.count(),
                "totalDonors",       donorProfileRepository.count(),
                "totalHospitals",    hospitalRepository.count(),
                "pendingRequests",   requestRepository.countByStatus(RequestStatus.PENDING),
                "fulfilledRequests", requestRepository.countByStatus(RequestStatus.FULFILLED),
                "cancelledRequests", requestRepository.countByStatus(RequestStatus.CANCELLED)
        );
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.success("All users", userRepository.findAll()));
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.blooddonation.exception
                        .ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User deactivated", null));
    }

    @PutMapping("/hospitals/{id}/verify")
    public ResponseEntity<ApiResponse<Void>> verifyHospital(@PathVariable Long id) {
        hospitalRepository.findById(id).ifPresentOrElse(
                h -> { h.setIsVerified(true); hospitalRepository.save(h); },
                () -> { throw new com.blooddonation.exception
                        .ResourceNotFoundException("Hospital not found"); }
        );
        return ResponseEntity.ok(ApiResponse.success("Hospital verified", null));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<?>> getAllRequests(
            @RequestParam(required = false) RequestStatus status) {
        var requests = status != null
                ? requestRepository.findByStatusOrderByCreatedAtDesc(status)
                : requestRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Requests", requests));
    }
    @PutMapping("/offers/{offerId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeOffer(@PathVariable Long offerId) {
        return ResponseEntity.ok(donorRequestService.completeOffer(offerId));
    }
}