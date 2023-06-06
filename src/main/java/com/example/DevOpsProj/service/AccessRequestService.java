package com.example.DevOpsProj.service;

import com.example.DevOpsProj.dto.requestDto.AccessRequestDTO;
import com.example.DevOpsProj.dto.responseDto.AccessResponseDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.AccessRequest;
import com.example.DevOpsProj.repository.AccessRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessRequestService {

    @Autowired
    private AccessRequestRepository accessRequestRepository;

    @Autowired
    private ModelMapper modelMapper;

    public AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO){
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setPmName(accessRequestDTO.getPmName());
        accessRequest.setRequestDescription(accessRequestDTO.getRequestDescription());
        accessRequest.setUser(accessRequestDTO.getUser());
        accessRequest.setProject(accessRequestDTO.getProject());
        accessRequestRepository.save(accessRequest);
        return modelMapper.map(accessRequest, AccessRequestDTO.class);
    }

    public List<AccessResponseDTO> getAllRequests(){
        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();
        List<AccessResponseDTO> accessResponseDTOList = accessRequestList.stream()
                .map(accessRequest -> new AccessResponseDTO(accessRequest.getAccessRequestId(), accessRequest.getPmName(), accessRequest.getUser(), accessRequest.getProject(), accessRequest.getRequestDescription(), accessRequest.isAllowed()))
                .collect(Collectors.toList());
        return accessResponseDTOList;

//        return accessRequestRepository.findAllActiveRequests();
    }

    public List<AccessResponseDTO> getUpdatedRequests(Long id, AccessRequestDTO accessRequestDTO){
        Optional<AccessRequest> optionalAccessRequest = accessRequestRepository.findById(id);
        if(optionalAccessRequest.isPresent()){
            AccessRequest existingAccessRequest = optionalAccessRequest.get();
            existingAccessRequest.setAllowed(accessRequestDTO.isAllowed());
            existingAccessRequest.setUpdated(true);
            AccessRequest updatedAccessRequest = accessRequestRepository.save(existingAccessRequest);
            AccessResponseDTO accessResponseDTO = new AccessResponseDTO(
                    updatedAccessRequest.getAccessRequestId(),
                    updatedAccessRequest.getPmName(),
                    updatedAccessRequest.getUser(),
                    updatedAccessRequest.getProject(),
                    updatedAccessRequest.getRequestDescription(),
                    updatedAccessRequest.isAllowed());
        }
        List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();
        List<AccessResponseDTO> accessResponseDTOList = accessRequests.stream()
                .map(accessRequest -> new AccessResponseDTO(accessRequest.getAccessRequestId(), accessRequest.getPmName(), accessRequest.getUser(), accessRequest.getProject(), accessRequest.getRequestDescription(), accessRequest.isAllowed()))
                .collect(Collectors.toList());
        return accessResponseDTOList;
    }
}
