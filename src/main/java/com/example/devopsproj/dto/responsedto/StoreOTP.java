package com.example.devopsproj.dto.responsedto;

public class StoreOTP {

    private static int otp;

    // Private constructor to hide the implicit public one
    private StoreOTP() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }

    public static int getOtp() {
        return otp;
    }

    public static void setOtp(int otp) {
        StoreOTP.otp = otp;
    }
}