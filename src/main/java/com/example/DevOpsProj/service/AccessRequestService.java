package com.example.DevOpsProj.service;

import com.example.DevOpsProj.dto.requestDto.AccessRequestDTO;
import com.example.DevOpsProj.dto.responseDto.AccessResponseDTO;
import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.AccessRequest;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.AccessRequestRepository;
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

    public AccessRequestDTO createRequest(AccessRequestDTO accessRequestDTO){
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setPmName(accessRequestDTO.getPmName());
        accessRequest.setRequestDescription(accessRequestDTO.getRequestDescription());
        accessRequest.setUser(mapUserDTOToUser(accessRequestDTO.getUser()));
        accessRequest.setProject(mapProjectDTOToProject(accessRequestDTO.getProject()));
        accessRequestRepository.save(accessRequest);
        return modelMapper.map(accessRequest, AccessRequestDTO.class);
    }

    public List<AccessRequestDTO> getAllRequests() {
        List<AccessRequest> accessRequestList = accessRequestRepository.findAllActiveRequests();
        if (accessRequestList.isEmpty()){
            return Collections.emptyList();
        }
        List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

        for (AccessRequest accessRequest : accessRequestList) {
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setAccessRequestId(accessRequest.getAccessRequestId());
            accessRequestDTO.setPmName(accessRequest.getPmName());
            accessRequestDTO.setUser(mapUserToUserDTO(accessRequest.getUser()));
            accessRequestDTO.setProject(mapProjectToProjectDTO(accessRequest.getProject()));
            accessRequestDTO.setRequestDescription(accessRequest.getRequestDescription());
            accessRequestDTO.setAllowed(accessRequest.isAllowed());
            accessRequestDTOList.add(accessRequestDTO);
        }

        return accessRequestDTOList;
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
                    mapUserToUserDTO(updatedAccessRequest.getUser()),
                    mapProjectToProjectDTO(updatedAccessRequest.getProject()),
                    updatedAccessRequest.getRequestDescription(),
                    updatedAccessRequest.isAllowed());
        }
        List<AccessRequest> accessRequests = accessRequestRepository.findAllActiveRequests();
        List<AccessResponseDTO> accessResponseDTOList = accessRequests.stream()
                .map(accessRequest -> new AccessResponseDTO(accessRequest.getAccessRequestId(), accessRequest.getPmName(), mapUserToUserDTO(accessRequest.getUser()), mapProjectToProjectDTO(accessRequest.getProject()), accessRequest.getRequestDescription(), accessRequest.isAllowed()))
                .collect(Collectors.toList());
        return accessResponseDTOList;
    }

    public List<String> getPMRequests(String pmName){
        List<AccessRequest> accessRequests = accessRequestRepository.findAllPMRequests(pmName);
        if (accessRequests.isEmpty()){
            return Collections.emptyList();
        }
//        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        List<String> listOfPMRequests = new ArrayList<>();
        for (AccessRequest accessRequest : accessRequests) {
//            AccessResponseDTO accessResponseDTO = new AccessResponseDTO();
//            accessResponseDTO.setAccessRequestId(accessRequest.getAccessRequestId());
//            accessResponseDTO.setPmName(accessRequest.getPmName());
//            accessResponseDTO.setUser(mapUserToUserDTO(accessRequest.getUser()));
//            accessResponseDTOList.add(accessResponseDTO);
            if (!accessRequest.isAllowed()){
                listOfPMRequests.add("Request for adding "+accessRequest.getUser().getName()+" has been granted");
            }
            else {
                listOfPMRequests.add("Request for adding "+accessRequest.getUser().getName()+" has been denied");
            }
        }
        return listOfPMRequests;
    }


    private UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        // Map other fields as needed
        return userDTO;
    }
    private ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        // Map other fields as needed
        return projectDTO;
    }

    private User mapUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        // Set other fields as needed
        return user;
    }

    private Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        // Set other fields as needed
        return project;
    }
}
