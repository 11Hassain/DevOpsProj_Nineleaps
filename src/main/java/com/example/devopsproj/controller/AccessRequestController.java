package com.example.devopsproj.controller;

import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.AccessResponseDTO;
import com.example.devopsproj.service.interfaces.AccessRequestService;
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
        AccessRequestDTO accessRequestDTO1 = accessRequestService.createRequest(accessRequestDTO);
        if (accessRequestDTO1 == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid RequestDTO");
        }
        return ResponseEntity.ok(accessRequestDTO1);
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
    public ResponseEntity<Object> getAllActiveRequests(){
        List<AccessRequestDTO>  accessRequestDTOList= accessRequestService.getAllActiveRequests();
        if (accessRequestDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
        }
        return ResponseEntity.ok(accessRequestDTOList);
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
    public ResponseEntity<Object> getAllRequests(){
        List<AccessRequestDTO>  accessRequestDTOList= accessRequestService.getAllRequests();
        if (accessRequestDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
        }
        return ResponseEntity.ok(accessRequestDTOList);
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
        @Valid @RequestBody AccessRequestDTO accessRequestDTO){
        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);
        if (accessResponseDTOList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else return ResponseEntity.ok(accessResponseDTOList);
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
    public ResponseEntity<Object> getUnreadPMRequestsNotification(@RequestParam("pmName") String pmName){
            List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMUnreadRequests(pmName);
            if (accessResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return ResponseEntity.ok(accessResponseDTOList);
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
    public ResponseEntity<Object> getPMRequestsNotification(@RequestParam("pmName") String pmName){

            List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getPMRequests(pmName);
            if (accessResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return ResponseEntity.ok(accessResponseDTOList);

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
            @RequestParam("accessRequestId") Long accessRequestId
    ){

            accessRequestService.setPMRequestsNotificationTrue(accessRequestId);
            return ResponseEntity.ok("Notification read");

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
    public ResponseEntity<Object> deleteAllNotifications(){

            accessRequestService.clearAllNotifications();
            return ResponseEntity.noContent().build();

    }

}
