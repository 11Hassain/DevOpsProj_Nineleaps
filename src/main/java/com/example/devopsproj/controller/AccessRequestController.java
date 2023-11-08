package com.example.devopsproj.controller;

import com.example.devopsproj.constants.AccessRequestConstants;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/request")
public class AccessRequestController {

    private final AccessRequestService accessRequestService;


    // Create a new access request.
    @PostMapping("/create")
    @ApiOperation("Create an Access Request")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createAccessRequest(@RequestBody AccessRequestDTO accessRequestDTO) {
        Optional<AccessRequestDTO> createdRequest = Optional.ofNullable(accessRequestService.createRequest(accessRequestDTO));
        return createdRequest.map(request -> ResponseEntity.ok((Object) AccessRequestConstants.REQUEST_SUCCESS))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) AccessRequestConstants.REQUEST_FAILURE));
    }
    // Get all active access requests.
    @GetMapping("/allActive")
    @ApiOperation("Get all active access requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllActiveRequests(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccessRequestDTO> accessRequestDTOPage = accessRequestService.getAllActiveRequests(pageable);

        List<AccessRequestDTO> accessRequestDTOList = accessRequestDTOPage.getContent();

        if (accessRequestDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(AccessRequestConstants.NO_REQUESTS);
        }

        return ResponseEntity.status(HttpStatus.OK).body(accessRequestDTOList);
    }

    // Get all access requests.
    @GetMapping("/all")
    @ApiOperation("Get all access requests")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequests() {
        List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllRequests();

        return ResponseEntity.status(accessRequestDTOList.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(accessRequestDTOList.isEmpty() ? AccessRequestConstants.NO_REQUESTS : accessRequestDTOList);
    }
    // Update an access request.
    @PutMapping("/update/{accessRequestId}")
    @ApiOperation("Update an access request")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateAccessRequest(
            @PathVariable("accessRequestId") Long requestId,
            @RequestBody AccessRequestDTO accessRequestDTO,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO, pageable);

        if (accessResponseDTOList != null && !accessResponseDTOList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(accessResponseDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No access requests found.");
        }
    }

    // Get unread PM requests notification.
    @GetMapping("/unread/PM")
    @ApiOperation("Get unread PM requests notification")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<Object> getUnreadPMRequestsNotification(
            @RequestParam("pmName") String pmName) {
        List<AccessResponseDTO> unreadRequests = accessRequestService.getPMUnreadRequests(pmName);

        if (unreadRequests.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(unreadRequests);
        }
    }
    // Get all PM requests notification.
    @GetMapping("/all/PM")
    @ApiOperation("Get all PM requests notification")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getPMRequestsNotification(
            @RequestParam("pmName") String pmName) {
        List<AccessResponseDTO> result = accessRequestService.getPMRequests(pmName);
        return ResponseEntity.ok(result);
    }
    // Set PM requests notification to true.
    @PutMapping("/notifiedPM")
    @ApiOperation("Set PM requests notification to true")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> setPMRequestsNotificationToTrue(
            @RequestParam("accessRequestId") Long accessRequestId) {
        accessRequestService.setPMRequestsNotificationTrue(accessRequestId);
        return ResponseEntity.ok(AccessRequestConstants.SET_NOTIFICATION_SUCCESS);
    }

    // Soft delete all notifications.
    @DeleteMapping("/clearAll")
    @ApiOperation("Soft delete all notifications")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteAllNotifications() {
        accessRequestService.clearAllNotifications();
        return ResponseEntity.ok(AccessRequestConstants.DELETE_NOTIFICATION_SUCCESS);
    }

}

