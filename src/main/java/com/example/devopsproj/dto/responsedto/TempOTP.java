package com.example.devopsproj.dto.responsedto;

/**
 * A class representing a temporary OTP (One-Time Password).
 */
public class TempOTP {

    private int otp;

    /**
     * Get the OTP value.
     *
     * @return The OTP value.
     */
    public int getOtp() {
        return otp;
    }

    /**
     * Set the OTP value.
     *
     * @param otp The OTP value to be set.
     */
    public void setOtp(int otp) {
        this.otp = otp;
    }
}
