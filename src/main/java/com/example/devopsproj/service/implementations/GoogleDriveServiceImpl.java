package com.example.devopsproj.service.implementations;

import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import com.example.devopsproj.utils.DTOModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private final GoogleDriveRepository googleDriveRepository;

    @Override
    public GoogleDrive createGoogleDrive(GoogleDriveDTO googleDriveDTO) {
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(DTOModelMapper.mapProjectDTOToProject(googleDriveDTO.getProjectDTO()));
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        return googleDriveRepository.save(googleDrive);
    }

    @Override
    public List<GoogleDrive> getAllGoogleDrives() {
        return googleDriveRepository.findAll();
    }

    @Override
    public Optional<GoogleDriveDTO> getGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        return optionalGoogleDrive.map(this::mapGoogleDriveToDTO);
    }

    @Override
    public boolean deleteGoogleDriveById(Long driveId) {
        Optional<GoogleDrive> optionalGoogleDrive = googleDriveRepository.findById(driveId);
        if (optionalGoogleDrive.isPresent()) {
            googleDriveRepository.deleteById(driveId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<GoogleDrive> getGoogleDriveByProjectId(Long projectId) {
        return googleDriveRepository.findGoogleDriveByProjectId(projectId);
    }

    public GoogleDriveDTO mapGoogleDriveToDTO(GoogleDrive googleDrive) {
        return new GoogleDriveDTO(DTOModelMapper.mapProjectToProjectDTO(googleDrive.getProject()), googleDrive.getDriveLink(), googleDrive.getDriveId());
    }


}
