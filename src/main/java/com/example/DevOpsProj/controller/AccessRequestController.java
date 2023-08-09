package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.requestDto.AccessRequestDTO;
import com.example.DevOpsProj.dto.responseDto.AccessResponseDTO;
import com.example.DevOpsProj.model.AccessRequest;
import com.example.DevOpsProj.service.AccessRequestService;
import com.example.DevOpsProj.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
public class AccessRequestController {

    @Autowired
    private AccessRequestService accessRequestService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/")
    public ResponseEntity<Object> createAccessRequest(@RequestBody AccessRequestDTO accessRequestDTO, @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            AccessRequestDTO accessRequestDTO1 = accessRequestService.createRequest(accessRequestDTO);
            return ResponseEntity.ok("Request made successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/allActive")
    public ResponseEntity<Object> getAllActiveRequests(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessRequestDTO>  accessRequestDTOList= accessRequestService.getAllActiveRequests();
            if (accessRequestDTOList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
            }
            return ResponseEntity.ok(accessRequestDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessRequestDTO>  accessRequestDTOList= accessRequestService.getAllRequests();
            if (accessRequestDTOList.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No requests");
            }
            return ResponseEntity.ok(accessRequestDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @PutMapping("/update/{accessRequestId}")
    public ResponseEntity<Object> updateAccessRequest(
            @PathVariable("accessRequestId") Long requestId,
            @RequestBody AccessRequestDTO accessRequestDTO,
            @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);
            if (accessResponseDTOList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else return ResponseEntity.ok(accessResponseDTOList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/unread/PM")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/all/PM")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @PutMapping("/notifiedPM")
    public ResponseEntity<String> setPMRequestsNotificationToTrue(
            @RequestParam("accessRequestId") Long accessRequestId,
            @RequestHeader("AccessToken") String accessToken
    ){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid){
            accessRequestService.setPMRequestsNotificationTrue(accessRequestId);
            return ResponseEntity.ok("Notification read");
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Something went wrong");
        }
    }

    @DeleteMapping("/clearAll")
    public ResponseEntity<String> deleteAllNotifications(@RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if(isTokenValid){
            accessRequestService.clearAllNotifications();
            return ResponseEntity.ok("All notifications cleared");
        }else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Something went wrong");
        }
    }

}
