package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.AccessRequest;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.repository.AccessRequestRepository;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The `AccessRequestServiceImpl` class provides services for managing access requests and responses.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class AccessRequestServiceImpl implements AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(AccessRequestServiceImpl.class);
    private static final String RESPONSE = "Request for adding ";
    private final JwtService jwtService;

    /**
     * Create Request
     *
     * @param accessRequestDTO DTO for the access requests.
     * @return AccessRequestDTO response
     */
    @Override
    public AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO) {
        logger.info("Creating AccessRequest: {}", accessRequestDTO);

        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setAccessRequestId(accessRequestDTO.getAccessRequestId());
        accessRequest.setPmName(accessRequestDTO.getPmName());

        // Save the AccessRequest directly
        AccessRequest savedAccessRequest = accessRequestRepository.save(accessRequest);
        logger.info("AccessRequest created successfully. ID: {}", savedAccessRequest.getAccessRequestId());

        // Create an AccessRequestDTO from the saved entity
        AccessRequestDTO createdRequestDTO = new AccessRequestDTO();
        createdRequestDTO.setAccessRequestId(savedAccessRequest.getAccessRequestId());
        createdRequestDTO.setPmName(savedAccessRequest.getPmName());

        logger.info("Returning created AccessRequestDTO: {}", createdRequestDTO);
        return createdRequestDTO;
    }

    /**
     * Get all requests
     *
     * @return List of access requests using DTO.
     */
    @Override
    public List<AccessRequestDTO> getAllRequests() {
        logger.info("Fetching all access request.");

        List<AccessRequest> accessRequestList = accessRequestRepository.findAll();
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());

            accessRequestDTOList.add(accessRequestDTO);
        }

        logger.info("Fetched {} access requests.", accessRequestDTOList.size());
        return accessRequestDTOList;
    }

    /**
     * Get all active requests
     *
     * @return List of active access requests.
     */
    @Override
    public List<AccessRequestDTO> getAllActiveRequests() {
        logger.info("Fetching all active access requests.");

        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());

            accessRequestDTOList.add(accessRequestDTO);
        }

        logger.info("Fetched {} active access requests.", accessRequestDTOList.size());
        return accessRequestDTOList;
    }

    /**
     * Get updated access requests
     *
     * @param id The ID of the access request.
     * @param accessRequestDTO The DTO for the access requests.
     * @return List of access request responses.
     */
    @Override
    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO) {
        logger.info("Updating access request with ID: {}", id);

        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest existingAccessRequest = optionalAccessRequest.get();
            existingAccessRequest.setAllowed(accessRequestDTO.isAllowed());
            existingAccessRequest.setUpdated(true);
            accessRequestRepository.save(existingAccessRequest);

            logger.info("Access request updated successfully.");
        }

        logger.info("Fetching all active access requests.");
        List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();

        // Create a list of AccessResponseDTO objects directly
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
            accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessResponseDTO.setPmName(accessRequest.getPmName());
            accessResponseDTO.setAccessDescription(accessRequest.getRequestDescription());
            accessResponseDTO.setAllowed(accessRequest.isAllowed());

            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(accessRequest.getProject().getProjectId());
            projectDTO.setProjectName(accessRequest.getProject().getProjectName());

            UserDTO userDTO = new UserDTO();
            userDTO.setId(accessRequest.getUser().getId());
            userDTO.setName(accessRequest.getUser().getName());
            userDTO.setEmail(accessRequest.getUser().getEmail());

            accessResponseDTO.setProject(projectDTO);
            accessResponseDTO.setUser(userDTO);

            accessResponseDTOList.add(accessResponseDTO);
        }

        logger.info("Fetched {} active access requests.", accessResponseDTOList.size());
        return accessResponseDTOList;
    }

    /**
     * Get unread requests by PM
     *
     * @param pmName The name (string) of the Project Manager.
     * @return List of access requests response using DTO.
     */
    @Override
    public List<AccessResponseDTO> getPMUnreadRequests(String pmName) {
        logger.info("Fetching unread access requests for PM: {}", pmName);

        List<AccessRequest> accessRequests = accessRequestRepository.findAllUnreadPMRequestsByName(pmName);
        logger.info("Fetched {} unread access requests for PM: {}", accessRequests.size(), pmName);

        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    /**
     * Get Project Manager requests
     *
     * @param pmName The name of the Project Manager whose requests are retrieved.
     * @return List of access requests response using DTO.
     */
    @Override
    public List<AccessResponseDTO> getPMRequests(String pmName) {
        logger.info("Fetching all access requests for PM: {}", pmName);

        List<AccessRequest> accessRequests = accessRequestRepository.findAllPMRequestsByName(pmName);
        logger.info("Fetched {} access requests for PM: {}", accessRequests.size(), pmName);

        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    /**
     * Set PM requests notification
     *
     * @param accessRequestId The ID of the access request.
     */
    // Set PM notification status to `true` for the specified access request
    @Override
    public void setPMRequestsNotificationTrue(Long accessRequestId) {
        logger.info("Setting PM notification to true for AccessRequest with ID: {}", accessRequestId);

        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(accessRequestId);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest accessRequest = optionalAccessRequest.get();
            accessRequest.setPmNotified(true);
            accessRequestRepository.save(accessRequest);

            logger.info("PM notification set to true successfully.");
        }
    }

    @Override
    @Transactional
    public void clearAllNotifications(){
        logger.info("Deleting all the request notifications.");
        accessRequestRepository.deleteAll();

        logger.info("Deleted all request notifications.");
    }

    // Map `AccessRequest` entities to `AccessResponseDTO` objects
    public List<AccessResponseDTO> mapAccessRequestsToResponseDTOs(List<AccessRequest> accessRequests) {
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequests) {
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
            accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessResponseDTO.setPmName(accessRequest.getPmName());
            String listOfPMRequests;
            if (accessRequest.isAllowed()) {
                listOfPMRequests = RESPONSE + accessRequest.getUser().getName() + " has been granted";
            } else {
                listOfPMRequests = RESPONSE + accessRequest.getUser().getName() + " has been denied";
            }
            accessResponseDTO.setResponse(listOfPMRequests);
            accessResponseDTO.setNotified(accessRequest.isPmNotified());
            accessResponseDTOList.add(accessResponseDTO);
        }
        return accessResponseDTOList;
    }

}
