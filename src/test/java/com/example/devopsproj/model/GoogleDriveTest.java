package com.example.devopsproj.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveTest {

    @Test
    void testGoogleDriveGettersAndSetters() {
        GoogleDrive googleDrive = new GoogleDrive();

        googleDrive.setDriveId(1L);
        googleDrive.setDriveLink("https://drive.google.com/sample");

        assertEquals(1L, googleDrive.getDriveId());
        assertEquals("https://drive.google.com/sample", googleDrive.getDriveLink());
    }
}
