package com.example.devopsproj.model;

import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class GoogleDriveTest {

    @Test
     void testNoArgsConstructor() {
        GoogleDrive googleDrive = new GoogleDrive();

        assertNull(googleDrive.getDriveId());
        assertNull(googleDrive.getDriveLink());
        assertNull(googleDrive.getProject());
    }

    @Test
     void testGetterSetter() {
        GoogleDrive googleDrive = new GoogleDrive();

        Long driveId = 1L;
        String driveLink = "https://example.com/drive";
        Project project = new Project(); // You can create a Project instance here

        googleDrive.setDriveId(driveId);
        googleDrive.setDriveLink(driveLink);
        googleDrive.setProject(project);

        assertEquals(driveId, googleDrive.getDriveId());
        assertEquals(driveLink, googleDrive.getDriveLink());
        assertEquals(project, googleDrive.getProject());
    }
}
