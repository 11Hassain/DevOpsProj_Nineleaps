package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.requestDto.AccessRequestDTO;
import com.example.devopsproj.dto.responseDto.AccessResponseDTO;

import java.util.List;

public interface AccessRequestService {

    AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO);

    List<AccessRequestDTO> getAllRequests();

    List<AccessRequestDTO> getAllActiveRequests();

    List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO);

    List<AccessResponseDTO> getPMUnreadRequests(String pmName);

    List<AccessResponseDTO> getPMRequests(String pmName);

    void setPMRequestsNotificationTrue(Long accessRequestId);

    void clearAllNotifications();
}
