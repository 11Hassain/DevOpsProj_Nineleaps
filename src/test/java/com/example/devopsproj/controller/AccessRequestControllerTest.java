package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.implementations.AccessRequestServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AccessRequestControllerTest {

    @InjectMocks
    private AccessRequestController accessRequestController;
    @Mock
    private AccessRequestServiceImpl accessRequestService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Nested
    class CreateAccessRequestTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCreateAccessRequest_ValidToken_Success(){
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            accessRequestDTO.setPmName("John Doe");

            when(accessRequestService.createRequest(accessRequestDTO)).thenReturn(accessRequestDTO);

            ResponseEntity<Object> response = accessRequestController.createAccessRequest(accessRequestDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accessRequestDTO, response.getBody());
        }

        @Test
        @DisplayName("Testing bad request case with valid token")
        void testCreateAccessRequest_ValidToken_BadRequest(){
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();
            String badRequest = "Invalid RequestDTO";

            when(accessRequestService.createRequest(accessRequestDTO)).thenReturn(null);

            ResponseEntity<Object> response = accessRequestController.createAccessRequest(accessRequestDTO);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(badRequest, response.getBody());
        }
    }

    @Nested
    class GetAllActiveRequestsTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllActiveRequests_ValidToken_Success(){
            List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();
            AccessRequestDTO a1 = new AccessRequestDTO();
            AccessRequestDTO a2 = new AccessRequestDTO();
            AccessRequestDTO a3 = new AccessRequestDTO();
            accessRequestDTOList.add(a1);
            accessRequestDTOList.add(a2);
            accessRequestDTOList.add(a3);

            when(accessRequestService.getAllActiveRequests()).thenReturn(accessRequestDTOList);

            ResponseEntity<Object> response = accessRequestController.getAllActiveRequests();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accessRequestDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing no content case with valid token")
        void testGetAllActiveRequests_ValidToken_NoContent(){
            List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

            when(accessRequestService.getAllActiveRequests()).thenReturn(accessRequestDTOList);

            ResponseEntity<Object> response = accessRequestController.getAllActiveRequests();

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(accessRequestDTOList.isEmpty());
        }
    }

    @Nested
    class GetAllRequestsTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllRequests_ValidToken_Success(){
            List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();
            AccessRequestDTO a1 = new AccessRequestDTO();
            AccessRequestDTO a2 = new AccessRequestDTO();
            accessRequestDTOList.add(a1);
            accessRequestDTOList.add(a2);

            when(accessRequestService.getAllRequests()).thenReturn(accessRequestDTOList);

            ResponseEntity<Object> response = accessRequestController.getAllRequests();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accessRequestDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing no content case with valid token")
        void testGetAllRequests_ValidToken_NoContent(){
            List<AccessRequestDTO> accessRequestDTOList = new ArrayList<>();

            when(accessRequestService.getAllRequests()).thenReturn(accessRequestDTOList);

            ResponseEntity<Object> response = accessRequestController.getAllRequests();

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(accessRequestDTOList.isEmpty());
        }
    }

    @Nested
    class UpdateAccessRequestTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testUpdateAccessRequest_ValidToken_Success(){
            Long requestId = 1L;
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

            List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
            AccessResponseDTO a1 = new AccessResponseDTO();
            AccessResponseDTO a2 = new AccessResponseDTO();
            accessResponseDTOList.add(a1);
            accessResponseDTOList.add(a2);

            when(accessRequestService.getUpdatedRequests(requestId,accessRequestDTO)).thenReturn(accessResponseDTOList);

            ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, accessRequestDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accessResponseDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing no content case with valid token")
        void testUpdateAccessRequest_ValidToken_NoContent(){
            Long requestId = 1L;
            AccessRequestDTO accessRequestDTO = new AccessRequestDTO();

            List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

            when(accessRequestService.getUpdatedRequests(requestId,accessRequestDTO)).thenReturn(accessResponseDTOList);

            ResponseEntity<Object> response = accessRequestController.updateAccessRequest(requestId, accessRequestDTO);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(accessResponseDTOList.isEmpty());
        }
    }

    @Nested
    class GetUnreadPMRequestsNotificationTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetUnreadPMRequestsNotification_ValidToken_Success(){
            String pmName = "John Doe";

            List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
            AccessResponseDTO a1 = new AccessResponseDTO();
            accessResponseDTOList.add(a1);

            when(accessRequestService.getPMUnreadRequests(pmName)).thenReturn(accessResponseDTOList);

            ResponseEntity<Object> response = accessRequestController.getUnreadPMRequestsNotification(pmName);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accessResponseDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing no content case with valid token")
        void testGetUnreadPMRequestsNotification_ValidToken_NoContent(){
            String pmName = "John Doe";

            List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

            when(accessRequestService.getPMUnreadRequests(pmName)).thenReturn(accessResponseDTOList);

            ResponseEntity<Object> response = accessRequestController.getUnreadPMRequestsNotification(pmName);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(accessResponseDTOList.isEmpty());
        }
    }

    @Nested
    class GetPMRequestsNotificationTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetPMRequestsNotification_ValidToken_Success(){
            String pmName = "John Doe";

            List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();
            AccessResponseDTO a1 = new AccessResponseDTO();
            accessResponseDTOList.add(a1);

            when(accessRequestService.getPMRequests(pmName)).thenReturn(accessResponseDTOList);

            ResponseEntity<Object> response = accessRequestController.getPMRequestsNotification(pmName);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(accessResponseDTOList, response.getBody());
        }

        @Test
        @DisplayName("Testing no content case with valid token")
        void testGetPMRequestsNotification_ValidToken_NoContent(){
            String pmName = "John Doe";

            List<AccessResponseDTO> accessResponseDTOList = new ArrayList<>();

            when(accessRequestService.getPMRequests(pmName)).thenReturn(accessResponseDTOList);

            ResponseEntity<Object> response = accessRequestController.getPMRequestsNotification(pmName);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(accessResponseDTOList.isEmpty());
        }

    }

    @Test
    void testSetPMRequestsNotificationToTrue_ValidToken_Success(){
        Long requestId = 1L;
        String read = "Notification read";

        ResponseEntity<String> response = accessRequestController.setPMRequestsNotificationToTrue(requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(read, response.getBody());
    }


    @Test
    void testDeleteAllNotifications_ValidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(true);

        ResponseEntity<Object> response = accessRequestController.deleteAllNotifications();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}
