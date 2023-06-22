package com.example.DevOpsProj.controller;


import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.repository.UserRepository;
import com.example.DevOpsProj.service.JwtService;
import com.example.DevOpsProj.service.UserService;
import com.example.DevOpsProj.utils.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/api/get-email")
    public ResponseEntity<Object> getEmailFromToken(@RequestHeader("emailToVerify") String emailToVerify) throws IOException {
        return ResponseEntity.ok(userService.loginVerification(emailToVerify));
//        return new ResponseEntity<>(userService.loginVerification(emailToVerify), HttpStatus.OK);
    }







//import com.example.DevOpsProj.model.User;
//import com.example.DevOpsProj.service.ProjectService;
//import com.example.DevOpsProj.service.UserService;
////import com.example.DevOpsProj.utils.JwtUtils;
//import com.example.DevOpsProj.utils.JwtUtils;
//import jakarta.servlet.http.HttpServletResponse;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//
//    fetch the email from the token given to verify the user - whether present in DB
    @GetMapping("/api/getEmail")
    public ResponseEntity<Object> getEmailFromToken(
            @RequestHeader("Authorization") String authHeader,
            HttpServletResponse response) throws IOException{
        try {
            String jwt = authHeader.replace("Bearer", "");
            String emailToVerify = JwtUtils.getEmailFromJwt(jwt);
            return new ResponseEntity<>(userService.loginVerification(emailToVerify), HttpStatus.OK);
//            return getUserByEmail(response, emailToVerify);
        }catch (IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }
//
//    //check if he Email is present in DB
//    public ResponseEntity<Map<String, String>> getUserByEmail(HttpServletResponse response, String emailToVerify) throws IOException{
//        User user = userService.getUserByEmail(emailToVerify);
//        String role = user.getEnumRole().toString();
//
//        if(user!=null){
//            Map<String, String> userDetails = new HashMap<>();
//            userDetails.put("email", user.getEmail());
//            userDetails.put("role", user.getEnumRole().toString());
//            userDetails.put("name", user.getName());
//            userDetails.put("id", String.valueOf(user.getId()));
//            userDetails.put("deleted", user.getDeleted().toString());
//
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Custom-Header", role);
//            return new ResponseEntity<>(userDetails, headers, HttpStatus.OK);
//        }
//        else
//            return ResponseEntity.notFound().build();
//    }

}

