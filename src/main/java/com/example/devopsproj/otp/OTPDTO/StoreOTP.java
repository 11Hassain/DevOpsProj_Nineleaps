package com.example.devopsproj.otp.OTPDTO;

import lombok.Getter;

public class StoreOTP {

    @Getter
    private static int otp;

    public static void setOtp(int otp) {
        StoreOTP.otp = otp;
    }
}