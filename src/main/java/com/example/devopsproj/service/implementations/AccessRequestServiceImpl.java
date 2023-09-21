package com.example.devopsproj.service.implementations;
import com.example.devopsproj.dto.requestDto.AccessRequestDTO;
import com.example.devopsproj.dto.responseDto.AccessResponseDTO;
import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.dto.responseDto.UserDTO;
import com.example.devopsproj.model.AccessRequest;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.AccessRequestRepository;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessRequestServiceImpl implements AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final ModelMapper modelMapper;

    // Create a new access request
    @Override
    public AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO) {
        // Create a new AccessRequest object and populate it with data from the DTO
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setPmName(accessRequestDTO.getPmName());
        accessRequest.setRequestDescription(accessRequestDTO.getRequestDescription());
        accessRequest.setUser(mapUserDTOToUser(accessRequestDTO.getUser()));
        accessRequest.setProject(mapProjectDTOToProject(accessRequestDTO.getProject()));

        // Save the new AccessRequest to the repository
        accessRequestRepository.save(accessRequest);

        // Map the saved AccessRequest back to a DTO and return it
        return modelMapper.map(accessRequest, AccessRequestDTO.class);
    }

    // Get all access requests
    @Override
    public List<AccessRequestDTO> getAllRequests() {
        // Retrieve all access requests from the repository
        List<AccessRequest> accessRequestList = accessRequestRepository.findAll();

        // If there are no requests, return an empty list
        if (accessRequestList.isEmpty()) {
            return Collections.emptyList();
        }

        // Create a list to store DTOs and map each AccessRequest to a DTO
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();
        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            // Populate the DTO with data from the AccessRequest
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());
            accessRequestDTO.setUser(mapUserToUserDTO(accessRequest.getUser()));
            accessRequestDTO.setProject(mapProjectToProjectDTO(accessRequest.getProject()));
            accessRequestDTO.setRequestDescription(accessRequest.getRequestDescription());
            accessRequestDTO.setAllowed(accessRequest.isAllowed());
            accessRequestDTOList.add(accessRequestDTO);
        }

        // Return the list of DTOs
        return accessRequestDTOList;
    }

    // Get all active access requests
    @Override
    public List<AccessRequestDTO> getAllActiveRequests() {
        // Retrieve all active access requests from the repository
        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();

        // If there are no active requests, return an empty list
        if (accessRequestList.isEmpty()) {
            return Collections.emptyList();
        }

        // Create a list to store DTOs and map each active AccessRequest to a DTO
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();
        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            // Populate the DTO with data from the active AccessRequest
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());
            accessRequestDTO.setUser(mapUserToUserDTO(accessRequest.getUser()));
            accessRequestDTO.setProject(mapProjectToProjectDTO(accessRequest.getProject()));
            accessRequestDTO.setRequestDescription(accessRequest.getRequestDescription());
            accessRequestDTO.setAllowed(accessRequest.isAllowed());
            accessRequestDTOList.add(accessRequestDTO);
        }

        // Return the list of DTOs only for active requests
        return accessRequestDTOList;
    }

    // Update an access request and return a list of updated requests
    @Override
    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO) {
        // Retrieve the existing access request by ID
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest existingAccessRequest = optionalAccessRequest.get();

            // Update the access request with data from the DTO
            existingAccessRequest.setAllowed(accessRequestDTO.isAllowed());
            existingAccessRequest.setUpdated(true);

            // Save the updated access request
            AccessRequest updatedAccessRequest = accessRequestRepository.save(existingAccessRequest);

            // Create a response DTO with updated data
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO(
                    updatedAccessRequest.getAccessRequestId(),
                    updatedAccessRequest.getPmName(),
                    mapUserToUserDTO(updatedAccessRequest.getUser()),
                    mapProjectToProjectDTO(updatedAccessRequest.getProject()),
                    updatedAccessRequest.getRequestDescription(),
                    updatedAccessRequest.isAllowed()
            );
        }

        // Retrieve all active access requests and map them to DTOs
        List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();
        List<AccessResponseDTO> accessResponseDTOList = accessRequests.stream()
                .map(accessRequest -> new AccessResponseDTO(accessRequest.getAccessRequestId(), accessRequest.getPmName(), mapUserToUserDTO(accessRequest.getUser()), mapProjectToProjectDTO(accessRequest.getProject()), accessRequest.getRequestDescription(), accessRequest.isAllowed()))
                .collect(Collectors.toList());

        // Return the list of updated DTOs
        return accessResponseDTOList;
    }

    // Get unread access requests for a project manager
    @Override
    public List<AccessResponseDTO> getPMUnreadRequests(String pmName) {
        // Retrieve all unread access requests for the given project manager
        List<AccessRequest> accessRequests = accessRequestRepository.findAllUnreadPMRequestsByName(pmName);

        // If there are no unread requests, return an empty list
        if (accessRequests.isEmpty()) {
            return Collections.emptyList();
        }

        // Create a list to store DTOs and map each unread AccessRequest to a DTO
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        String listOfPMRequests;
        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
            accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessResponseDTO.setPmName(accessRequest.getPmName());

            // Determine the response message based on whether the request was allowed or denied
            if (accessRequest.isAllowed()) {
                listOfPMRequests = "Request for adding " + accessRequest.getUser().getName() + " has been granted";
            } else {
                listOfPMRequests = "Request for adding " + accessRequest.getUser().getName() + " has been denied";
            }

            // Populate the DTO with data
            accessResponseDTO.setResponse(listOfPMRequests);
            accessResponseDTO.setNotified(accessRequest.isPmNotified());
            accessResponseDTOList.add(accessResponseDTO);
        }

        // Return the list of unread request DTOs
        return accessResponseDTOList;
    }

    // Get all access requests for a project manager
    @Override
    public List<AccessResponseDTO> getPMRequests(String pmName) {
        // Retrieve all access requests for the given project manager
        List<AccessRequest> accessRequests = accessRequestRepository.findAllPMRequestsByName(pmName);

        // If there are no requests, return an empty list
        if (accessRequests.isEmpty()) {
            return Collections.emptyList();
        }

        // Create a list to store DTOs and map each AccessRequest to a DTO
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        String listOfPMRequests;
        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
            accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessResponseDTO.setPmName(accessRequest.getPmName());

            // Determine the response message based on whether the request was allowed or denied
            if (accessRequest.isAllowed()) {
                listOfPMRequests = "Request for adding " + accessRequest.getUser().getName() + " has been granted";
            } else {
                listOfPMRequests = "Request for adding " + accessRequest.getUser().getName() + " has been denied";
            }

            // Populate the DTO with data
            accessResponseDTO.setResponse(listOfPMRequests);
            accessResponseDTO.setNotified(accessRequest.isPmNotified());
            accessResponseDTOList.add(accessResponseDTO);
        }

        // Return the list of request DTOs for the project manager
        return accessResponseDTOList;
    }

    // Set the notification flag for a PM request to true
    @Override
    public void setPMRequestsNotificationTrue(Long accessRequestId) {
        // Retrieve the access request by ID
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(accessRequestId);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest accessRequest = optionalAccessRequest.get();

            // Set the PM notification flag to true
            accessRequest.setPmNotified(true);

            // Save the updated access request
            accessRequestRepository.save(accessRequest);
        }
    }

    // Clear all notifications by deleting all access requests
    @Override
    public void clearAllNotifications() {
        // Delete all access requests
        accessRequestRepository.deleteAll();
    }

    // Helper method to map User to UserDTO
    private UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        return userDTO;
    }

    // Helper method to map Project to ProjectDTO
    private ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    // Helper method to map UserDTO to User
    private User mapUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        return user;
    }

    // Helper method to map ProjectDTO to Project
    private Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
}