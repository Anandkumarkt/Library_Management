package com.library.management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private int age;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    private String role;

}
