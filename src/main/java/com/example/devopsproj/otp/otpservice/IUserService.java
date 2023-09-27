package com.example.devopsproj.otp.otpservice;

import com.example.devopsproj.model.User;

public interface IUserService {

    public User getUserViaPhoneNumber(String phoneNumber);

    public User getUserByMail(String userMai);

}
