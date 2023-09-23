package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.requestDto.AccessRequestDTO;
import com.example.devopsproj.dto.responseDto.AccessResponseDTO;
import com.example.devopsproj.model.AccessRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccessRequestService {
    AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO);

    List<AccessRequestDTO> getAllRequests();

    List<AccessRequestDTO> getAllActiveRequests();

    List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO);

    List<AccessResponseDTO> getPMUnreadRequests(String pmName);

    List<AccessResponseDTO> getPMRequests(String pmName);

    List<AccessResponseDTO> mapAccessRequestsToResponseDTOs(List<AccessRequest> accessRequests);

    void setPMRequestsNotificationTrue(Long accessRequestId);

    @Transactional
    void clearAllNotifications();
}
