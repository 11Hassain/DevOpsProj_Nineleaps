package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.model.AccessRequest;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.repository.AccessRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccessRequestServiceTest {
    @InjectMocks
    private AccessRequestServiceImpl accessRequestService;

    @Mock
    private AccessRequestRepository accessRequestRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

    }

    @Test
    public void testCreateRequest_Success() {
        // Arrange
        AccessRequestDTO inputDTO = new AccessRequestDTO();
        inputDTO.setAccessRequestId(1L); // Set input data as needed
        inputDTO.setPmName("John Doe");

        AccessRequest savedAccessRequest = new AccessRequest();
        savedAccessRequest.setAccessRequestId(1L); // Set expected saved data
        savedAccessRequest.setPmName("John Doe");

        when(accessRequestRepository.save(any(AccessRequest.class))).thenReturn(savedAccessRequest);

        // Act
        AccessRequestDTO result = accessRequestService.createRequest(inputDTO);

        // Assert
        assertEquals(savedAccessRequest.getAccessRequestId(), result.getAccessRequestId());
        assertEquals(savedAccessRequest.getPmName(), result.getPmName());

        // Verify that accessRequestRepository.save() was called once with the expected argument
        verify(accessRequestRepository, times(1)).save(any(AccessRequest.class));
    }
    @Test
    public void testGetAllRequests_RequestsExist() {
        // Arrange
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName("PM1");

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName("PM2");

        List<AccessRequest> accessRequestList = new ArrayList<>();
        accessRequestList.add(accessRequest1);
        accessRequestList.add(accessRequest2);

        when(accessRequestRepository.findAll()).thenReturn(accessRequestList);

        // Act
        List<AccessRequestDTO> result = accessRequestService.getAllRequests();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getAccessRequestId());
        assertEquals("PM1", result.get(0).getPmName());
        assertEquals(2L, result.get(1).getAccessRequestId());
        assertEquals("PM2", result.get(1).getPmName());
    }

    @Test
    public void testGetAllRequests_NoRequestsExist() {
        // Arrange
        when(accessRequestRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<AccessRequestDTO> result = accessRequestService.getAllRequests();

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllActiveRequests_ActiveRequestsExist() {
        // Arrange
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName("PM1");

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName("PM2");

        List<AccessRequest> activeRequestList = new ArrayList<>();
        activeRequestList.add(accessRequest1);
        activeRequestList.add(accessRequest2);

        when(accessRequestRepository.findAllActiveRequests()).thenReturn(activeRequestList);

        // Act
        List<AccessRequestDTO> result = accessRequestService.getAllActiveRequests();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getAccessRequestId());
        assertEquals("PM1", result.get(0).getPmName());
        assertEquals(2L, result.get(1).getAccessRequestId());
        assertEquals("PM2", result.get(1).getPmName());
    }

    @Test
    public void testGetAllActiveRequests_NoActiveRequestsExist() {
        // Arrange
        when(accessRequestRepository.findAllActiveRequests()).thenReturn(new ArrayList<>());

        // Act
        List<AccessRequestDTO> result = accessRequestService.getAllActiveRequests();

        // Assert
        assertEquals(0, result.size());
    }
    @Test
    public void testGetUpdatedRequests_ExistingAccessRequest() {
        // Arrange
        Long requestId = 1L;
        AccessRequestDTO updatedRequestDTO = new AccessRequestDTO();
        updatedRequestDTO.setAllowed(true);

        AccessRequest existingRequest = new AccessRequest();
        existingRequest.setAccessRequestId(requestId);
        existingRequest.setAllowed(false);

        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
        when(accessRequestRepository.findAllActiveRequests()).thenReturn(new ArrayList<>());

        // Act
        List<AccessResponseDTO> result = accessRequestService.getUpdatedRequests(requestId, updatedRequestDTO);

        // Assert
        verify(accessRequestRepository, times(1)).save(existingRequest);
        assertEquals(0, result.size());
        assertEquals(true, existingRequest.isAllowed());
        assertEquals(true, existingRequest.isUpdated());
    }

    @Test
    public void testGetUpdatedRequests_NonExistingAccessRequest() {
        // Arrange
        Long requestId = 1L;
        AccessRequestDTO updatedRequestDTO = new AccessRequestDTO();
        updatedRequestDTO.setAllowed(true);

        when(accessRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        when(accessRequestRepository.findAllActiveRequests()).thenReturn(new ArrayList<>());

        // Act
        List<AccessResponseDTO> result = accessRequestService.getUpdatedRequests(requestId, updatedRequestDTO);

        // Assert
        verify(accessRequestRepository, never()).save(any());
        assertEquals(0, result.size());
    }
    @Test
    public void testGetPMUnreadRequests() {
        // Arrange
        String pmName = "PM1";

        // Create a list of AccessRequest entities that match the query criteria
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setPmName(pmName);
        accessRequest1.setUpdated(true);
        accessRequest1.setPmNotified(false);

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setPmName(pmName);
        accessRequest2.setUpdated(true);
        accessRequest2.setPmNotified(false);

        // Create mock User objects and set their names
        User user1 = new User();
        user1.setName("John Doe");

        User user2 = new User();
        user2.setName("Jane Smith");

        // Set the User objects for the AccessRequest entities
        accessRequest1.setUser(user1);
        accessRequest2.setUser(user2);

        List<AccessRequest> expectedAccessRequests = new ArrayList<>();
        expectedAccessRequests.add(accessRequest1);
        expectedAccessRequests.add(accessRequest2);

        // Mock the repository behavior
        when(accessRequestRepository.findAllUnreadPMRequestsByName(pmName))
                .thenReturn(expectedAccessRequests);

        // Act
        List<AccessResponseDTO> result = accessRequestService.getPMUnreadRequests(pmName);

        // Assert
        assertEquals(expectedAccessRequests.size(), result.size());

    }
    @Test
    public void testGetPMRequests_Success() {
        // Arrange
        String pmName = "PM1";

        // Create mock AccessRequest entities
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setPmName(pmName);
        accessRequest1.setUser(mock(User.class)); // Mock User object

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setPmName(pmName);
        accessRequest2.setUser(mock(User.class)); // Mock User object

        List<AccessRequest> accessRequests = new ArrayList<>();
        accessRequests.add(accessRequest1);
        accessRequests.add(accessRequest2);

        // Mock the repository behavior
        when(accessRequestRepository.findAllPMRequestsByName(pmName))
                .thenReturn(accessRequests);

        // Act
        List<AccessResponseDTO> result = accessRequestService.getPMRequests(pmName);

        // Assert
        assertEquals(accessRequests.size(), result.size());
        // You can add more assertions here to validate the contents of the DTOs if needed
    }
    @Test
    public void testGetPMRequests_NoRequestsFound() {
        // Arrange
        String pmName = "PM1";

        // Mock an empty list of AccessRequest entities
        List<AccessRequest> accessRequests = Collections.emptyList();

        // Mock the repository behavior
        when(accessRequestRepository.findAllPMRequestsByName(pmName))
                .thenReturn(accessRequests);

        // Act
        List<AccessResponseDTO> result = accessRequestService.getPMRequests(pmName);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    public void testMapAccessRequestsToResponseDTOs() {
        // Arrange
        AccessRequest accessRequest1 = new AccessRequest();
        accessRequest1.setAccessRequestId(1L);
        accessRequest1.setPmName("PM1");

        User user1 = new User();
        user1.setName("User1");
        accessRequest1.setUser(user1);

        accessRequest1.setAllowed(true);
        accessRequest1.setPmNotified(false);

        AccessRequest accessRequest2 = new AccessRequest();
        accessRequest2.setAccessRequestId(2L);
        accessRequest2.setPmName("PM2");

        User user2 = new User();
        user2.setName("User2");
        accessRequest2.setUser(user2);

        accessRequest2.setAllowed(false);
        accessRequest2.setPmNotified(true);

        List<AccessRequest> accessRequests = new ArrayList<>();
        accessRequests.add(accessRequest1);
        accessRequests.add(accessRequest2);

        // Act
        List<AccessResponseDTO> result = accessRequestService.mapAccessRequestsToResponseDTOs(accessRequests);

        // Assert
        assertEquals(2, result.size());

        AccessResponseDTO responseDTO1 = result.get(0);
        assertEquals(accessRequest1.getAccessRequestId(), responseDTO1.getAccessRequestId());
        assertEquals(accessRequest1.getPmName(), responseDTO1.getPmName());
        assertEquals("Request for adding User1 has been granted", responseDTO1.getResponse());
        assertFalse(responseDTO1.isNotified());

        AccessResponseDTO responseDTO2 = result.get(1);
        assertEquals(accessRequest2.getAccessRequestId(), responseDTO2.getAccessRequestId());
        assertEquals(accessRequest2.getPmName(), responseDTO2.getPmName());
        assertEquals("Request for adding User2 has been denied", responseDTO2.getResponse());
        assertTrue(responseDTO2.isNotified());
    }

    @Test
    public void testSetPMRequestsNotificationTrue_Success() {
        // Arrange
        Long accessRequestId = 1L;
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setAccessRequestId(accessRequestId);
        accessRequest.setPmNotified(false);

        when(accessRequestRepository.findById(accessRequestId)).thenReturn(Optional.of(accessRequest));

        // Act
        accessRequestService.setPMRequestsNotificationTrue(accessRequestId);

        // Assert
        assertTrue(accessRequest.isPmNotified());
        verify(accessRequestRepository, times(1)).save(accessRequest);
    }

    @Test
    public void testSetPMRequestsNotificationTrue_NotFound() {
        // Arrange
        Long accessRequestId = 1L;

        when(accessRequestRepository.findById(accessRequestId)).thenReturn(Optional.empty());

        // Act
        accessRequestService.setPMRequestsNotificationTrue(accessRequestId);

        // Assert
        // Ensure that no interactions with the repository occurred
        verify(accessRequestRepository, never()).save(any());
    }
    @Test
    public void testClearAllNotifications() {
        // Act
        accessRequestService.clearAllNotifications();

        // Assert
        verify(accessRequestRepository, times(1)).deleteAll();
    }

    @Test
    public void testGetUpdatedRequests() {
        // Create a sample User object
        User sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("Sample User");

        // Create a sample Project object
        Project sampleProject = new Project();
        sampleProject.setProjectId(1L);
        sampleProject.setProjectName("Sample Project");

        // Create a sample AccessRequest object and associate it with the User and Project
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setUser(sampleUser); // Set the associated user
        accessRequest.setProject(sampleProject); // Set the associated project
        accessRequest.setAccessRequestId(1L);
        accessRequest.setPmName("Sample PM");
        accessRequest.setRequestDescription("Sample request");
        accessRequest.setAllowed(true);

        // Mock the behavior of AccessRequestRepository.findById
        when(accessRequestRepository.findById(1L)).thenReturn(Optional.of(accessRequest));

        // Create a list of AccessRequest objects
        List<AccessRequest> accessRequests = new ArrayList<>();
        accessRequests.add(accessRequest);

        // Mock the behavior of AccessRequestRepository.findAllActiveRequests
        when(accessRequestRepository.findAllActiveRequests()).thenReturn(accessRequests);

        // Create an AccessRequestDTO with updated information
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        accessRequestDTO.setAllowed(false);

        // Call the method to be tested
        List<AccessResponseDTO> result = accessRequestService.getUpdatedRequests(1L, accessRequestDTO);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals(false, result.get(0).isAllowed());
    }




}
