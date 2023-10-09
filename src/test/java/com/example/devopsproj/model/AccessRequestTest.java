package com.example.devopsproj.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class AccessRequestTest {
    @Mock
    private User user;
    @Mock
    private Project project;
    @InjectMocks
    private AccessRequest accessRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAccessRequestInitializationn() {
        AccessRequest accessRequest = new AccessRequest();

        assertNull(accessRequest.getAccessRequestId());
        assertNull(accessRequest.getPmName());
        assertNull(accessRequest.getUser());
        assertNull(accessRequest.getProject());
        assertNull(accessRequest.getRequestDescription());
        assertFalse(accessRequest.isAllowed());
        assertFalse(accessRequest.isUpdated());
        assertFalse(accessRequest.isPmNotified());
    }

    @Test
    void testAccessRequestSetterGetterr() {
        AccessRequest accessRequest = new AccessRequest();

        Long accessRequestId = 1L;
        String pmName = "John Doe";
        String description = "Access request description";

        accessRequest.setAccessRequestId(accessRequestId);
        accessRequest.setPmName(pmName);
        accessRequest.setUser(user);
        accessRequest.setProject(project);
        accessRequest.setRequestDescription(description);
        accessRequest.setAllowed(true);
        accessRequest.setUpdated(true);
        accessRequest.setPmNotified(true);

        assertEquals(accessRequestId, accessRequest.getAccessRequestId());
        assertEquals(pmName, accessRequest.getPmName());
        assertEquals(user, accessRequest.getUser());
        assertEquals(project, accessRequest.getProject());
        assertEquals(description, accessRequest.getRequestDescription());
        assertTrue(accessRequest.isAllowed());
        assertTrue(accessRequest.isUpdated());
        assertTrue(accessRequest.isPmNotified());
    }


    @Test
    void testAccessRequestSetterGetter() {
        Long accessRequestId = 1L;
        String pmName = "John Doe";
        User user = new User();
        Project project = new Project();
        String description = "Access request description";

        accessRequest.setAccessRequestId(accessRequestId);
        accessRequest.setPmName(pmName);
        accessRequest.setUser(user);
        accessRequest.setProject(project);
        accessRequest.setRequestDescription(description);
        accessRequest.setAllowed(true);
        accessRequest.setUpdated(true);
        accessRequest.setPmNotified(true);

        assertEquals(accessRequestId, accessRequest.getAccessRequestId());
        assertEquals(pmName, accessRequest.getPmName());
        assertEquals(user, accessRequest.getUser());
        assertEquals(project, accessRequest.getProject());
        assertEquals(description, accessRequest.getRequestDescription());
        assertTrue(accessRequest.isAllowed());
        assertTrue(accessRequest.isUpdated());
        assertTrue(accessRequest.isPmNotified());
    }

    @Test
    void testAccessRequestInitialization() {
        AccessRequest accessRequest = new AccessRequest();

        assertNull(accessRequest.getAccessRequestId());
        assertNull(accessRequest.getPmName());
        assertNull(accessRequest.getUser());
        assertNull(accessRequest.getProject());
        assertNull(accessRequest.getRequestDescription());
        assertFalse(accessRequest.isAllowed());
        assertFalse(accessRequest.isUpdated());
        assertFalse(accessRequest.isPmNotified());
    }





}