package com.example.devopsproj.otp.OTPService;

import com.example.devopsproj.model.User;

public interface IUserService {

    public User getUserViaPhoneNumber(String phoneNumber);

    public User getUserByMail(String userMai);

}
