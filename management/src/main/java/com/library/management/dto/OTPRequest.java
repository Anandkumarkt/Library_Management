package com.library.management.dto;

import lombok.*;

@Getter
@Setter
public class OTPRequest {

    private long userId;
    private int otp;
}
