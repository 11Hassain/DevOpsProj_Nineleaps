package com.example.devopsproj.controller;

import com.example.devopsproj.service.implementations.AccessRequestServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * This controller integrates with the AccessRequestServiceImpl to perform operations related to access requests and
 * uses JWT tokens for user authentication.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/request")
@Validated
@RequiredArgsConstructor
public class AccessRequestController {

    private final AccessRequestServiceImpl accessRequestServiceImpl;
    private final JwtServiceImpl jwtServiceImpl;

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/")
    @Operation(
            description = "Create Access Request",
            responses = {
            @ApiResponse(responseCode = "200", description = "Request made successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createAccessRequest(@Valid @RequestBody AccessRequestDTO accessRequestDTO, @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            AccessRequestDTO accessRequestDTO1 = accessRequestServiceImpl.createRequest(accessRequestDTO);
            if (accessRequestDTO1 == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid RequestDTO");
            }
            return ResponseEntity.ok(accessRequestDTO1);
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessRequestDTO>  accessRequestDTOList= accessRequestServiceImpl.getAllActiveRequests();
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessRequestDTO>  accessRequestDTOList= accessRequestServiceImpl.getAllRequests();
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestServiceImpl.getUpdatedRequests(requestId, accessRequestDTO);
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestServiceImpl.getPMUnreadRequests(pmName);
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestServiceImpl.getPMRequests(pmName);
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
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if (isTokenValid){
            accessRequestServiceImpl.setPMRequestsNotificationTrue(accessRequestId);
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
    public ResponseEntity<Object> deleteAllNotifications(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtServiceImpl.isTokenTrue(accessToken);
        if(isTokenValid){
            accessRequestServiceImpl.clearAllNotifications();
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

}
