package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.dto.responseDto.UserNamesDTO;
import com.example.devopsproj.repository.UserNamesRepository;
import com.example.devopsproj.service.interfaces.UserNamesService;
import com.example.devopsproj.utils.GitHubUserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserNamesServiceImpl implements UserNamesService {
    @Autowired
    private UserNamesRepository userNamesRepository;

    @Autowired
    public UserNamesServiceImpl(UserNamesRepository userNamesRepository) {
        this.userNamesRepository = userNamesRepository;
    }

    @Autowired
    public GitHubUserValidation gitHubUserValidation;

    @Override
    public UserNamesDTO saveUsername(UserNamesDTO userNamesDTO) {
        boolean yes = GitHubUserValidation.isGitHubUserValid(userNamesDTO.getUsername(), userNamesDTO.getAccessToken());
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
                .collect(Collectors.toList());
    }
}