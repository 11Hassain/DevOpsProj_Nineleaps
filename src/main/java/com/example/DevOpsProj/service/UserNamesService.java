package com.example.DevOpsProj.service;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.responseDto.UserNamesDTO;
import com.example.DevOpsProj.model.UserNames;
import com.example.DevOpsProj.repository.UserNamesRepository;
import com.example.DevOpsProj.utils.GitHubUserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserNamesService {
    @Autowired
    private UserNamesRepository userNamesRepository;

    @Autowired
    public UserNamesService(UserNamesRepository userNamesRepository) {
        this.userNamesRepository = userNamesRepository;
    }

    @Autowired
    public GitHubUserValidation gitHubUserValidation;

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

    public List<String> getGitHubUserNamesByRole(EnumRole role) {
        List<UserNames> userNamesList = userNamesRepository.findByUserRole(role);
        return userNamesList.stream()
                .map(UserNames::getUsername)
                .collect(Collectors.toList());
    }
}
