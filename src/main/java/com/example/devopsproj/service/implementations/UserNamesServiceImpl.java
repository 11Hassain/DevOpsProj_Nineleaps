package com.example.devopsproj.service.implementations;


import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.exceptions.DuplicateUsernameException;
import com.example.devopsproj.exceptions.GitHubUserNotFoundException;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.service.interfaces.UserNamesService;
import com.example.devopsproj.utils.GitHubUserValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNamesServiceImpl implements UserNamesService {

    private final UserNamesRepository userNamesRepository;
    private final GitHubUserValidation gitHubUserValidation;

    // Save a GitHub username in the database if it's valid
    @Override
    public UserNamesDTO saveUsername(UserNamesDTO userNamesDTO) {
        try {
            // Check if the GitHub user is valid using GitHubUserValidation
            boolean isValid = GitHubUserValidation.isGitHubUserValid(userNamesDTO.getUsername(), userNamesDTO.getAccessToken());
            if (isValid) {
                UserNames userNames = new UserNames();
                userNames.setUsername(userNamesDTO.getUsername());
                userNames.setUser(userNamesDTO.getUser());
                userNamesRepository.save(userNames);
                return userNamesDTO; // Return the saved UserNamesDTO
            } else {
                throw new GitHubUserNotFoundException("GitHub user not found");
            }
        } catch (DataIntegrityViolationException e) {
            // Handle the case where the username already exists.
            throw new DuplicateUsernameException("Username already exists");
        } catch (Exception e) {
            // Handle other exceptions generically or as needed.
            throw new RuntimeException("An error occurred", e);
        }
    }


    // Get a list of GitHub usernames by their role
    @Override
    public List<String> getGitHubUserNamesByRole(EnumRole role) {
        // Find user names associated with the specified role
        List<UserNames> userNamesList = userNamesRepository.findByUserRole(role);
        // Extract the usernames and return them as a list of strings
        return userNamesList.stream()
                .map(UserNames::getUsername)
                .collect(Collectors.toList());
    }
}