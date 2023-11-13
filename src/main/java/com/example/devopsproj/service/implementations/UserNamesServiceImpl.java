package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.service.interfaces.UserNamesService;
import com.example.devopsproj.utils.GitHubUserValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserNamesServiceImpl.class);
    private final UserNamesRepository userNamesRepository;
    public final GitHubUserValidator gitHubUserValidator;

    /**
     * Saves a GitHub username and associated user information.
     *
     * @param userNamesDTO The DTO containing GitHub username and user information.
     * @return The saved UserNamesDTO if the GitHub user is valid; otherwise, null.
     */
    @Override
    public UserNamesDTO saveUsername(UserNamesDTO userNamesDTO) {
        logger.info("Saving GitHub username: {}", userNamesDTO.getUsername());

        boolean yes = gitHubUserValidator.isGitHubUserValid(userNamesDTO.getUsername(), userNamesDTO.getAccessToken());
        if (yes){
            UserNames userNames = new UserNames();
            userNames.setUsername(userNamesDTO.getUsername());
            userNames.setUser(userNamesDTO.getUser());
            userNamesRepository.save(userNames);
            logger.info("GitHub username saved successfully: {}", userNamesDTO.getUsername());
            return userNamesDTO;
        }
        else {
            logger.warn("Invalid GitHub username: {}", userNamesDTO.getUsername());
            return null;
        }
    }

    /**
     * Gets GitHub usernames based on the specified user role.
     *
     * @param role The user role for filtering GitHub usernames.
     * @return A list of GitHub usernames.
     */
    @Override
    public List<String> getGitHubUserNamesByRole(EnumRole role) {
        logger.info("Fetching GitHub usernames for role: {}", role);

        List<UserNames> userNamesList = userNamesRepository.findByUserRole(role);
        logger.info("Fetched GitHub usernames successfully for role: {}", role);
        return userNamesList.stream()
                .map(UserNames::getUsername)
                .toList();
    }
}
