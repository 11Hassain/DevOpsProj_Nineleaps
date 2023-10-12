package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.service.interfaces.UserNamesService;
import com.example.devopsproj.utils.GitHubUserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The `UserNamesServiceImpl` class provides services for managing usernames, including validation,
 * saving, and retrieving usernames based on their roles.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class UserNamesServiceImpl implements UserNamesService {

    private final UserNamesRepository userNamesRepository;

    public final GitHubUserValidator gitHubUserValidator;

    // Validates and saves a GitHub username with the associated user data.
    @Override
    public UserNamesDTO saveUsername(UserNamesDTO userNamesDTO) {
        boolean yes = gitHubUserValidator.isGitHubUserValid(userNamesDTO.getUsername(), userNamesDTO.getAccessToken());
        if (yes){
            UserNames userNames = new UserNames();
            userNames.setUsername(userNamesDTO.getUsername());
            userNames.setUser(userNamesDTO.getUser());
            userNamesRepository.save(userNames);
            return userNamesDTO;
        }
        else {
            return null;
        }
    }

    @Override
    public List<String> getGitHubUserNamesByRole(EnumRole role) {
        List<UserNames> userNamesList = userNamesRepository.findByUserRole(role);
        return userNamesList.stream()
                .map(UserNames::getUsername)
                .toList();
    }
}
