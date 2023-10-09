package com.example.devopsproj.service.implementations;


import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.UserNamesDTO;
import com.example.devopsproj.exceptions.CustomGenericException;
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


@Service
@RequiredArgsConstructor
public class UserNamesServiceImpl implements UserNamesService {

    public final GitHubUserValidation gitHubUserValidation;
    private final UserNamesRepository userNamesRepository;


    @Override
    public UserNamesDTO saveUsername(UserNamesDTO userNamesDTO) {
        try {
        boolean yes = GitHubUserValidation.isGitHubUserValid(userNamesDTO.getUsername(), userNamesDTO.getAccessToken());
        if (yes){
            UserNames userNames = new UserNames();
            userNames.setUsername(userNamesDTO.getUsername());
            userNames.setUser(userNamesDTO.getUser());
            userNamesRepository.save(userNames);
            return userNamesDTO;
        } else {
            throw new GitHubUserNotFoundException("GitHub user not found");
        }
    } catch (DataIntegrityViolationException e) {
        // Handle the case where the username already exists.
        throw new DuplicateUsernameException("Username already exists");
    } catch (Exception e) {
        // Handle other exceptions generically or as needed.
        throw new CustomGenericException("An error occurred", e);
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