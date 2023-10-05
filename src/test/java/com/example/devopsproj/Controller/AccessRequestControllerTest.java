package com.example.devopsproj.Controller;

import com.example.devopsproj.controller.AccessRequestController;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AccessRequestControllerTest {

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
    public void testCreateAccessRequest_Success() {
        AccessRequestDTO requestDTO = new AccessRequestDTO();
        when(accessRequestService.createRequest(requestDTO)).thenReturn(requestDTO);

        ResponseEntity<Object> response = accessRequestController.createAccessRequest(requestDTO);

        verify(accessRequestService, times(1)).createRequest(requestDTO);
        assert(response.getStatusCode() == HttpStatus.OK);
    }

    @Test
    public void testCreateAccessRequest_Failure() {
        AccessRequestDTO requestDTO = new AccessRequestDTO();
        when(accessRequestService.createRequest(requestDTO)).thenReturn(null); // Mocking a failure by returning null

        ResponseEntity<Object> response = accessRequestController.createAccessRequest(requestDTO);

        verify(accessRequestService, times(1)).createRequest(requestDTO);
        assert(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Test
    public void testGetAllActiveRequests_NoRequests() {
        when(accessRequestService.getAllActiveRequests()).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = accessRequestController.getAllActiveRequests();

        verify(accessRequestService, times(1)).getAllActiveRequests();
        assert(response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    public void testGetAllActiveRequests_WithRequests() {
        List<AccessRequestDTO> requestDTOList = Collections.singletonList(new AccessRequestDTO());
        when(accessRequestService.getAllActiveRequests()).thenReturn(requestDTOList);

        ResponseEntity<Object> response = accessRequestController.getAllActiveRequests();

        verify(accessRequestService, times(1)).getAllActiveRequests();
        assert(response.getStatusCode() == HttpStatus.OK);
    }


    @Test
    public void testGetAllRequests_NoRequests() {
        when(accessRequestService.getAllRequests()).thenReturn(Collections.emptyList());

        ResponseEntity<Object> response = accessRequestController.getAllRequests();

        verify(accessRequestService, times(1)).getAllRequests();
        assert(response.getStatusCode() == HttpStatus.NO_CONTENT);
        assert(response.getBody().equals("No requests"));
    }

    @Test
    public void testGetAllRequests_WithRequests() {
        List<AccessRequestDTO> requestDTOList = Collections.singletonList(new AccessRequestDTO());
        when(accessRequestService.getAllRequests()).thenReturn(requestDTOList);

        ResponseEntity<Object> response = accessRequestController.getAllRequests();

        verify(accessRequestService, times(1)).getAllRequests();
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(requestDTOList));
    }

    @Test
    public void testUpdateAccessRequest_Success() {
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
    public void testUpdateAccessRequest_NotFound() {
        Long requestId = 1L;
        AccessRequestDTO requestDTO = new AccessRequestDTO();

        when(accessRequestService.getUpdatedRequests(requestId, requestDTO)).thenReturn(null);

        ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, requestDTO);

        verify(accessRequestService, times(1)).getUpdatedRequests(requestId, requestDTO);
        assert(response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetUnreadPMRequestsNotification_NoUnreadRequests() {
        String pmName = "John";
        List<AccessResponseDTO> unreadRequests = Collections.emptyList();

        when(accessRequestService.getPMUnreadRequests(pmName)).thenReturn(unreadRequests);

        ResponseEntity<Object> response = accessRequestController.getUnreadPMRequestsNotification(pmName);

        verify(accessRequestService, times(1)).getPMUnreadRequests(pmName);
        assert(response.getStatusCode() == HttpStatus.NO_CONTENT);
    }

    @Test
    public void testGetUnreadPMRequestsNotification_WithUnreadRequests() {
        String pmName = "John";
        List<AccessResponseDTO> unreadRequests = Collections.singletonList(new AccessResponseDTO());

        when(accessRequestService.getPMUnreadRequests(pmName)).thenReturn(unreadRequests);

        ResponseEntity<Object> response = accessRequestController.getUnreadPMRequestsNotification(pmName);

        verify(accessRequestService, times(1)).getPMUnreadRequests(pmName);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(unreadRequests));
    }

    @Test
    public void testGetPMRequestsNotification_WithRequests() {
        String pmName = "John";
        List<AccessResponseDTO> result = Collections.singletonList(new AccessResponseDTO());

        when(accessRequestService.getPMRequests(pmName)).thenReturn(result);

        ResponseEntity<Object> response = accessRequestController.getPMRequestsNotification(pmName);

        verify(accessRequestService, times(1)).getPMRequests(pmName);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals(result));
    }

    @Test
    public void testSetPMRequestsNotificationToTrue_Success() {
        Long accessRequestId = 1L;

        ResponseEntity<String> response = accessRequestController.setPMRequestsNotificationToTrue(accessRequestId);

        verify(accessRequestService, times(1)).setPMRequestsNotificationTrue(accessRequestId);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals("Notification read"));
    }

    @Test
    public void testDeleteAllNotifications_Success() {
        ResponseEntity<String> response = accessRequestController.deleteAllNotifications();

        verify(accessRequestService, times(1)).clearAllNotifications();
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals("All notifications cleared"));
    }

}











