package com.exAmple.DevOpsProj.otp.OTPService;

import com.exAmple.DevOpsProj.model.User;

public interface IUserService {

    public User getUserViaPhoneNumber(String phoneNumber);

    public User getUserByMail(String userMai);

}
