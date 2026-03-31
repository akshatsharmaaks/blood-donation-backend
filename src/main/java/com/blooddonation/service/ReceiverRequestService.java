package com.blooddonation.service;

import com.blooddonation.algorithm.BloodCompatibilityMatrix;
import com.blooddonation.algorithm.DonorRankingAlgorithm;
import com.blooddonation.dto.request.BloodRequestCreate;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DonorResponse;
import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.RequestStatus;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiverRequestService {

    private final ReceiverRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final BloodCompatibilityMatrix compatibilityMatrix;
    private final DonorRankingAlgorithm rankingAlgorithm;

    @Transactional
    public ApiResponse<ReceiverRequest> createRequest(BloodRequestCreate dto) {
        User user = getCurrentUser();

        ReceiverRequest request = ReceiverRequest.builder()
                .receiver(user)
                .bloodGroup(dto.getBloodGroup())
                .unitsRequired(dto.getUnitsRequired())
                .patientName(dto.getPatientName())
                .hospital(dto.getHospital())
                .city(dto.getCity())
                .state(dto.getState())
                .latitude(dto.getLatitude() != null ? dto.getLatitude() : 0.0)
                .longitude(dto.getLongitude() != null ? dto.getLongitude() : 0.0)
                .notes(dto.getNotes())
                .urgencyLevel(dto.getUrgencyLevel())
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(request);
        return ApiResponse.success("Blood request created successfully", request);
    }

    public ApiResponse<List<ReceiverRequest>> getMyRequests() {
        User user = getCurrentUser();
        List<ReceiverRequest> requests =
                requestRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId());
        return ApiResponse.success("Requests fetched", requests);
    }

    public ApiResponse<ReceiverRequest> getRequestById(Long id) {
        ReceiverRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        return ApiResponse.success("Request fetched", request);
    }

    @Transactional
    public ApiResponse<Void> cancelRequest(Long id) {
        User user = getCurrentUser();
        ReceiverRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!request.getReceiver().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to cancel this request");
        }
        if (request.getStatus() == RequestStatus.FULFILLED) {
            throw new IllegalArgumentException("Cannot cancel a fulfilled request");
        }

        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
        return ApiResponse.success("Request cancelled", null);
    }

    public ApiResponse<List<DonorResponse>> getMatchedDonors(Long requestId) {
        ReceiverRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        List<BloodGroup> compatible =
                compatibilityMatrix.getCompatibleDonorGroups(request.getBloodGroup());

        List<DonorProfile> donors =
                donorProfileRepository.findEligibleDonors(compatible, request.getCity());

        List<DonorResponse> ranked = rankingAlgorithm.rankDonors(
                donors, request.getBloodGroup(),
                request.getLatitude(), request.getLongitude());

        return ApiResponse.success("Matched donors: " + ranked.size(), ranked);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}