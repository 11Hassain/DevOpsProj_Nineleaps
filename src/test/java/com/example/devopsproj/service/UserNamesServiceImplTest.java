package com.example.devopsproj.service;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.model.User;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.service.implementations.UserNamesServiceImpl;
import com.example.devopsproj.utils.GitHubUserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserNamesServiceImplTest {

    @InjectMocks
    private UserNamesServiceImpl userNamesService;
    @Mock
    private UserNamesRepository userNamesRepository;
    @Mock
    private GitHubUserValidation gitHubUserValidation;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS -----

    @Test
    void testGetGitHubUserNamesByRole_WithValidRole() {
        EnumRole role = EnumRole.ADMIN;
        UserNames user1 = new UserNames();
        user1.setUsername("Username1");

        UserNames user2 = new UserNames();
        user2.setUsername("Username2");

        when(userNamesRepository.findByUserRole(role)).thenReturn(Arrays.asList(user1, user2));

        List<String> userNames = userNamesService.getGitHubUserNamesByRole(role);

        assertNotNull(userNames);
        assertEquals(2, userNames.size());
        assertTrue(userNames.contains("Username1"));
        assertTrue(userNames.contains("Username2"));
    }

    // ----- FAILURE -----


    @Test
    void testGetGitHubUserNamesByRole_WithNoUsers() {
        EnumRole role = EnumRole.USER;
        when(userNamesRepository.findByUserRole(role)).thenReturn(Collections.emptyList());

        List<String> userNames = userNamesService.getGitHubUserNamesByRole(role);

        assertNotNull(userNames);
        assertTrue(userNames.isEmpty());
    }

}
