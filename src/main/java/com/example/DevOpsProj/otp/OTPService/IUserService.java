package com.example.DevOpsProj.otp.OTPService;

import com.example.DevOpsProj.dto.responseDto.UserDTO;
import com.example.DevOpsProj.model.User;

public interface IUserService {

    public User getUserViaPhoneNumber(String phoneNumber);

    public User updateUser(User user);

    public User getUserByMail(String userMai);

    User insertuser(UserDTO newSsoUser);

    public User findUserByEmail(String userMail);

    public User insertUser(UserDTO newSsoUser);
}
