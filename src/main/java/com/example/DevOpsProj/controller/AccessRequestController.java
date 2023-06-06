package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.requestDto.AccessRequestDTO;
import com.example.DevOpsProj.dto.responseDto.AccessResponseDTO;
import com.example.DevOpsProj.model.AccessRequest;
import com.example.DevOpsProj.service.AccessRequestService;
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

    @PostMapping("/")
    public ResponseEntity<Object> createAccessRequest(@RequestBody AccessRequestDTO accessRequestDTO){
        AccessRequestDTO accessRequestDTO1 = accessRequestService.createRequest(accessRequestDTO);
        return ResponseEntity.ok("Request made successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<AccessResponseDTO>> getAllActiveRequests(){
        List<AccessResponseDTO>  accessResponseDTOList = accessRequestService.getAllRequests();
//        List<AccessRequest> accessRequestList = accessRequestService.getAllRequests();
        return ResponseEntity.ok(accessResponseDTOList);
    }

    @PutMapping("/update/{accessRequestId}")
    public ResponseEntity<List<AccessResponseDTO>> updateAccessRequest(@PathVariable("accessRequestId") Long requestId, @RequestBody AccessRequestDTO accessRequestDTO){
        List<AccessResponseDTO> accessResponseDTOList = accessRequestService.getUpdatedRequests(requestId, accessRequestDTO);
        if (accessResponseDTOList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else return ResponseEntity.ok(accessResponseDTOList);
    }

}
