package com.library.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.management.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.print.Book;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto  {


    private long empId;
    private  String name;
    private int age;
    private String email;
    private String phoneNumber;

    @JsonIgnore
    private String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    private String role;
    @JsonIgnore
    private Date createdAt;
    @JsonIgnore
    private Date updatedAt;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BooksDto> booksList;



    public UserDto convertEntityToDto(Users users) {
        UserDto userDto = new UserDto();
        userDto.setName(users.getName());
        userDto.setEmpId(users.getEmpId());
        userDto.setAge(users.getAge());
        userDto.setEmail(users.getEmail());
        userDto.setPhoneNumber(users.getPhoneNumber());
        userDto.setPassword(users.getPassword());
        userDto.setRole(users.getRole());
        if((users.getBooks() !=null)){
            BooksDto booksDto = new BooksDto();
            userDto.setBooksList(booksDto.convertEntityToDto(users.getBooks()));
        }
        return userDto;
    }


}
