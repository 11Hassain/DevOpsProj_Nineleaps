package com.example.devopsproj.service.implementations;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.AccessRequest;
import org.springframework.data.domain.Pageable;

import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.AccessRequestRepository;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class AccessRequestServiceImpl implements AccessRequestService {
    private static final Logger logger = LoggerFactory.getLogger(AccessRequestServiceImpl.class);

    private final AccessRequestRepository accessRequestRepository;

    @Override
    public AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO) {
        logger.info("Creating a new access request");
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setAccessRequestId(accessRequestDTO.getAccessRequestId());
        accessRequest.setPmName(accessRequestDTO.getPmName());

        // Save the AccessRequest directly
        AccessRequest savedAccessRequest = accessRequestRepository.save(accessRequest);

        logger.info("Access request created with ID: {}", savedAccessRequest.getAccessRequestId());

        // Create an AccessRequestDTO from the saved entity
        AccessRequestDTO createdRequestDTO = new AccessRequestDTO();
        createdRequestDTO.setAccessRequestId(savedAccessRequest.getAccessRequestId());
        createdRequestDTO.setPmName(savedAccessRequest.getPmName());

        logger.info("AccessRequestDTO created for request ID: {}", createdRequestDTO.getAccessRequestId());

        return createdRequestDTO;
    }

    @Override
    public List<AccessRequestDTO> getAllRequests() {
        logger.info("Fetching all access requests");

        List<AccessRequest> accessRequestList = accessRequestRepository.findAll();
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());

            accessRequestDTOList.add(accessRequestDTO);
        }

        logger.info("Fetched {} access requests", accessRequestDTOList.size());
        return accessRequestDTOList;
    }



    @Override
    public Page<AccessRequestDTO> getAllActiveRequests(Pageable pageable) {
        logger.info("Fetching all active access requests");

        Page<AccessRequest> accessRequestPage = accessRequestRepository.findAllActiveRequests(pageable);


        Page<AccessRequestDTO> accessRequestDTOPage = accessRequestPage.map(accessRequest -> {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());
            return accessRequestDTO;
        });

        logger.info("Fetched {} active access requests", accessRequestDTOPage.getTotalElements());
        return accessRequestDTOPage;
    }


   @Override
    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO, Pageable pageable) {
        logger.info("Fetching updated access requests");

        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);

        if (optionalAccessRequest.isPresent()) {
            AccessRequest existingAccessRequest = optionalAccessRequest.get();
            updateAccessRequest(existingAccessRequest, accessRequestDTO);
        }

        Page<AccessRequest> accessRequestPage = accessRequestRepository.findAllActiveRequests(pageable);
        List<AccessRequest> accessRequests = accessRequestPage.getContent();

        logger.info("Fetched {} updated active access requests", accessRequests.size());

        return mapAccessRequestsToDTOs(accessRequests);
    }

    void updateAccessRequest(AccessRequest accessRequest, AccessRequestDTO accessRequestDTO) {
        logger.info("Updating access request with ID: {}", accessRequest.getAccessRequestId());

        accessRequest.setAllowed(accessRequestDTO.isAllowed());
        accessRequest.setUpdated(true);
        accessRequestRepository.save(accessRequest);

        logger.info("Access request updated successfully");
    }

    List<AccessResponseDTO> mapAccessRequestsToDTOs(List<AccessRequest> accessRequests) {
        logger.info("Mapping access requests to DTOs");
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = mapAccessRequestToDTO(accessRequest);
            accessResponseDTOList.add(accessResponseDTO);
        }

        logger.info("Mapped {} access requests to DTOs", accessRequests.size());
        return accessResponseDTOList;
    }

    AccessResponseDTO mapAccessRequestToDTO(AccessRequest accessRequest) {
        logger.info("Mapping access request to DTO: AccessRequestId={}", accessRequest.getAccessRequestId());

        AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
        accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
        accessResponseDTO.setPmName(accessRequest.getPmName());
        accessResponseDTO.setAccessDescription(accessRequest.getRequestDescription());
        accessResponseDTO.setAllowed(accessRequest.isAllowed());

        ProjectDTO projectDTO = mapProjectToDTO(accessRequest.getProject());
        UserDTO userDTO = mapUserToDTO(accessRequest.getUser());

        accessResponseDTO.setProject(projectDTO);
        accessResponseDTO.setUser(userDTO);

        logger.info("Mapped access request to DTO: AccessRequestId={}", accessRequest.getAccessRequestId());
        return accessResponseDTO;
    }

    ProjectDTO mapProjectToDTO(Project project) {
        logger.info("Mapping project to DTO: ProjectId={}", project.getProjectId());

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());

        logger.info("Mapped project to DTO: ProjectId={}", project.getProjectId());
        return projectDTO;
    }

    UserDTO mapUserToDTO(User user) {
        logger.info("Mapping user to DTO: UserId={}", user.getId());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());

        logger.info("Mapped user to DTO: UserId={}", user.getId());
        return userDTO;
    }



    @Override
    public List<AccessResponseDTO> getPMUnreadRequests(String pmName) {
        logger.info("Fetching unread PM requests for PM: {}", pmName);

        List<AccessRequest> accessRequests = accessRequestRepository.findAllUnreadPMRequestsByName(pmName);

        logger.info("Fetched {} unread PM requests for PM: {}", accessRequests.size(), pmName);

        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    @Override
    public List<AccessResponseDTO> getPMRequests(String pmName) {
        logger.info("Fetching PM requests for PM: {}", pmName);

        List<AccessRequest> accessRequests = accessRequestRepository.findAllPMRequestsByName(pmName);

        logger.info("Fetched {} PM requests for PM: {}", accessRequests.size(), pmName);

        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    @Override
    public List<AccessResponseDTO> mapAccessRequestsToResponseDTOs(List<AccessRequest> accessRequests) {
        logger.info("Mapping access requests to response DTOs");

        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
            accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessResponseDTO.setPmName(accessRequest.getPmName());
            String listOfPMRequests;
            if (accessRequest.isAllowed()) {
                listOfPMRequests = "Request for adding " + accessRequest.getUser().getName() + " has been granted";
            } else {
                listOfPMRequests = "Request for adding " + accessRequest.getUser().getName() + " has been denied";
            }
            accessResponseDTO.setResponse(listOfPMRequests);
            accessResponseDTO.setNotified(accessRequest.isPmNotified());
            accessResponseDTOList.add(accessResponseDTO);
        }

        logger.info("Mapped {} access requests to response DTOs", accessRequests.size());

        return accessResponseDTOList;
    }

    @Override
    public void setPMRequestsNotificationTrue(Long accessRequestId) {
        logger.info("Setting PM requests notification to true for AccessRequestId: {}", accessRequestId);

        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(accessRequestId);

        if (optionalAccessRequest.isPresent()) {
            AccessRequest accessRequest = optionalAccessRequest.get();
            accessRequest.setPmNotified(true);
            accessRequestRepository.save(accessRequest);

            logger.info("PM requests notification set to true for AccessRequestId: {}", accessRequestId);
        } else {
            logger.warn("AccessRequest with AccessRequestId {} not found", accessRequestId);
        }
    }
    @Override
    @Transactional
    public void clearAllNotifications() {
        logger.info("Clearing all notifications");

        List<AccessRequest> accessRequests = accessRequestRepository.findAll();
        for (AccessRequest accessRequest : accessRequests) {
            accessRequest.setDeleted(true);
        }
        accessRequestRepository.saveAll(accessRequests);

        logger.info("Cleared all notifications");
    }
}