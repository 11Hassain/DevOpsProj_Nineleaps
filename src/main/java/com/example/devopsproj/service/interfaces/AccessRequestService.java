package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.model.AccessRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

/**
 * Service interface for managing access request operations, including creation,
 * retrieval, and mapping. Provides methods for handling both active and updated requests,
 * as well as managing notifications for project managers.
 */

public interface AccessRequestService {

    AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO);

    List<AccessRequestDTO> getAllRequests();

    Page<AccessRequestDTO> getAllActiveRequests(Pageable pageable);

    List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO, Pageable pageable);

    List<AccessResponseDTO> getPMUnreadRequests(String pmName);

    List<AccessResponseDTO> getPMRequests(String pmName);

    List<AccessResponseDTO> mapAccessRequestsToResponseDTOs(List<AccessRequest> accessRequests);

    void setPMRequestsNotificationTrue(Long accessRequestId);

    void clearAllNotifications();
}
