package com.library.management.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.management.constants.ResponseConstants;
import com.library.management.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Getter
@Setter
public class UserResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
    private HttpStatus status;
    private String message;

    public Object ConvertObjectToResponse(Object userDto,String message) {

        UserResponse response = new UserResponse();
        response.setData(userDto);
        response.setMessage(message);
        response.setStatus(HttpStatus.OK);
        return response;

    }

    public Object ConvertObjectToResponse(String message) {

        UserResponse response = new UserResponse();

        response.setMessage(message);
        response.setStatus(HttpStatus.BAD_REQUEST);
        return response;
    }
}
