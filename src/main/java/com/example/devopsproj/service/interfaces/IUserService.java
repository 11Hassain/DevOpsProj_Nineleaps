package com.example.devopsproj.service.interfaces;


import com.example.devopsproj.model.User;
/**
 * Service interface for managing user-related operations, including retrieving user
 * information by phone number or email.
 */

public interface IUserService {

    public User getUserViaPhoneNumber(String phoneNumber);

    public User getUserByMail(String userMai);

}