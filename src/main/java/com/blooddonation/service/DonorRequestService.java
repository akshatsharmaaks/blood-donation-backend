package com.blooddonation.service;

import com.blooddonation.algorithm.DonorEligibilityChecker;
import com.blooddonation.dto.request.DonorRequestCreate;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DonorRequestResponse;
import com.blooddonation.enums.DonorRequestStatus;
import com.blooddonation.enums.RequestStatus;
import com.blooddonation.exception.ResourceNotFoundException;
import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonorRequestService {

    private final DonorRequestRepository donorRequestRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final ReceiverRequestRepository receiverRequestRepository;
    private final UserRepository userRepository;
    private final DonorEligibilityChecker eligibilityChecker;

    // ── Donor creates an offer ──────────────────────────────────────────────

    @Transactional
    public ApiResponse<DonorRequestResponse> createOffer(DonorRequestCreate dto) {
        DonorProfile donor = getCurrentDonorProfile();

        // Enforce 90-day eligibility rule
        if (!eligibilityChecker.isEligible(donor)) {
            long days = eligibilityChecker.daysUntilEligible(donor);
            throw new IllegalArgumentException(
                    "You are not eligible to donate yet. "
                            + days + " days remaining.");
        }

        ReceiverRequest receiverRequest = null;

        if (dto.getReceiverRequestId() != null) {
            // Responding to a specific receiver request
            receiverRequest = receiverRequestRepository
                    .findById(dto.getReceiverRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Receiver request not found"));

            if (receiverRequest.getStatus() != RequestStatus.PENDING) {
                throw new IllegalArgumentException(
                        "This blood request is no longer accepting offers");
            }

            // Prevent duplicate offers
            if (donorRequestRepository.existsByDonorProfileIdAndReceiverRequestId(
                    donor.getId(), dto.getReceiverRequestId())) {
                throw new IllegalArgumentException(
                        "You have already made an offer for this request");
            }
        }

        DonorRequest donorRequest = DonorRequest.builder()
                .donorProfile(donor)
                .receiverRequest(receiverRequest)
                .bloodGroup(donor.getBloodGroup())
                .unitsOffered(dto.getUnitsOffered())
                .preferredDate(dto.getPreferredDate())
                .city(dto.getCity() != null ? dto.getCity() : donor.getCity())
                .state(dto.getState() != null ? dto.getState() : donor.getState())
                .message(dto.getMessage())
                .status(DonorRequestStatus.PENDING)
                .build();

        donorRequestRepository.save(donorRequest);
        return ApiResponse.success("Donation offer submitted successfully",
                toResponse(donorRequest));
    }

    // ── Donor views their own offers ────────────────────────────────────────

    public ApiResponse<List<DonorRequestResponse>> getMyOffers() {
        DonorProfile donor = getCurrentDonorProfile();
        List<DonorRequestResponse> offers = donorRequestRepository
                .findByDonorProfileIdOrderByCreatedAtDesc(donor.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Your offers", offers);
    }

    // ── Donor withdraws their own offer ─────────────────────────────────────

    @Transactional
    public ApiResponse<Void> withdrawOffer(Long offerId) {
        DonorProfile donor = getCurrentDonorProfile();
        DonorRequest offer = donorRequestRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        if (!offer.getDonorProfile().getId().equals(donor.getId())) {
            throw new IllegalArgumentException("Not authorized to withdraw this offer");
        }
        if (offer.getStatus() == DonorRequestStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot withdraw a completed donation");
        }

        offer.setStatus(DonorRequestStatus.WITHDRAWN);
        donorRequestRepository.save(offer);
        return ApiResponse.success("Offer withdrawn", null);
    }

    // ── Receiver views all offers on their request ───────────────────────────

    public ApiResponse<List<DonorRequestResponse>> getOffersForRequest(Long receiverRequestId) {
        List<DonorRequestResponse> offers = donorRequestRepository
                .findByReceiverRequestIdOrderByCreatedAtDesc(receiverRequestId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Offers for this request", offers);
    }

    // ── Receiver accepts a donor offer ───────────────────────────────────────

    @Transactional
    public ApiResponse<Void> acceptOffer(Long offerId) {
        DonorRequest offer = donorRequestRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        offer.setStatus(DonorRequestStatus.ACCEPTED);
        donorRequestRepository.save(offer);

        // Mark the receiver request as matched
        if (offer.getReceiverRequest() != null) {
            ReceiverRequest req = offer.getReceiverRequest();
            req.setStatus(RequestStatus.MATCHED);
            req.setMatchedDonor(offer.getDonorProfile());
            receiverRequestRepository.save(req);
        }

        return ApiResponse.success("Offer accepted. Donor has been notified.", null);
    }

    // ── Receiver rejects a donor offer ──────────────────────────────────────

    @Transactional
    public ApiResponse<Void> rejectOffer(Long offerId) {
        DonorRequest offer = donorRequestRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        offer.setStatus(DonorRequestStatus.REJECTED);
        donorRequestRepository.save(offer);
        return ApiResponse.success("Offer rejected", null);
    }

    // ── Admin / Hospital marks donation as completed ─────────────────────────

    @Transactional
    public ApiResponse<Void> completeOffer(Long offerId) {
        DonorRequest offer = donorRequestRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        if (offer.getStatus() != DonorRequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Only accepted offers can be completed");
        }

        offer.setStatus(DonorRequestStatus.COMPLETED);
        donorRequestRepository.save(offer);

        // Mark receiver request as fulfilled
        if (offer.getReceiverRequest() != null) {
            ReceiverRequest req = offer.getReceiverRequest();
            req.setStatus(RequestStatus.FULFILLED);
            receiverRequestRepository.save(req);
        }

        return ApiResponse.success("Donation marked as completed", null);
    }

    // ── Open offers (not tied to any specific receiver request) ─────────────

    public ApiResponse<List<DonorRequestResponse>> getOpenOffers() {
        List<DonorRequestResponse> offers = donorRequestRepository
                .findByReceiverRequestIsNullAndStatus(DonorRequestStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Open donation offers", offers);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private DonorProfile getCurrentDonorProfile() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return donorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Donor profile not found. Please complete your profile first."));
    }

    private DonorRequestResponse toResponse(DonorRequest dr) {
        return DonorRequestResponse.builder()
                .id(dr.getId())
                .donorProfileId(dr.getDonorProfile().getId())
                .donorName(dr.getDonorProfile().getUser().getFullName())
                .donorPhone(dr.getDonorProfile().getUser().getPhone())
                .bloodGroup(dr.getBloodGroup())
                .unitsOffered(dr.getUnitsOffered())
                .status(dr.getStatus())
                .preferredDate(dr.getPreferredDate())
                .city(dr.getCity())
                .state(dr.getState())
                .message(dr.getMessage())
                .receiverRequestId(dr.getReceiverRequest() != null
                        ? dr.getReceiverRequest().getId() : null)
                .createdAt(dr.getCreatedAt())
                .build();
    }
}