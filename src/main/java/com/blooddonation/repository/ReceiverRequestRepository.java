package com.blooddonation.repository;

import com.blooddonation.enums.RequestStatus;
import com.blooddonation.model.ReceiverRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiverRequestRepository extends JpaRepository<ReceiverRequest, Long> {
    List<ReceiverRequest> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<ReceiverRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);
    List<ReceiverRequest> findByCityIgnoreCaseAndStatus(String city, RequestStatus status);
    long countByStatus(RequestStatus status);
}