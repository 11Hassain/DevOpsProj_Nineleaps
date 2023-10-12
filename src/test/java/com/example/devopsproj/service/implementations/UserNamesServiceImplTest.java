package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.exceptions.CustomGenericException;
import com.example.devopsproj.exceptions.DuplicateUsernameException;
import com.example.devopsproj.exceptions.GitHubUserNotFoundException;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.utils.GitHubUserValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserNamesServiceImplTest {

    @InjectMocks
    private UserNamesServiceImpl userNamesService;

    @Mock
    private UserNamesRepository userNamesRepository;

    @Mock
    private GitHubUserValidation gitHubUserValidation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetGitHubUserNamesByRole() {
        EnumRole role = EnumRole.USER;
        UserNames userNames1 = new UserNames();
        userNames1.setUsername("testUsername1");
        UserNames userNames2 = new UserNames();
        userNames2.setUsername("testUsername2");

        when(userNamesRepository.findByUserRole(role)).thenReturn(Arrays.asList(userNames1, userNames2));

        List<String> result = userNamesService.getGitHubUserNamesByRole(role);

        assertEquals(2, result.size());
        assertTrue(result.contains("testUsername1"));
        assertTrue(result.contains("testUsername2"));
    }

    @Test
    void testGetGitHubUserNamesByRolee() {
        // Arrange
        EnumRole role = EnumRole.ADMIN; // Replace with the desired role
        List<UserNames> userNamesList = new ArrayList<>();
        userNamesList.add(createUserWithName("user1"));
        userNamesList.add(createUserWithName("user2"));
        when(userNamesRepository.findByUserRole(role)).thenReturn(userNamesList);

        // Act
        List<String> result = userNamesService.getGitHubUserNamesByRole(role);

        // Assert
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0));
        assertEquals("user2", result.get(1));
    }

    private UserNames createUserWithName(String username) {
        UserNames userNames = new UserNames();
        userNames.setUsername(username);
        return userNames;
    }
}
