package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestDto.AccessRequestDTO;
import com.example.devopsproj.dto.responseDto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/request")
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    private final JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

        // Create a new access request.
        @PostMapping("/create")
        public ResponseEntity<Object> createAccessRequest(@RequestBody AccessRequestDTO accessRequestDTO, @RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Return a success response.
                return ResponseEntity.ok("Request made successfully");
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of all active access requests.
        @GetMapping("/allActive")
        public ResponseEntity<Object> getAllActiveRequests(@RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Get the list of all active access requests.
                List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllActiveRequests();
                if (accessRequestDTOList.isEmpty()) {
                    // Return a no content response if there are no requests.
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
                }
                // Return the list of access request DTOs.
                return ResponseEntity.ok(accessRequestDTOList);
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of all access requests.
        @GetMapping("/all")
        public ResponseEntity<Object> getAllRequests(@RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Get the list of all access requests.
                List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllRequests();
                if (accessRequestDTOList.isEmpty()) {
                    // Return a no content response if there are no requests.
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
                }
                // Return the list of access request DTOs.
                return ResponseEntity.ok(accessRequestDTOList);
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Update an access request based on the request ID and request DTO.
        @PutMapping("/update/{accessRequestId}")
        public ResponseEntity<Object> updateAccessRequest(
                @PathVariable("accessRequestId") Long requestId,
                @RequestBody AccessRequestDTO accessRequestDTO,
                @RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Get a list of updated access requests.
                List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);
                if (accessResponseDTOList.isEmpty()) {
                    // Return a no content response if there are no updated requests.
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    // Return the list of updated access request DTOs.
                    return ResponseEntity.ok(accessResponseDTOList);
                }
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of unread access requests for a project manager by their name.
        @GetMapping("/unread/PM")
        public ResponseEntity<Object> getUnreadPMRequestsNotification(@RequestParam("pmName") String pmName,
                                                                      @RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Get a list of unread access request DTOs for a project manager.
                List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMUnreadRequests(pmName);
                if (accessResponseDTOList.isEmpty()) {
                    // Return a no content response if there are no unread requests.
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    // Return the list of unread access request DTOs.
                    return ResponseEntity.ok(accessResponseDTOList);
                }
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Get a list of access requests for a project manager by their name.
        @GetMapping("/all/PM")
        public ResponseEntity<Object> getPMRequestsNotification(@RequestParam("pmName") String pmName,
                                                                @RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Get a list of access request DTOs for a project manager.
                List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMRequests(pmName);
                if (accessResponseDTOList.isEmpty()) {
                    // Return a no content response if there are no requests.
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    // Return the list of access request DTOs.
                    return ResponseEntity.ok(accessResponseDTOList);
                }
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Set the notification status of an access request to true.
        @PutMapping("/notifiedPM")
        public ResponseEntity<String> setPMRequestsNotificationToTrue(
                @RequestParam("accessRequestId") Long accessRequestId,
                @RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Set the notification status of the access request to true.
                accessRequestService.setPMRequestsNotificationTrue(accessRequestId);
                return ResponseEntity.ok("Notification read");
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }

        // Delete all notifications for access requests.
        @DeleteMapping("/clearAll")
        public ResponseEntity<String> deleteAllNotifications(@RequestHeader("AccessToken") String accessToken) {
            // Check if the access token is valid.
            boolean isTokenValid = jwtService.isTokenTrue(accessToken);
            if (isTokenValid) {
                // Clear all notifications for access requests.
                accessRequestService.clearAllNotifications();
                return ResponseEntity.ok("All notifications cleared");
            } else {
                // Return an unauthorized response if the token is invalid.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
            }
        }
    }
