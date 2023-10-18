package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import com.example.devopsproj.service.interfaces.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccessRequestControllerTest {

    @InjectMocks
    private AccessRequestController accessRequestController;

    @Mock
    private AccessRequestService accessRequestService;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
     void testCreateAccessRequest_Success() {
        AccessRequestDTO requestDTO = new AccessRequestDTO();
        when(accessRequestService.createRequest(requestDTO)).thenReturn(requestDTO);

        ResponseEntity<Object> response = accessRequestController.createAccessRequest(requestDTO);

        verify(accessRequestService, times(1)).createRequest(requestDTO);
        assert(response.getStatusCode() == HttpStatus.OK);
    }

    @Test
     void testCreateAccessRequest_Failure() {
        AccessRequestDTO requestDTO = new AccessRequestDTO();
        when(accessRequestService.createRequest(requestDTO)).thenReturn(null); // Mocking a failure by returning null

        ResponseEntity<Object> response = accessRequestController.createAccessRequest(requestDTO);

        verify(accessRequestService, times(1)).createRequest(requestDTO);
        assert(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testUpdateAccessRequestWithNonEmptyList() {
        // Arrange
        Long requestId = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        accessResponseDTOList.add(new AccessResponseDTO());

        when(accessRequestService.getUpdatedRequests(requestId, accessRequestDTO)).thenReturn(accessResponseDTOList);

        // Act
        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, accessRequestDTO);

        // Assert
        verify(accessRequestService, times(1)).getUpdatedRequests(requestId, accessRequestDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accessResponseDTOList, response.getBody());
    }

    @Test
    void testUpdateAccessRequestWithNonEmptyListt() {
        // Arrange
        Long requestId = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        accessResponseDTOList.add(new AccessResponseDTO());

        when(accessRequestService.getUpdatedRequests(requestId, accessRequestDTO)).thenReturn(accessResponseDTOList);

        // Act
        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, accessRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testUpdateAccessRequestWithNonEmptyListll() {
        // Arrange
        Long requestId = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
        accessResponseDTOList.add(new AccessResponseDTO());

        when(accessRequestService.getUpdatedRequests(requestId, accessRequestDTO)).thenReturn(accessResponseDTOList);

        // Act
        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, accessRequestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateAccessRequestWithEmptyList() {
        // Arrange
        Long requestId = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

        when(accessRequestService.getUpdatedRequests(requestId, accessRequestDTO)).thenReturn(accessResponseDTOList);

        // Act
        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, accessRequestDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
     void testGetAllActiveRequests_NoRequests() {
        when(accessRequestService.getAllActiveRequests()).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = accessRequestController.getAllActiveRequests();

        verify(accessRequestService, times(1)).getAllActiveRequests();
        assert(response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    void testGetAllActiveRequests_WithRequests() {
        List<AccessRequestDTO> requestDTOList = Collections.singletonList(new AccessRequestDTO());
        when(accessRequestService.getAllActiveRequests()).thenReturn(requestDTOList);

        ResponseEntity<Object> response = accessRequestController.getAllActiveRequests();

        verify(accessRequestService, times(1)).getAllActiveRequests();
        assert(response.getStatusCode() == HttpStatus.OK);
    }


    @Test
     void testGetAllRequests_NoRequests() {
        when(accessRequestService.getAllRequests()).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = accessRequestController.getAllRequests();

        verify(accessRequestService, times(1)).getAllRequests();
        assert(response.getStatusCode() == HttpStatus.NO_CONTENT);
        assert(response.getBody().equals("No requests"));
    }

    @Test
     void testGetAllRequests_WithRequests() {
        List<AccessRequestDTO> requestDTOList = Collections.singletonList(new AccessRequestDTO());
        when(accessRequestService.getAllRequests()).thenReturn(requestDTOList);

        ResponseEntity<Object> response = accessRequestController.getAllRequests();

        verify(accessRequestService, times(1)).getAllRequests();
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(requestDTOList));
    }

    @Test
     void testUpdateAccessRequest_Success() {
        Long requestId = 1L;
        AccessRequestDTO requestDTO = new AccessRequestDTO();
        List<AccessResponseDTO> responseDTOList = Collections.singletonList(new AccessResponseDTO());

        when(accessRequestService.getUpdatedRequests(requestId, requestDTO)).thenReturn(responseDTOList);

        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, requestDTO);

        verify(accessRequestService, times(1)).getUpdatedRequests(requestId, requestDTO);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(responseDTOList));
    }

    @Test
    void testUpdateAccessRequest_NotFound() {
        Long requestId = 1L;
        AccessRequestDTO requestDTO = new AccessRequestDTO();

        when(accessRequestService.getUpdatedRequests(requestId, requestDTO)).thenReturn(null);

        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, requestDTO);

        verify(accessRequestService, times(1)).getUpdatedRequests(requestId, requestDTO);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetUnreadPMRequestsNotification_NoUnreadRequests() {
        String pmName = "John";
        List<AccessResponseDTO> unreadRequests = Collections.emptyList();

        when(accessRequestService.getPMUnreadRequests(pmName)).thenReturn(unreadRequests);

        ResponseEntity<Object> response = accessRequestController.getUnreadPMRequestsNotification(pmName);

        verify(accessRequestService, times(1)).getPMUnreadRequests(pmName);
        assert(response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
     void testGetUnreadPMRequestsNotification_WithUnreadRequests() {
        String pmName = "John";
        List<AccessResponseDTO> unreadRequests = Collections.singletonList(new AccessResponseDTO());

        when(accessRequestService.getPMUnreadRequests(pmName)).thenReturn(unreadRequests);

        ResponseEntity<Object> response = accessRequestController.getUnreadPMRequestsNotification(pmName);

        verify(accessRequestService, times(1)).getPMUnreadRequests(pmName);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(unreadRequests));
    }

    @Test
     void testGetPMRequestsNotification_WithRequests() {
        String pmName = "John";
        List<AccessResponseDTO> result = Collections.singletonList(new AccessResponseDTO());

        when(accessRequestService.getPMRequests(pmName)).thenReturn(result);

        ResponseEntity<Object> response = accessRequestController.getPMRequestsNotification(pmName);

        verify(accessRequestService, times(1)).getPMRequests(pmName);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(result));
    }

    @Test
     void testSetPMRequestsNotificationToTrue_Success() {
        Long accessRequestId = 1L;

        ResponseEntity<String> response = accessRequestController.setPMRequestsNotificationToTrue(accessRequestId);

        verify(accessRequestService, times(1)).setPMRequestsNotificationTrue(accessRequestId);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals("Notification read"));
    }

    @Test
     void testDeleteAllNotifications_Success() {
        ResponseEntity<String> response = accessRequestController.deleteAllNotifications();

        verify(accessRequestService, times(1)).clearAllNotifications();
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals("All notifications cleared"));
    }

}











