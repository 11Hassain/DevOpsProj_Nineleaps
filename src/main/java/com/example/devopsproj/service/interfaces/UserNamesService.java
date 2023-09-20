package com.example.devopsproj.service.interfaces;



import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.UserNamesDTO;

import java.util.List;

public interface UserNamesService {
    UserNamesDTO saveUsername(UserNamesDTO userNamesDTO);
    List<String> getGitHubUserNamesByRole(EnumRole role);
}
