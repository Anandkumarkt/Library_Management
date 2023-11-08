package com.library.management.dao;

import com.library.management.dto.BookHistoryDto;
import com.library.management.dto.UserHistoryDto;
import com.library.management.entity.BookHistory;
import com.library.management.entity.UserHistory;
import com.library.management.repository.BookHistoryRepository;
import com.library.management.repository.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryDao {

    @Autowired private BookHistoryRepository bookHistoryRepository;
    @Autowired private UserHistoryRepository userHistoryRepository;

    public void savingToHistory(BookHistoryDto dto) {
        BookHistory history = new BookHistory().convertDtoToEntity(dto);
        bookHistoryRepository.save(history);
    }

    public void savingToUserHistory(UserHistoryDto historyDto) {
        UserHistory history = new UserHistory().convertDtoToEntity(historyDto);
        userHistoryRepository.save(history);

    }

    public List<UserHistory> findByUsername(String email) {
        return userHistoryRepository.findByUsername(email);


    }
}
