package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import com.example.devopsproj.service.interfaces.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mysql.cj.exceptions.MysqlErrorNumbers.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


class AccessRequestControllerTest {

    @InjectMocks
    private AccessRequestController accessRequestController;

    @Mock
    private AccessRequestService accessRequestService;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(accessRequestController).build();
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
    void testGetAllActiveRequests_NoRequests() {
        // Create an empty Page of AccessRequestDTO objects
        Page<AccessRequestDTO> emptyPage = new PageImpl<>(Collections.emptyList());

        // Mock the accessRequestService to return the empty Page
        when(accessRequestService.getAllActiveRequests(any(Pageable.class))).thenReturn(emptyPage);

        // Call the getAllActiveRequests method
        ResponseEntity<Object> responseEntity = accessRequestController.getAllActiveRequests(0, 10);

        // Assert the response status code
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        // Assert the response body to be the expected message
        assertEquals("No requests", responseEntity.getBody());
    }

    @Test
    void testGetAllActiveRequests_WithRequests() {
        // Create a list of AccessRequestDTO objects
        List<AccessRequestDTO> requestDTOList = Collections.singletonList(new AccessRequestDTO());

        // Create a Page object with the list of AccessRequestDTOs
        Page<AccessRequestDTO> requestPage = new PageImpl<>(requestDTOList);

        // Mock the accessRequestService to return the Page object
        when(accessRequestService.getAllActiveRequests(any(Pageable.class))).thenReturn(requestPage);

        // Call the getAllActiveRequests method
        ResponseEntity<Object> responseEntity = accessRequestController.getAllActiveRequests(0, 10);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<AccessRequestDTO> responseRequests = (List<AccessRequestDTO>) responseEntity.getBody();
        assertEquals(requestDTOList.size(), responseRequests.size());
        for (int i = 0; i < requestDTOList.size(); i++) {
            assertEquals(requestDTOList.get(i), responseRequests.get(i));
        }
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
        // Create a request ID and AccessRequestDTO
        Long requestId = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
        // Create a list of AccessResponseDTO objects
        List<AccessResponseDTO> accessResponseDTOList = Collections.singletonList(new AccessResponseDTO());

        // Mock the yourService to return the list of AccessResponseDTO objects
        when(accessRequestService.getUpdatedRequests(eq(requestId), eq(accessRequestDTO), any(Pageable.class)))
                .thenReturn(accessResponseDTOList);

        // Call the updateAccessRequest method
        ResponseEntity<Object> responseEntity = accessRequestController.updateAccessRequest(requestId, accessRequestDTO, 0, 10);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body
        List<AccessResponseDTO> responseBody = (List<AccessResponseDTO>) responseEntity.getBody();
        assertEquals(accessResponseDTOList, responseBody);
    }

    @Test
    void testUpdateAccessRequest_NoRequestsFound() {
        // Create a request ID and AccessRequestDTO
        Long requestId = 1L;
        AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

        // Mock the yourService to return an empty list
        when(accessRequestService.getUpdatedRequests(eq(requestId), eq(accessRequestDTO), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        // Call the updateAccessRequest method
        ResponseEntity<Object> responseEntity = accessRequestController.updateAccessRequest(requestId, accessRequestDTO, 0, 10);

        // Assert the response status code
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        // Assert the response body message
        assertEquals("No access requests found.", responseEntity.getBody());
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
        // Mock the accessRequestService to do nothing when clearAllNotifications is called
        doNothing().when(accessRequestService).clearAllNotifications();

        // Call the deleteAllNotifications method
        ResponseEntity<String> responseEntity = accessRequestController.deleteAllNotifications();

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body message (You may need to modify it based on the actual value in your code)
        assertEquals("All notifications have been soft-deleted", responseEntity.getBody());

        // Verify that clearAllNotifications was called
        verify(accessRequestService).clearAllNotifications();
    }



    @Test
    void testUpdateAccessRequest_Successs() {
        // Create a list of AccessResponseDTO objects
        List<AccessResponseDTO> responseDTOList = new ArrayList<>();
        responseDTOList.add(new AccessResponseDTO(/* your data */));

        // Mock the accessRequestService to return the list of AccessResponseDTO when getUpdatedRequests is called
        when(accessRequestService.getUpdatedRequests(anyLong(), any(AccessRequestDTO.class), any(Pageable.class))).thenReturn(responseDTOList);

        // Call the updateAccessRequest method
        ResponseEntity<Object> responseEntity = accessRequestController.updateAccessRequest(1L, new AccessRequestDTO(/* your data */), 0, 10);

        // Assert the response status code
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert the response body contains the expected data
        List<AccessResponseDTO> responseBody = (List<AccessResponseDTO>) responseEntity.getBody();
        assertEquals(responseDTOList, responseBody);
    }
}











