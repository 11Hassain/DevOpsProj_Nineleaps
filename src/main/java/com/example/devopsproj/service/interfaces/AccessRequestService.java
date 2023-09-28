package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface AccessRequestService {

    // Create a new access request
    Optional<AccessRequestDTO> createRequest(AccessRequestDTO accessRequestDTO);

    List<AccessRequestDTO> getAllRequests();

    List<AccessRequestDTO> getAllActiveRequests();

    List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO);

    List<AccessResponseDTO> getPMUnreadRequests(String pmName);

    List<AccessResponseDTO> getPMRequests(String pmName);

    ResponseEntity<String> setPMRequestsNotificationTrue(Long accessRequestId);

    void clearAllNotifications();
}
