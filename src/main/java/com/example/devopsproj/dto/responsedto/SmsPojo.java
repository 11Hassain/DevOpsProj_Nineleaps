package com.example.devopsproj.dto.responsedto;

import lombok.*;

/**
 * A POJO (Plain Old Java Object) representing an SMS with a phone number.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class SmsPojo {
    /**
     * The phone number associated with the SMS.
     */
    private String phoneNumber;
}
