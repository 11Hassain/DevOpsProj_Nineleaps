package com.example.DevOpsProj.service;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.requestDto.UserCreationDTO;
import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.User;
import com.example.DevOpsProj.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper;


    //implementing user creation using DTO pattern
    public User saveUser(@RequestBody UserCreationDTO userCreationDTO){
        User user = new User();
        user.setId(userCreationDTO.getId());
        user.setName(userCreationDTO.getName());
        user.setEmail(userCreationDTO.getEmail());
//        user.setPassword(userCreationDTO.getPassword());
        user.setEnumRole(userCreationDTO.getEnumRole());
        return userRepository.save(user);
    }

    //find user by user id
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }


    //this function says whether id is soft-deleted or not
    public boolean existsByIdIsDeleted(Long id){
        Optional<User> checkUser = userRepository.findById(id);
        User cuser = checkUser.get();
        return cuser.getDeleted(); //true if deleted=1, false otherwise
    }

    //Soft deleting the user
    public boolean softDeleteUser(Long id){
        try{
            userRepository.softDelete(id);
            return true; //setting deleted=1 / true
        }catch (Exception e){
            return false; //keeping deleted false
        }
    }

    //check if the user is present in db
    public boolean existsById(Long id){
        return userRepository.existsById(id);
    }

    //updating the user details
    public User updateUser(User updateUser){
        return userRepository.save(updateUser);
    }

    //editing
    //get all user based on role id
    public List<User> getUsersByRole(EnumRole enumRole){
        return userRepository.findByRole(enumRole);
    }

    private UserDTO convertToUserDto(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    public User getUserByEmail(String userEmail){
        return userRepository.findByEmail(userEmail);
    }

}
