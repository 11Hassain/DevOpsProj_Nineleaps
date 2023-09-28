package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.AccessRequest;
import com.example.devopsproj.model.User;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.model.Project;
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
        AccessRequest accessRequest = mapDTOToAccessRequest(accessRequestDTO);
        accessRequestRepository.save(accessRequest);
        return mapAccessRequestToDTO(accessRequest);
    }

    @Override
    public List<AccessRequestDTO> getAllRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAll();
        return mapAccessRequestListToDTOList(accessRequestList);
    }

    @Override
    public List<AccessRequestDTO> getAllActiveRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();
        return mapAccessRequestListToDTOList(accessRequestList);
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
        return mapAccessRequestListToResponseDTOList(accessRequests);
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


    //Other methods...

    private AccessRequestDTO mapAccessRequestToDTO(AccessRequest accessRequest) {
        return modelMapper.map(accessRequest, AccessRequestDTO.class);
    }

    private List<AccessRequestDTO> mapAccessRequestListToDTOList(List<AccessRequest> accessRequestList) {
        return accessRequestList.stream()
                .map(this::mapAccessRequestToDTO)
                .toList();
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
                .toList();
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
