package com.exAmple.DevOpsProj.dto.responseDto;

import com.exAmple.DevOpsProj.commons.enumerations.OTPStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponseDTO {

    private OTPStatus otpStatus;
    private String message;
}
