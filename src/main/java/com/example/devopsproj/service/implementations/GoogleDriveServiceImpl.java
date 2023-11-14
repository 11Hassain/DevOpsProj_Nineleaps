package com.example.devopsproj.service.implementations;

import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import com.example.devopsproj.utils.DTOModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The `GoogleDriveServiceImpl` class provides services for managing Google Drive links associated with projects.
 * It includes methods for creating, retrieving, listing, and deleting Google Drive links.
 *
 * @version 2.0
 */

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    @Autowired
    public GoogleDriveServiceImpl(GoogleDriveRepository googleDriveRepository) {
        this.googleDriveRepository = googleDriveRepository;
    }

    private final GoogleDriveRepository googleDriveRepository;
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveServiceImpl.class);

    /**
     * Creates a new Google Drive entry based on the provided GoogleDriveDTO.
     *
     * @param googleDriveDTO The DTO containing information for creating the Google Drive entry.
     * @return The created Google Drive entry.
     */
    @Override
    public GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        logger.info("Creating Google Drive entry for project: {}", googleDriveDTO.getProjectDTO().getProjectName());

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(DTOModelMapper.mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());

        logger.info("Google Drive entry created successfully. Drive ID: {}", googleDrive.getDriveId());

        return googleDriveRepository.save(googleDrive);
    }

    /**
     * Retrieves all Google Drive entries.
     *
     * @return A list of all Google Drive entries.
     */
    @Override
    public List<GoogleDrive> getAllGoogleDrives() {
        logger.info("Fetching all Google Drive entries");
        return googleDriveRepository.findAll();
    }

    /**
     * Retrieves a Google Drive entry by its ID.
     *
     * @param driveId The ID of the Google Drive entry to retrieve.
     * @return An optional containing the Google Drive entry DTO if found.
     */
    @Override
    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        logger.info("Fetching Google Drive entry by ID: {}", driveId);

        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        return optionalGoogleDrive.map(this::mapGoogleDriveToDTO);
    }

    /**
     * Deletes a Google Drive entry by its ID.
     *
     * @param driveId The ID of the Google Drive entry to delete.
     * @return True if the Google Drive entry was deleted successfully, false otherwise.
     */
    @Override
    public boolean deleteGoogleDriveById(Long driveId) {
        logger.info("Deleting Google Drive entry by ID: {}", driveId);

        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            googleDriveRepository.deleteById(driveId);
            logger.info("Google Drive entry with ID {} deleted successfully.", driveId);
            return true;
        } else {
            logger.warn("Google Drive entry with ID {} not found. Deletion failed.", driveId);
            return false;
        }
    }

    /**
     * Retrieves a Google Drive entry by its associated project ID.
     *
     * @param projectId The ID of the project associated with the Google Drive entry.
     * @return An optional containing the Google Drive entry if found.
     */
    @Override
    public Optional<GoogleDrive> getGoogleDriveByProjectId(Long projectId) {
        logger.info("Fetching Google Drive entry by project ID: {}", projectId);
        return googleDriveRepository.findGoogleDriveByProjectId(projectId);
    }

    public GoogleDriveDTO mapGoogleDriveToDTO(GoogleDrive googleDrive) {
        return new GoogleDriveDTO(DTOModelMapper.mapProjectToProjectDTO(googleDrive.getProject()), googleDrive.getDriveLink(), googleDrive.getDriveId());
    }
}
