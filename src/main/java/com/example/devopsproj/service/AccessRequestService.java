package com.example.devopsproj.service;

import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.dto.responseDto.UserDTO;
import com.example.devopsproj.model.AccessRequest;
import com.example.devopsproj.model.User;
import com.example.devopsproj.dto.requestDto.AccessRequestDTO;
import com.example.devopsproj.dto.responseDto.AccessResponseDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.AccessRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessRequestService {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ModelMapper modelMapper;

    public AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO) {
        AccessRequest accessRequest = mapDTOToAccessRequest(accessRequestDTO);
        accessRequestRepository.save(accessRequest);
        return mapAccessRequestToDTO(accessRequest);
    }

    public List<AccessRequestDTO> getAllRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAll();
        return mapAccessRequestListToDTOList(accessRequestList);
    }

    public List<AccessRequestDTO> getAllActiveRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();
        return mapAccessRequestListToDTOList(accessRequestList);
    }


    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO) {
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest existingAccessRequest = optionalAccessRequest.get();
            existingAccessRequest.setAllowed(accessRequestDTO.isAllowed());
            existingAccessRequest.setUpdated(true);
            accessRequestRepository.save(existingAccessRequest);
        }
        List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();
        return mapAccessRequestListToResponseDTOList(accessRequests);
    }

    private static final String RESPONSE = "Request for adding ";

    public List<AccessResponseDTO> getPMUnreadRequests(String pmName) {
        List<AccessRequest> accessRequests = accessRequestRepository.findAllUnreadPMRequestsByName(pmName);
        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    public List<AccessResponseDTO> getPMRequests(String pmName) {
        List<AccessRequest> accessRequests = accessRequestRepository.findAllPMRequestsByName(pmName);
        return mapAccessRequestsToResponseDTOs(accessRequests);
    }

    private List<AccessResponseDTO> mapAccessRequestsToResponseDTOs(List<AccessRequest> accessRequests) {
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

    public void setPMRequestsNotificationTrue(Long accessRequestId) {
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(accessRequestId);
        if (optionalAccessRequest.isPresent()) {
            AccessRequest accessRequest = optionalAccessRequest.get();
            accessRequest.setPmNotified(true);
            accessRequestRepository.save(accessRequest);
        }
    }

    public void clearAllNotifications(){
        accessRequestRepository.deleteAll();
    }


    //Other methods...

    private AccessRequestDTO mapAccessRequestToDTO(AccessRequest accessRequest) {
        return modelMapper.map(accessRequest, AccessRequestDTO.class);
    }

    private List<AccessRequestDTO> mapAccessRequestListToDTOList(List<AccessRequest> accessRequestList) {
        return accessRequestList.stream()
                .map(this::mapAccessRequestToDTO)
                .collect(Collectors.toList());
    }

    private List<AccessResponseDTO> mapAccessRequestListToResponseDTOList(List<AccessRequest> accessRequestList) {
        return accessRequestList.stream()
                .map(accessRequest -> new AccessResponseDTO(
                        accessRequest.getAccessRequestId(),
                        accessRequest.getPmName(),
                        mapUserToUserDTO(accessRequest.getUser()),
                        mapProjectToProjectDTO(accessRequest.getProject()),
                        accessRequest.getRequestDescription(),
                        accessRequest.isAllowed()))
                .collect(Collectors.toList());
    }

    private UserDTO mapUserToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private ProjectDTO mapProjectToProjectDTO(Project project) {
        return modelMapper.map(project, ProjectDTO.class);
    }

    private AccessRequest mapDTOToAccessRequest(AccessRequestDTO accessRequestDTO) {
        return modelMapper.map(accessRequestDTO, AccessRequest.class);
    }

}
