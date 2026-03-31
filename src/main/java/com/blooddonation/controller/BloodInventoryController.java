package com.blooddonation.controller;

import com.blooddonation.dto.request.InventoryUpdateRequest;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.enums.BloodGroup;
import com.blooddonation.service.BloodInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital/inventory")
@RequiredArgsConstructor
public class BloodInventoryController {

    private final BloodInventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<?>> getInventory() {
        return ResponseEntity.ok(inventoryService.getMyInventory());
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('HOSPITAL')")
    public ResponseEntity<ApiResponse<?>> updateInventory(
            @Valid @RequestBody InventoryUpdateRequest dto) {
        return ResponseEntity.ok(inventoryService.updateInventory(dto));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<?>> getAvailable(
            @RequestParam BloodGroup bloodGroup) {
        return ResponseEntity.ok(inventoryService.getAvailableByBloodGroup(bloodGroup));
    }
}