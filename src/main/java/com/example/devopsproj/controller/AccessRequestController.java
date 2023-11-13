package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The AccessRequestController class is responsible for handling RESTful API endpoints related to access requests.
 * It provides endpoints for creating, updating, and retrieving access requests, as well as managing notifications for
 * project managers (PMs).
 * .
 * This controller integrates with the accessRequestService to perform operations related to access requests and
 * uses JWT tokens for user authentication.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/request")
@Validated
@RequiredArgsConstructor
public class AccessRequestController {

    private final AccessRequestService accessRequestService;
    private static final Logger logger = LoggerFactory.getLogger(AccessRequestController.class);


    /**
     * Create an access request.
     *
     * @param accessRequestDTO The AccessRequestDTO containing the request data.
     * @return ResponseEntity with the result of the request creation.
     */
    @PostMapping("/")
    @Operation(
            description = "Create Access Request",
            responses = {
            @ApiResponse(responseCode = "200", description = "Request made successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createAccessRequest(@Valid @RequestBody AccessRequestDTO accessRequestDTO){
        logger.info("Received a request to create an access request");

        AccessRequestDTO accessRequestDTO1 = accessRequestService.createRequest(accessRequestDTO);
        if (accessRequestDTO1 == null){
            logger.error("Invalid AccessRequestDTO received. Failed to create the request.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid RequestDTO");
        }

        logger.info("Access request created successfully.");
        return ResponseEntity.ok(accessRequestDTO1);
    }

    /**
     * Get all active access requests.
     *
     * @return ResponseEntity with the list of active access requests.
     */
    @GetMapping("/allActive")
    @Operation(
            description = "Get All Active Requests",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Active requests retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No active requests"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllActiveRequests() {
        logger.info("Received a request to retrieve all active access requests.");

        List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllActiveRequests();
        if (accessRequestDTOList.isEmpty()) {
            logger.info("No active access requests found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No active requests");
        }

        logger.info("Active access requests retrieved successfully.");
        return ResponseEntity.ok(accessRequestDTOList);
    }

    /**
     * Get all access requests.
     *
     * @return ResponseEntity with the list of access requests.
     */
    @GetMapping("/all")
    @Operation(
            description = "Get All Requests",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No requests"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests() {
        logger.info("Received a request to retrieve all access requests.");

        List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllRequests();
        if (accessRequestDTOList.isEmpty()) {
            logger.info("No access requests found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
        }

        logger.info("Access requests retrieved successfully.");
        return ResponseEntity.ok(accessRequestDTOList);
    }

    /**
     * Update an access request.
     *
     * @param requestId        The ID of the access request to be updated.
     * @param accessRequestDTO The AccessRequestDTO containing the updated request data.
     * @return ResponseEntity with the result of the request update.
     */
    @PutMapping("/update/{accessRequestId}")
    @Operation(
            description = "Update Access Request",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access request updated successfully"),
                    @ApiResponse(responseCode = "204", description = "No updated requests"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateAccessRequest(
            @PathVariable("accessRequestId") Long requestId,
            @Valid @RequestBody AccessRequestDTO accessRequestDTO) {
        logger.info("Received a request to update an access request with ID: {}", requestId);

        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);
        if (accessResponseDTOList.isEmpty()) {
            logger.info("No access requests were updated for ID: {}", requestId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("Access requests updated successfully for ID: {}", requestId);
        return ResponseEntity.ok(accessResponseDTOList);
    }

    /**
     * Get unread PM requests notifications for a specific PM (Project Manager).
     *
     * @param pmName The name of the Project Manager for whom to retrieve unread notifications.
     * @return ResponseEntity with the list of unread PM requests notifications.
     */
    @GetMapping("/unread/PM")
    @Operation(
            description = "Get Unread PM Requests Notification",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unread PM requests retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No unread PM requests"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUnreadPMRequestsNotification(@RequestParam("pmName") String pmName) {
        logger.info("Received a request to retrieve unread PM requests notifications for PM: {}", pmName);

        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMUnreadRequests(pmName);
        if (accessResponseDTOList.isEmpty()) {
            logger.info("No unread PM requests notifications found for PM: {}", pmName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("Unread PM requests notifications retrieved successfully for PM: {}", pmName);
        return ResponseEntity.ok(accessResponseDTOList);
    }

    /**
     * Get all PM requests notifications for a specific PM (Project Manager).
     *
     * @param pmName The name of the Project Manager for whom to retrieve PM requests notifications.
     * @return ResponseEntity with the list of PM requests notifications.
     */
    @GetMapping("/all/PM")
    @Operation(
            description = "Get All PM Requests Notification",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All PM requests retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No PM requests"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getPMRequestsNotification(@RequestParam("pmName") String pmName) {
        logger.info("Received a request to retrieve all PM requests notifications for PM: {}", pmName);

        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMRequests(pmName);
        if (accessResponseDTOList.isEmpty()) {
            logger.info("No PM requests notifications found for PM: {}", pmName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.info("All PM requests notifications retrieved successfully for PM: {}", pmName);
        return ResponseEntity.ok(accessResponseDTOList);
    }

    /**
     * Set PM requests notification to true for a specific access request.
     *
     * @param accessRequestId The ID of the access request to update the notification status.
     * @return ResponseEntity indicating that the notification has been read.
     */
    @PutMapping("/notifiedPM")
    @Operation(
            description = "Set PM Requests Notification to True",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notification read"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> setPMRequestsNotificationToTrue(
            @RequestParam("accessRequestId") Long accessRequestId
    ) {
        logger.info("Received a request to set PM requests notification to true for Access Request ID: {}", accessRequestId);

        // Set PM requests notification to true
        accessRequestService.setPMRequestsNotificationTrue(accessRequestId);

        logger.info("PM requests notification set to true for Access Request ID: {}", accessRequestId);
        return ResponseEntity.ok("Notification read");
    }

    /**
     * Clear all notifications.
     *
     * @return ResponseEntity indicating that all notifications have been cleared.
     */
    @DeleteMapping("/clearAll")
    @Operation(
            description = "Clear All Notifications",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All notifications cleared"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> deleteAllNotifications() {
        logger.info("Received a request to clear all notifications.");

        // Clear all notifications
        accessRequestService.clearAllNotifications();

        logger.info("All notifications have been cleared.");
        return ResponseEntity.noContent().build();
    }

}
