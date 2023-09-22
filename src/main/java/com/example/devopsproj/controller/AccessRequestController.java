package com.example.devopsproj.controller;

import com.example.devopsproj.service.AccessRequestService;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.dto.requestDto.AccessRequestDTO;
import com.example.devopsproj.dto.responseDto.AccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/request")
@Validated
public class AccessRequestController {

    @Autowired
    private AccessRequestService accessRequestService;
    @Autowired
    private JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/")
    @Operation(
            description = "Create Access Request",
            responses = {
            @ApiResponse(responseCode = "200", description = "Request made successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createAccessRequest(@Valid @RequestBody AccessRequestDTO accessRequestDTO, @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok("Request made successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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
    public ResponseEntity<Object> getAllActiveRequests(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessRequestDTO>  accessRequestDTOList= accessRequestService.getAllActiveRequests();
            if (accessRequestDTOList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
            }
            return ResponseEntity.ok(accessRequestDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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
    public ResponseEntity<Object> getAllRequests(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessRequestDTO>  accessRequestDTOList= accessRequestService.getAllRequests();
            if (accessRequestDTOList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
            }
            return ResponseEntity.ok(accessRequestDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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
            @Valid @RequestBody AccessRequestDTO accessRequestDTO,
            @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);
            if (accessResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return ResponseEntity.ok(accessResponseDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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
    public ResponseEntity<Object> getUnreadPMRequestsNotification(@RequestParam("pmName") String pmName,
                                                @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMUnreadRequests(pmName);
            if (accessResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return ResponseEntity.ok(accessResponseDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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
    public ResponseEntity<Object> getPMRequestsNotification(@RequestParam("pmName") String pmName,
                                                            @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMRequests(pmName);
            if (accessResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return ResponseEntity.ok(accessResponseDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

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
            @RequestParam("accessRequestId") Long accessRequestId,
            @RequestHeader("AccessToken") String accessToken
    ){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid){
            accessRequestService.setPMRequestsNotificationTrue(accessRequestId);
            return ResponseEntity.ok("Notification read");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @DeleteMapping("/clearAll")
    @Operation(
            description = "Clear All Notifications",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All notifications cleared"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteAllNotifications(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if(isTokenValid){
            accessRequestService.clearAllNotifications();
            return ResponseEntity.ok("All notifications cleared");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

}
