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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccessRequestServiceImpl implements AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final ModelMapper modelMapper;

    private final JwtService jwtService;

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
            existingAccessRequest.setAllowed(accessRequestDTO.isAllowed());
            existingAccessRequest.setUpdated(true);
            accessRequestRepository.save(existingAccessRequest);
        }
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
        return accessResponseDTOList;
    }

    private static final String RESPONSE = "Request for adding ";

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
    public void clearAllNotifications(){
        accessRequestRepository.deleteAll();
    }

}
