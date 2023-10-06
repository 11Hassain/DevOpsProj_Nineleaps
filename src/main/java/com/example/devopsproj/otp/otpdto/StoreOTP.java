package com.example.devopsproj.otp.otpdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreOTP {

    private StoreOTP() {
        // This constructor is intentionally left empty because the class
        // only contains static methods for mapping and doesn't need to be instantiated.
        throw new UnsupportedOperationException("This class should not be instantiated.");
    }

    @Getter
    private static int otp;

    public static void setOtp(int otp) {
        StoreOTP.otp = otp;
    }
}