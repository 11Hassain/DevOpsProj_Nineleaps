package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.responseDto.UserNamesDTO;
import com.example.DevOpsProj.service.JwtService;
import com.example.DevOpsProj.service.UserNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.List;

@RestController
@RequestMapping("/usernames")
public class UserNamesController {

    @Autowired
    private final UserNamesService userNamesService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    public UserNamesController(UserNamesService userNamesService) {
        this.userNamesService = userNamesService;
    }

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Object> getUserNamesByRole(@PathVariable String role, @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userNamesService.getGitHubUserNamesByRole(enumRole));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }
}
