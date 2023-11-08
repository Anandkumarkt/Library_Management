package com.library.management.entity;

import com.library.management.dto.SignupRequest;
import com.library.management.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "emp_id")
    private long empId;
    @Column(name = "name")
    private  String name;
    @Column(name = "age")
    private int age;
    @Column(name = "email")
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;
    @OneToMany(targetEntity = Books.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",referencedColumnName = "emp_id")
    private List<Books> books;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;


    public  Users convertDtoToEntity(SignupRequest signupRequest) {

        Users users = new Users();
        users.setAge(signupRequest.getAge());
        users.setName(signupRequest.getName());
        users.setEmail(signupRequest.getEmail());
        users.setPassword(signupRequest.getPassword());
        users.setPhoneNumber(signupRequest.getPhoneNumber());
        users.setRole((signupRequest.getRole()));
        return users;
    }

    public Users convertDtoToEntity(UserDto userDto) {
        Users users = new Users();
        users.setAge(userDto.getAge());
        users.setName(userDto.getName());
        users.setEmail(userDto.getEmail());
        users.setPhoneNumber(userDto.getPhoneNumber());
        users.setPassword(userDto.getPassword());
        users.setEmpId(userDto.getEmpId());
        users.setRole(userDto.getRole());
        return users;
    }


}
