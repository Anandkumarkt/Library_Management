package com.library.management.repository;

import com.library.management.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Users findByEmail(String email);

    Users findByEmpId(long empId);

    Users findByPhoneNumber(String phoneNumber);
    @Query("select u from Users u where u.email=?1 OR u.phoneNumber=?1")
    Users findByEmailOrPhoneNumber(String username);

    @Query("select u from Users u where u.role = 'USER'")
    List<Users> findAllUsers();
}
