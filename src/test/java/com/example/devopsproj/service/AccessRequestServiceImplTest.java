package com.example.devopsproj.service;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.model.AccessRequest;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.AccessRequestRepository;
import com.example.devopsproj.service.implementations.AccessRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccessRequestServiceImplTest {

    @InjectMocks
    private AccessRequestServiceImpl accessRequestService;
    @Mock
    private AccessRequestRepository accessRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS ------

    @Test
    void testCreateRequest_Success() {
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        accessRequestDTO.setAccessRequestId(1L);
        accessRequestDTO.setPmName("John Doe");

        AccessRequest accessRequestToSave = new AccessRequest();
        accessRequestToSave.setAccessRequestId(accessRequestDTO.getAccessRequestId());
        accessRequestToSave.setPmName(accessRequestDTO.getPmName());

        when(accessRequestRepository.save(Mockito.any(AccessRequest.class))).thenReturn(accessRequestToSave);

        AccessRequestDTO createdRequestDTO = accessRequestService.createRequest(accessRequestDTO);

        assertNotNull(createdRequestDTO);
        assertEquals(accessRequestDTO.getAccessRequestId(), createdRequestDTO.getAccessRequestId());
        assertEquals(accessRequestDTO.getPmName(), createdRequestDTO.getPmName());
    }

    @Test
    void testGetAllRequests_Success() {
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName("John Doe");

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName("Jane Smith");

        List<AccessRequest> accessRequestList = Arrays.asList(accessRequest1, accessRequest2);

        when(accessRequestRepository.findAll()).thenReturn(accessRequestList);

        List<AccessRequestDTO> requestDTOList = accessRequestService.getAllRequests();

        assertNotNull(requestDTOList);
        assertEquals(2, requestDTOList.size());

        assertEquals(accessRequest1.getAccessRequestId(), requestDTOList.get(0).getAccessRequestId());
        assertEquals(accessRequest1.getPmName(), requestDTOList.get(0).getPmName());

        assertEquals(accessRequest2.getAccessRequestId(), requestDTOList.get(1).getAccessRequestId());
        assertEquals(accessRequest2.getPmName(), requestDTOList.get(1).getPmName());
    }

    @Test
    void testGetAllActiveRequests_Success() {
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName("John Doe");

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName("Jane Smith");

        List<AccessRequest> activeAccessRequestList = Arrays.asList(accessRequest1, accessRequest2);

        when(accessRequestRepository.findAllActiveRequests()).thenReturn(activeAccessRequestList);

        List<AccessRequestDTO> requestDTOList = accessRequestService.getAllActiveRequests();

        assertNotNull(requestDTOList);
        assertEquals(2, requestDTOList.size());

        assertEquals(accessRequest1.getAccessRequestId(), requestDTOList.get(0).getAccessRequestId());
        assertEquals(accessRequest1.getPmName(), requestDTOList.get(0).getPmName());

        assertEquals(accessRequest2.getAccessRequestId(), requestDTOList.get(1).getAccessRequestId());
        assertEquals(accessRequest2.getPmName(), requestDTOList.get(1).getPmName());
    }

    @Test
    void testGetUpdatedRequests_Success() {
        Long requestId = 1L;
        AccessRequestDTO updatedRequestDTO = new AccessRequestDTO();
        updatedRequestDTO.setAllowed(true);

        AccessRequest existingAccessRequest = new AccessRequest();
        existingAccessRequest.setAccessRequestId(requestId);
        existingAccessRequest.setPmName("John Doe");
        existingAccessRequest.setRequestDescription("Request description");
        existingAccessRequest.setAllowed(false);

        Project project = new Project();
        project.setProjectId(10L);
        project.setProjectName("Sample Project");

        User user = new User();
        user.setId(20L);
        user.setName("User 1");
        user.setEmail("user@gmail.com");

        existingAccessRequest.setProject(project);
        existingAccessRequest.setUser(user);

        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(existingAccessRequest));

        List<AccessRequest> accessRequests = new ArrayList<>();
        accessRequests.add(existingAccessRequest);

        when(accessRequestRepository.findAllActiveRequests()).thenReturn(accessRequests);

        List<AccessResponseDTO> responseDTOList = accessRequestService.getUpdatedRequests(requestId, updatedRequestDTO);

        assertNotNull(responseDTOList);
        assertEquals(1, responseDTOList.size());

        AccessResponseDTO responseDTO = responseDTOList.get(0);
        assertEquals(existingAccessRequest.getAccessRequestId(), responseDTO.getAccessRequestId());
        assertEquals(existingAccessRequest.getPmName(), responseDTO.getPmName());
        assertEquals(existingAccessRequest.getRequestDescription(), responseDTO.getAccessDescription());
        assertEquals(updatedRequestDTO.isAllowed(), responseDTO.isAllowed());

        ProjectDTO projectDTO = responseDTO.getProject();
        assertNotNull(projectDTO);
        assertEquals(project.getProjectId(), projectDTO.getProjectId());
        assertEquals(project.getProjectName(), projectDTO.getProjectName());

        UserDTO userDTO = responseDTO.getUser();
        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getName(), userDTO.getName());
        assertEquals(user.getEmail(), userDTO.getEmail());
    }

    @Test
    void testGetPMUnreadRequests_Success() {
        String pmName = "John Doe";

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");

        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName(pmName);
        accessRequest1.setUser(user1);

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName(pmName);
        accessRequest2.setUser(user2);

        List<AccessRequest> unreadRequests = Arrays.asList(accessRequest1, accessRequest2);

        when(accessRequestRepository.findAllUnreadPMRequestsByName(pmName)).thenReturn(unreadRequests);

        List<AccessResponseDTO> responseDTOList = accessRequestService.getPMUnreadRequests(pmName);

        assertNotNull(responseDTOList);
        assertEquals(2, responseDTOList.size());

        assertEquals(accessRequest1.getAccessRequestId(), responseDTOList.get(0).getAccessRequestId());
        assertEquals(accessRequest2.getAccessRequestId(), responseDTOList.get(1).getAccessRequestId());
    }

    @Test
    void testGetPMRequests_Success() {
        String pmName = "John Doe";

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");

        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName(pmName);
        accessRequest1.setUser(user1);

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName(pmName);
        accessRequest2.setUser(user2);

        List<AccessRequest> readRequests = Arrays.asList(accessRequest1, accessRequest2);

        when(accessRequestRepository.findAllPMRequestsByName(pmName)).thenReturn(readRequests);

        List<AccessResponseDTO> responseDTOList = accessRequestService.getPMRequests(pmName);

        assertNotNull(responseDTOList);
        assertEquals(2, responseDTOList.size());

        assertEquals(accessRequest1.getAccessRequestId(), responseDTOList.get(0).getAccessRequestId());
        assertEquals(accessRequest2.getAccessRequestId(), responseDTOList.get(1).getAccessRequestId());
    }

    @Test
    void testMapAccessRequestsToResponseDTOs_Success() {
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName("John Doe");
        accessRequest1.setAllowed(true);

        User user1 = new User();
        user1.setId(101L);
        user1.setName("User 1");
        accessRequest1.setUser(user1);

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName("Jane Smith");
        accessRequest2.setAllowed(false);

        User user2 = new User();
        user2.setId(102L);
        user2.setName("User 2");
        accessRequest2.setUser(user2);

        List<AccessRequest> accessRequests = Arrays.asList(accessRequest1, accessRequest2);

        List<AccessResponseDTO> responseDTOList = accessRequestService.mapAccessRequestsToResponseDTOs(accessRequests);

        assertNotNull(responseDTOList);
        assertEquals(2, responseDTOList.size());

        AccessResponseDTO responseDTO1 = responseDTOList.get(0);
        assertEquals(accessRequest1.getAccessRequestId(), responseDTO1.getAccessRequestId());
        assertEquals(accessRequest1.getPmName(), responseDTO1.getPmName());
        assertEquals("Request for adding User 1 has been granted", responseDTO1.getResponse());
        assertFalse(responseDTO1.isNotified());

        AccessResponseDTO responseDTO2 = responseDTOList.get(1);
        assertEquals(accessRequest2.getAccessRequestId(), responseDTO2.getAccessRequestId());
        assertEquals(accessRequest2.getPmName(), responseDTO2.getPmName());
        assertEquals("Request for adding User 2 has been denied", responseDTO2.getResponse());
        assertFalse(responseDTO2.isNotified());
    }

    @Test
    void testSetPMRequestsNotificationTrue_Success() {
        Long accessRequestId = 1L;

        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setAccessRequestId(accessRequestId);
        accessRequest.setPmNotified(false);

        when(accessRequestRepository.findById(accessRequestId)).thenReturn(Optional.of(accessRequest));

        accessRequestService.setPMRequestsNotificationTrue(accessRequestId);

        assertTrue(accessRequest.isPmNotified());
    }

    @Test
    void testClearAllNotifications_Success() {
        accessRequestService.clearAllNotifications();

        verify(accessRequestRepository, times(1)).deleteAll();
    }


    // ----- FAILURE ------

    @Test
    void testGetAllActiveRequests_NoActiveRequests() {
        List<AccessRequest> emptyAccessRequestList = Collections.emptyList();

        when(accessRequestRepository.findAllActiveRequests()).thenReturn(emptyAccessRequestList);

        List<AccessRequestDTO> requestDTOList = accessRequestService.getAllActiveRequests();

        assertNotNull(requestDTOList);
        assertTrue(requestDTOList.isEmpty());
    }

    @Test
    void testGetUpdatedRequests_OptionalNotPresent() {
        Long id = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        accessRequestDTO.setAllowed(true);

        when(accessRequestRepository.findById(id)).thenReturn(Optional.empty());

        List<AccessResponseDTO> responseDTOList = accessRequestService.getUpdatedRequests(id, accessRequestDTO);

        verify(accessRequestRepository, never()).save(any());

        assertNotNull(responseDTOList);
        assertTrue(responseDTOList.isEmpty());
    }

    @Test
    void testGetPMUnreadRequests_NoUnreadRequests() {
        String pmName = "Jane Smith";

        when(accessRequestRepository.findAllUnreadPMRequestsByName(pmName)).thenReturn(Collections.emptyList());

        List<AccessResponseDTO> responseDTOList = accessRequestService.getPMUnreadRequests(pmName);

        assertNotNull(responseDTOList);
        assertTrue(responseDTOList.isEmpty());
    }

    @Test
    void testGetPMRequests_NoRequests() {
        String pmName = "Jane Smith";

        when(accessRequestRepository.findAllPMRequestsByName(pmName)).thenReturn(Collections.emptyList());

        List<AccessResponseDTO> responseDTOList = accessRequestService.getPMRequests(pmName);

        assertNotNull(responseDTOList);
        assertTrue(responseDTOList.isEmpty());
    }

    @Test
    void testSetPMRequestsNotificationTrue_OptionalNotPresent() {
        Long accessRequestId = 1L;

        when(accessRequestRepository.findById(accessRequestId)).thenReturn(Optional.empty());

        accessRequestService.setPMRequestsNotificationTrue(accessRequestId);

        verify(accessRequestRepository, never()).save(any());
    }

    @Test
    void testClearAllNotifications_Exception() {
        doThrow(new RuntimeException("Error deleting notifications")).when(accessRequestRepository).deleteAll();
        assertThrows(RuntimeException.class, () -> accessRequestService.clearAllNotifications());
    }

}
