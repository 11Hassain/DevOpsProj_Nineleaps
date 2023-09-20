package com.exAmple.DevOpsProj.controller;

import com.exAmple.DevOpsProj.commons.enumerations.EnumRole;
import com.exAmple.DevOpsProj.service.JwtService;
import com.exAmple.DevOpsProj.service.UserNamesService;
import com.exAmple.DevOpsProj.dto.responseDto.UserNamesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usernames")
public class UserNamesController {

    @Autowired
    private final UserNamesService userNamesService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    public UserNamesController(UserNamesService userNamesService) {
        this.userNamesService = userNamesService;
    }

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/githubUsername")
    public ResponseEntity<Object> saveUsername(@RequestBody UserNamesDTO userNamesDTO,
                                               @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try{
                UserNamesDTO savedUserNames = userNamesService.saveUsername(userNamesDTO);
                if (savedUserNames == null){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Github user not found");
                }
                else {
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedUserNames);
                }
            }catch (DataIntegrityViolationException e){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Object> getUserNamesByRole(@PathVariable String role, @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userNamesService.getGitHubUserNamesByRole(enumRole));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}
