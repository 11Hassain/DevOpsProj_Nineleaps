package com.example.devopsproj.service.implementations;


import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;


import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.service.interfaces.UserNamesService;

import com.example.devopsproj.utils.GitHubUserValidator;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNamesServiceImpl implements UserNamesService {

    private final UserNamesRepository userNamesRepository;
    public final GitHubUserValidator gitHubUserValidator;

    private static final Logger logger = LoggerFactory.getLogger(UserNamesServiceImpl.class);

    // Validates and saves a GitHub username with the associated user data.
    @Override
    public UserNamesDTO saveUsername(UserNamesDTO userNamesDTO) {
        boolean isValidGitHubUser = gitHubUserValidator.isGitHubUserValid(userNamesDTO.getUsername(), userNamesDTO.getAccessToken());
        if (isValidGitHubUser) {
            UserNames userNames = new UserNames();
            userNames.setUsername(userNamesDTO.getUsername());
            userNames.setUser(userNamesDTO.getUser());
            userNamesRepository.save(userNames);
            logger.info("Saved GitHub username: {}", userNamesDTO.getUsername());
            return userNamesDTO;
        } else {
            logger.warn("GitHub username validation failed for: {}", userNamesDTO.getUsername());
            return null;
        }
    }

    @Override
    public List<String> getGitHubUserNamesByRole(EnumRole role) {
        List<UserNames> userNamesList = userNamesRepository.findByUserRole(role);
        logger.debug("Retrieved GitHub usernames for role: {}", role);
        return userNamesList.stream()
                .map(UserNames::getUsername)
                .toList();
    }
}