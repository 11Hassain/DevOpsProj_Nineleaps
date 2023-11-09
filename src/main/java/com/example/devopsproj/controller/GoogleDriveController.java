package com.example.devopsproj.controller;

import com.example.devopsproj.constants.CommonConstants;
import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;

import com.example.devopsproj.service.interfaces.GoogleDriveService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.Optional;

@RestController
@RequestMapping("/api/v1/googledrive")
@RequiredArgsConstructor
public class GoogleDriveController {
    private final GoogleDriveService googleDriveService;


    /**
     * Create a Google Drive entry.
     *
     * @param googleDriveDTO The GoogleDriveDTO containing entry details.
     * @return ResponseEntity containing the created GoogleDriveDTO.
     */
    @PostMapping("/create")
    @ApiOperation("Create a Google Drive entry")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GoogleDriveDTO> createGoogleDrive(@RequestBody GoogleDriveDTO googleDriveDTO) {
        GoogleDriveDTO createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoogleDrive);
    }


    /**
     * Get a list of all Google Drive entries.
     *
     * @param page The page number for pagination.
     * @param size The number of entries per page.
     * @return ResponseEntity containing a list of GoogleDriveDTOs.
     */
    @GetMapping("/getAllGoogleDrives")
    @ApiOperation("Get a list of all Google Drive entries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllGoogleDrives(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GoogleDriveDTO> googleDrivePage = googleDriveService.getAllGoogleDrives(pageable);

        if (googleDrivePage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonConstants.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).body(googleDrivePage.getContent());
    }



    /**
     * Get a Google Drive entry by ID.
     *
     * @param driveId The ID of the Google Drive entry to retrieve.
     * @return ResponseEntity containing the GoogleDriveDTO or indicating that it was not found.
     */
    @GetMapping("/getGoogleDriveById/{driveId}")
    @ApiOperation("Get a Google Drive entry by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveById(@PathVariable Long driveId) {
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);

        return optionalGoogleDriveDTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Delete a Google Drive entry by ID.
     *
     * @param driveId The ID of the Google Drive entry to delete.
     * @return ResponseEntity indicating the status of the deletion.
     */
    @DeleteMapping("/deleteGoogleDriveById/{driveId}")
    @ApiOperation("Soft delete a Google Drive entry by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteGoogleDriveById(@PathVariable Long driveId) {
        return googleDriveService.deleteGoogleDriveById(driveId);
    }




    /**
     * Get a Google Drive entry by project ID.
     *
     * @param projectId The ID of the project to retrieve the Google Drive entry for.
     * @return ResponseEntity containing the GoogleDriveDTO or indicating that it was not found.
     */
    @GetMapping("/getGoogleDriveByProjectId/{projectId}")
    @ApiOperation("Get a Google Drive entry by project ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GoogleDriveDTO> getGoogleDriveByProjectId(@PathVariable Long projectId) {
        return googleDriveService.getGoogleDriveByProjectId(projectId);
    }


}
