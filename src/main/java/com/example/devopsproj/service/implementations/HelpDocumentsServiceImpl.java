package com.example.devopsproj.service.implementations;

import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The `HelpDocumentsServiceImpl` class provides services for managing help documents associated with projects.
 * It includes methods for uploading, retrieving, listing, and deleting help documents.
 *
 * @version 2.0
 */

@Service
public class HelpDocumentsServiceImpl implements HelpDocumentsService {

    @Autowired
    public HelpDocumentsServiceImpl(ProjectRepository projectRepository, HelpDocumentsRepository helpDocumentsRepository) {
        this.projectRepository = projectRepository;
        this.helpDocumentsRepository = helpDocumentsRepository;
    }

    private final ProjectRepository projectRepository;
    private final HelpDocumentsRepository helpDocumentsRepository;
    private static final Logger logger = LoggerFactory.getLogger(HelpDocumentsServiceImpl.class);

    /**
     * Uploads a file for a specific project.
     *
     * @param projectId      The ID of the project.
     * @param projectFile    The file to be uploaded.
     * @param fileExtension  The extension of the file.
     * @return A ResponseEntity indicating the success or failure of the file upload.
     * @throws IOException If an I/O error occurs during file upload.
     */
    @Override
    public ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException {
        logger.info("Uploading file for project with ID: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        HelpDocuments helpDocuments = new HelpDocuments();
        saveFile(helpDocuments, projectFile,fileExtension);
        helpDocuments.setProject(project);
        helpDocumentsRepository.save(helpDocuments);

        logger.info("File uploaded successfully for project with ID: {}", projectId);

        return ResponseEntity.ok("File uploaded successfully");
    }

    /**
     * Saves the contents of a help document file to a `HelpDocuments` entity.
     *
     * @param helpDocuments The HelpDocuments entity to which the file contents will be saved.
     * @param file          The file to be saved.
     * @param fileExtension The extension of the file.
     * @throws IOException If an I/O error occurs during file saving.
     */
    @Override
    public void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException {
        logger.info("Saving file contents to HelpDocuments entity");

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            logger.info("File is not empty");
            if (originalFilename != null) {
                helpDocuments.setFileName(originalFilename);
                helpDocuments.setData(file.getBytes());
                helpDocuments.setFileExtension(fileExtension);

                logger.info("File is not empty and original file name is not null");
            }
        } else {
            // Handle the case where the file is empty or null
            helpDocuments.setFileName(null);
            helpDocuments.setData(new byte[0]);
            helpDocuments.setFileExtension(fileExtension);

            logger.info("File is empty");
        }
    }

    /**
     * Retrieves the file extension from a given MultipartFile.
     *
     * @param file The MultipartFile from which to extract the file extension.
     * @return The file extension or null if not found.
     */
    @Override
    public String getFileExtension(MultipartFile file) {
        logger.info("Retrieving file extension");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                logger.info("Retrieved file extension");
                return originalFilename.substring(dotIndex + 1).toLowerCase();
            }
        }

        logger.warn("File extension not found");
        return null;
    }

    /**
     * Retrieves all help documents associated with a specific project.
     *
     * @param projectId The ID of the project.
     * @return A list of HelpDocumentsDTO representing the help documents associated with the project.
     * @throws NotFoundException If no files are found for the specified project.
     */
    @Override
    public List<HelpDocumentsDTO> getAllDocumentsByProjectId(Long projectId){
        logger.info("Fetching all help documents for project with ID: {}", projectId);

        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();
        List<HelpDocumentsDTO> fileInfos = pdfFiles.stream()
                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && Objects.equals(pdfFile.getProject().getProjectId(), projectId))
                .map(pdfFile -> new HelpDocumentsDTO(pdfFile.getHelpDocumentId(), pdfFile.getFileName()))
                .filter(helpDoc -> helpDoc.getFileName() != null) // Filter out any remaining null file names
                .toList();
        if (fileInfos.isEmpty()) {
            logger.warn("No help documents found for project with ID: {}", projectId);
            throw new NotFoundException("Files Not Found");
        }
        return fileInfos;
    }

    /**
     * Retrieves a help document by its ID.
     *
     * @param fileId The ID of the help document.
     * @return An Optional containing the HelpDocumentsDTO if found, or an empty Optional if not found.
     */
    @Override
    public Optional<HelpDocumentsDTO> getDocumentById(Long fileId){
        logger.info("Fetching help document by ID: {}", fileId);

        Optional<HelpDocuments> helpDocuments = helpDocumentsRepository.findById(fileId);
        if (helpDocuments.isPresent()){
            HelpDocuments documents = helpDocuments.get();
            HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO();
            helpDocumentsDTO.setHelpDocumentId(documents.getHelpDocumentId());
            helpDocumentsDTO.setFileName(documents.getFileName());
            logger.info("Fetched help document successfully. Document ID: {}", fileId);
            return Optional.of(helpDocumentsDTO);
        } else {
            logger.warn("Help document with ID {} not found.", fileId);
            return Optional.empty();
        }
    }

    /**
     * Retrieves a help document by its file name.
     *
     * @param fileName The name of the help document file.
     * @return The HelpDocuments entity corresponding to the file name.
     */
    @Override
    public HelpDocuments getPdfFile(String fileName) {
        logger.info("Fetching help document by file name: {}", fileName);
        return helpDocumentsRepository.findByFileName(fileName);
    }

    /**
     * Deletes a help document by its ID.
     *
     * @param fileId The ID of the help document to delete.
     */
    @Override
    public void deleteDocument(Long fileId) {
        logger.info("Deleting help document by ID: {}", fileId);

        helpDocumentsRepository.deleteById(fileId);
        logger.info("Help document with ID {} deleted successfully.", fileId);
    }
}