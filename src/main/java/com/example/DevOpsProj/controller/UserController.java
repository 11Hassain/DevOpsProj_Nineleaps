package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.requestDto.UserCreationDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private ModelMapper modelMapper;


    @PostMapping("/") //Save the user
    public ResponseEntity<User> saveUser(@RequestBody UserCreationDTO userCreationDTO){
        User savedUser = userService.saveUser(userCreationDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{user_id}") //find user by user id
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long user_id){
        Optional<User> optionalUser = userService.getUserById(user_id);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/update/{id}")//update user by id
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO){
        Optional<User> optionalUser = userService.getUserById(id);
        if(optionalUser.isPresent()){
            User existingUser = optionalUser.get();
            existingUser.setId(userDTO.getId());
            existingUser.setName(userDTO.getName());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setEnumRole(userDTO.getEnumRole());
            User updatedUser = userService.updateUser(existingUser);
            UserDTO userDTOs = new UserDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail(), updatedUser.getEnumRole());
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{user_id}") //soft-deleting user
    public ResponseEntity<String> deleteUserById(@PathVariable Long user_id){
        if(userService.existsById(user_id)) {
            boolean checkIfDeleted = userService.existsByIdIsDeleted(user_id); //check if deleted = true?
            if (checkIfDeleted) {
                return ResponseEntity.ok("User doesn't exist");
                //user is present in db but deleted=true(soft deleted)
            }
            boolean isDeleted = userService.softDeleteUser(user_id); //soft deletes user with id (yes/no)
            if(isDeleted){
                return ResponseEntity.ok("User successfully deleted");
                //successfully deleting user (soft delete) (user exists in db)
            }
            else{
                return ResponseEntity.ok("404 Not found");
                //gives 404 Not Found error response
            }
        }
        else return ResponseEntity.ok("Invalid user ID");
    }

    @GetMapping("/role/{role}") //get list of user by role
    public ResponseEntity<List<UserDTO>> getUserByRoleId(@PathVariable("role") String role){
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase()); //getting value of role(string)
        List<User> users = userService.getUsersByRole(userRole);
        List<UserDTO> userDTOList = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping("/count") //get count of all the users
    public Integer getCountAllUsers(){
        Integer countUsers = userService.getCountAllUsers();
        if (countUsers == 0){
            return 0;
        }
        else {
            return countUsers;
        }
    }

    @GetMapping("/count/{role}")
    public Integer getCountAllUsersByRole(@PathVariable String role){
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsersByRole = userService.getCountAllUsersByRole(userRole);
        if(countUsersByRole == 0){
            return 0;
        }
        else {
            return countUsersByRole;
        }
    }

    @GetMapping("/count/project/{projectId}")
    public Integer getCountAllUsersByProjectId(@PathVariable Long projectId){
        Integer countUsersByProject = userService.getCountAllUsersByProjectId(projectId);
        if (countUsersByProject == 0){
            return 0;
        }
        else {
            return countUsersByProject;
        }
    }
}
