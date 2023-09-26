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
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class AccessRequestServiceImpl implements AccessRequestService {
    private static final Logger logger = LoggerFactory.getLogger(AccessRequestServiceImpl.class);


    private final AccessRequestRepository accessRequestRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    // Create a new access request
    @Override
    public Optional<AccessRequestDTO> createRequest(AccessRequestDTO accessRequestDTO, String accessToken) {
        try {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (!isTokenValid) {
                // Return an empty optional to indicate token validation failure
                return Optional.empty();
            }

            // Create a new AccessRequest object and populate it with data from the DTO
            AccessRequest accessRequest = new AccessRequest();
            accessRequest.setPmName(accessRequestDTO.getPmName());
            accessRequest.setRequestDescription(accessRequestDTO.getRequestDescription());
            accessRequest.setUser(mapUserDTOToUser(accessRequestDTO.getUser()));
            accessRequest.setProject(mapProjectDTOToProject(accessRequestDTO.getProject()));

            // Save the new AccessRequest to the repository
            AccessRequest savedRequest = accessRequestRepository.save(accessRequest);

            // Map the saved AccessRequest back to a DTO and return it
            AccessRequestDTO createdRequestDTO = modelMapper.map(savedRequest, AccessRequestDTO.class);
            return Optional.of(createdRequestDTO);
        } catch (Exception e) {
            // Log the exception for debugging purposes
            logger.error("An error occurred while creating an access request: " + e.getMessage(), e);

            // You can throw a custom exception here or handle the error differently based on your requirements.
            // For simplicity, this example returns an empty optional.
            return Optional.empty();
        }
    }



    @Override
    public List<AccessRequestDTO> getAllRequests() {
        try {
            // Retrieve all access requests from the repository
            List<AccessRequest> accessRequestList = accessRequestRepository.findAll();

            // If there are no requests, return an empty list
            if (accessRequestList.isEmpty()) {
                return Collections.emptyList();
            }

            // Create a list to store DTOs and map each AccessRequest to a DTO
            List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();
            for (AccessRequest accessRequest : accessRequestList) {
                AccessRequestDTO accessRequestDTO = modelMapper.map(accessRequest, AccessRequestDTO.class);
                accessRequestDTOList.add(accessRequestDTO);
            }

            // Return the list of DTOs
            return accessRequestDTOList;
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while retrieving all access requests: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AccessRequestDTO> getAllActiveRequests() {
        try {
            // Retrieve all active access requests from the repository
            List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();

            // If there are no active requests, return an empty list
            if (accessRequestList.isEmpty()) {
                return Collections.emptyList();
            }

            // Create a list to store DTOs and map each active AccessRequest to a DTO
            List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();
            for (AccessRequest accessRequest : accessRequestList) {
                AccessRequestDTO accessRequestDTO = modelMapper.map(accessRequest, AccessRequestDTO.class);
                accessRequestDTOList.add(accessRequestDTO);
            }

            // Return the list of DTOs only for active requests
            return accessRequestDTOList;
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while retrieving all active access requests: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Update an access request based on the request ID and request DTO
    @Override
    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO) {
        try {
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

                // Retrieve all active access requests and map them to DTOs
                List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();
                List<AccessResponseDTO> accessResponseDTOList = accessRequests.stream()
                        .map(accessRequest -> new AccessResponseDTO(accessRequest.getAccessRequestId(), accessRequest.getPmName(), mapUserToUserDTO(accessRequest.getUser()), mapProjectToProjectDTO(accessRequest.getProject()), accessRequest.getRequestDescription(), accessRequest.isAllowed()))
                        .collect(Collectors.toList());

                // Add the updated DTO to the list
                accessResponseDTOList.add(accessResponseDTO);

                // Return the list of updated DTOs
                return accessResponseDTOList;
            } else {
                // Handle the case where the requested access request does not exist
                logger.error("Access request with ID {} does not exist.", id);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while updating access request: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Get unread access requests for a project manager

    @Override
    public List<AccessResponseDTO> getPMUnreadRequests(String pmName) {
        try {
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
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while retrieving PM's unread requests: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<AccessResponseDTO> getPMRequests(String pmName) {
        try {
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
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while retrieving PM's requests: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    // Set the notification flag for a PM request to true
    @Override
    public void setPMRequestsNotificationTrue(Long accessRequestId) {
        try {
            // Retrieve the access request by ID
            Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(accessRequestId);
            if (optionalAccessRequest.isPresent()) {
                AccessRequest accessRequest = optionalAccessRequest.get();

                // Set the PM notification flag to true
                accessRequest.setPmNotified(true);

                // Save the updated access request
                accessRequestRepository.save(accessRequest);
            } else {
                // Handle the case where the requested access request does not exist
                logger.error("Access request with ID {} does not exist.", accessRequestId);
            }
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while setting PM notification flag: " + e.getMessage(), e);
        }
    }

    @Override
    public void clearAllNotifications() {
        try {
            // Delete all access requests
            accessRequestRepository.deleteAll();
        } catch (Exception e) {
            // Log and handle any exceptions
            logger.error("An error occurred while clearing all notifications: " + e.getMessage(), e);
        }
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