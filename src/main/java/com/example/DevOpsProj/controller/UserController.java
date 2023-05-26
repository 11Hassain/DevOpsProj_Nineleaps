package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.requestDto.UserCreationDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

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

}
