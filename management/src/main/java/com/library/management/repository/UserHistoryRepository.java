package com.library.management.repository;

import com.library.management.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory,Long> {
    List<UserHistory> findByUsername(String email);
}
