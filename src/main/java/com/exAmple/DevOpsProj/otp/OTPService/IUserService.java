package com.exAmple.DevOpsProj.otp.OTPService;

import com.exAmple.DevOpsProj.dto.responseDto.UserDTO;
import com.exAmple.DevOpsProj.model.User;

public interface IUserService {

    public User getUserViaPhoneNumber(String phoneNumber);

    public User updateUser(User user);

    public User getUserByMail(String userMai);

    User insertuser(UserDTO newSsoUser);

    public User findUserByEmail(String userMail);

    public User insertUser(UserDTO newSsoUser);
}
