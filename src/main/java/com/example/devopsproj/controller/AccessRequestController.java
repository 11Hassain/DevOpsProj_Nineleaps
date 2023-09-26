package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
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

    private final JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

        // Create a new access request.
        @PostMapping("/create")
        public ResponseEntity<Object> createAccessRequest(@RequestBody AccessRequestDTO accessRequestDTO, @RequestHeader("AccessToken") String accessToken) {
            Optional<AccessRequestDTO> createdRequest = accessRequestService.createRequest(accessRequestDTO, accessToken);

            return createdRequest.map(request -> ResponseEntity.ok((Object)"Request made successfully"))
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((Object)"Invalid token"));
        }



    // Get a list of all active access requests.
    @GetMapping("/allActive")
    public ResponseEntity<Object> getAllActiveRequests(@RequestHeader("AccessToken") String accessToken) {
        List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllActiveRequests();

        return ResponseEntity.status(accessRequestDTOList.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(accessRequestDTOList.isEmpty() ? "No requests" : accessRequestDTOList);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("AccessToken") String accessToken) {
        List<AccessRequestDTO> accessRequestDTOList = accessRequestService.getAllRequests();

        return ResponseEntity.status(accessRequestDTOList.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(accessRequestDTOList.isEmpty() ? "No requests" : accessRequestDTOList);
    }


    @PutMapping("/update/{accessRequestId}")
    public ResponseEntity<Object> updateAccessRequest(
            @PathVariable("accessRequestId") Long requestId,
            @RequestBody AccessRequestDTO accessRequestDTO,
            @RequestHeader("AccessToken") String accessToken) {
        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);

        return ResponseEntity.status(accessResponseDTOList.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(accessResponseDTOList.isEmpty() ? "" : accessResponseDTOList);
    }



    // Get a list of unread access requests for a project manager by their name.

    @GetMapping("/unread/PM")
    public ResponseEntity<Object> getUnreadPMRequestsNotification(
            @RequestParam("pmName") String pmName,
            @RequestHeader("AccessToken") String accessToken) {
        if (!jwtService.isTokenTrue(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        return ResponseEntity.ok(accessRequestService.getPMUnreadRequests(pmName));
    }

    @GetMapping("/all/PM")
    public ResponseEntity<Object> getPMRequestsNotification(
            @RequestParam("pmName") String pmName,
            @RequestHeader("AccessToken") String accessToken) {
        if (!jwtService.isTokenTrue(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }

        return ResponseEntity.ok(accessRequestService.getPMRequests(pmName));
    }

    @PutMapping("/notifiedPM")
    public ResponseEntity<String> setPMRequestsNotificationToTrue(
            @RequestParam("accessRequestId") Long accessRequestId,
            @RequestHeader("AccessToken") String accessToken) {
        if (jwtService.isTokenTrue(accessToken)) {
            accessRequestService.setPMRequestsNotificationTrue(accessRequestId);
            return ResponseEntity.ok("Notification read");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
    }

    @DeleteMapping("/clearAll")
    public ResponseEntity<String> deleteAllNotifications(@RequestHeader("AccessToken") String accessToken) {
        if (jwtService.isTokenTrue(accessToken)) {
            accessRequestService.clearAllNotifications();
            return ResponseEntity.ok("All notifications cleared");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
    }
}

