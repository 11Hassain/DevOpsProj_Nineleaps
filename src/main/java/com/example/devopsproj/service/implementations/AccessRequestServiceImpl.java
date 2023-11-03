package com.example.devopsproj.service.implementations;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.AccessRequest;

import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.AccessRequestRepository;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setAccessRequestId(accessRequestDTO.getAccessRequestId());
        accessRequest.setPmName(accessRequestDTO.getPmName());

        // Save the AccessRequest directly
        AccessRequest savedAccessRequest = accessRequestRepository.save(accessRequest);

        // Create an AccessRequestDTO from the saved entity
        AccessRequestDTO createdRequestDTO = new AccessRequestDTO();
        createdRequestDTO.setAccessRequestId(savedAccessRequest.getAccessRequestId());
        createdRequestDTO.setPmName(savedAccessRequest.getPmName());

        return createdRequestDTO;
    }

    @Override
    public List<AccessRequestDTO> getAllRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAll();
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());

            accessRequestDTOList.add(accessRequestDTO);
        }
        return accessRequestDTOList;
    }

    @Override
    public List<AccessRequestDTO> getAllActiveRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());

            accessRequestDTOList.add(accessRequestDTO);
        }
        return accessRequestDTOList;
    }
    @Override
    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO) {
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest existingAccessRequest = optionalAccessRequest.get();
            updateAccessRequest(existingAccessRequest, accessRequestDTO);
        }

        List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();
        return mapAccessRequestsToDTOs(accessRequests);
    }

    private void updateAccessRequest(AccessRequest accessRequest, AccessRequestDTO accessRequestDTO) {
        accessRequest.setAllowed(accessRequestDTO.isAllowed());
        accessRequest.setUpdated(true);
        accessRequestRepository.save(accessRequest);
    }

    private List<AccessResponseDTO> mapAccessRequestsToDTOs(List<AccessRequest> accessRequests) {
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = mapAccessRequestToDTO(accessRequest);
            accessResponseDTOList.add(accessResponseDTO);
        }

        return accessResponseDTOList;
    }

    private AccessResponseDTO mapAccessRequestToDTO(AccessRequest accessRequest) {
        AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
        accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
        accessResponseDTO.setPmName(accessRequest.getPmName());
        accessResponseDTO.setAccessDescription(accessRequest.getRequestDescription());
        accessResponseDTO.setAllowed(accessRequest.isAllowed());

        ProjectDTO projectDTO = mapProjectToDTO(accessRequest.getProject());
        UserDTO userDTO = mapUserToDTO(accessRequest.getUser());

        accessResponseDTO.setProject(projectDTO);
        accessResponseDTO.setUser(userDTO);

        return accessResponseDTO;
    }

    private ProjectDTO mapProjectToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }



    @Override
    public List<AccessResponseDTO> getPMUnreadRequests(String pmName) {
        List<AccessRequest> accessRequests = accessRequestRepository.findAllUnreadPMRequestsByName(pmName);
        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    @Override
    public List<AccessResponseDTO> getPMRequests(String pmName) {
        List<AccessRequest> accessRequests = accessRequestRepository.findAllPMRequestsByName(pmName);
        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    @Override
    public List<AccessResponseDTO> mapAccessRequestsToResponseDTOs(List<AccessRequest> accessRequests) {
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
        return accessResponseDTOList;
    }

    @Override
    public void setPMRequestsNotificationTrue(Long accessRequestId) {
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(accessRequestId);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest accessRequest = optionalAccessRequest.get();
            accessRequest.setPmNotified(true);
            accessRequestRepository.save(accessRequest);
        }
    }

    @Override
    @Transactional
    public void clearAllNotifications() {
        List<AccessRequest> accessRequests = accessRequestRepository.findAll();
        for (AccessRequest accessRequest : accessRequests) {
            accessRequest.setDeleted(true);
        }
        accessRequestRepository.saveAll(accessRequests);
    }
}