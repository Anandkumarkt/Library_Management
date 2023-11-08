package com.library.management.repository;

import com.library.management.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookHistoryRepository extends JpaRepository<BookHistory,Long> {
}
