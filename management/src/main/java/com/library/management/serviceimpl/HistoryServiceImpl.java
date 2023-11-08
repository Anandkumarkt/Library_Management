package com.library.management.serviceimpl;

import com.library.management.dao.HistoryDao;
import com.library.management.dto.BookHistoryDto;
import com.library.management.dto.BooksDto;
import com.library.management.dto.UserDto;
import com.library.management.dto.UserHistoryDto;
import com.library.management.entity.enums.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class HistoryServiceImpl {

    @Autowired private HistoryDao historyDao;

    public  void addToBookHistory(String username, String bookName, List<BooksDto> booksList, Action action) {

        booksList.forEach(book->{
            StringBuilder note=new StringBuilder();
            BookHistoryDto dto = new BookHistoryDto();
            dto.setUserName(username);
            dto.setBookName(bookName);
            if(action.equals(Action.ADD))
                note.append(username+" borrowed this book "+bookName+ " on "+book.getIssuedOn());
            else if(action.equals(Action.UPDATE))
                note.append(username+" returned the book "+bookName+" on "+book.getReturnDate());
            else if (action.equals(Action.ADDBOOK))
                note.append(username+" added a new book "+bookName+" in the library");
            dto.setNote(note.toString());
            dto.setBorrowDate(book.getIssuedOn());
            dto.setReturnDate(book.getReturnDate());
            historyDao.savingToHistory(dto);

        });


    }

    public void addToUserHistory(UserDto userDto, UserDto adminDto, Action action, String password) {
        StringBuilder sbr = new StringBuilder();
        UserHistoryDto historyDto = new UserHistoryDto();
        historyDto.setUsername(userDto.getEmail());
        historyDto.setRole(userDto.getRole());
        if (adminDto != null)
            historyDto.setCreatedBy(adminDto.getName() + " - " + adminDto.getEmail());
        else
            historyDto.setCreatedBy(userDto.getName() + " - " + userDto.getEmail());

        if(action.equals(Action.ADD)) {
            sbr.append("New user - ").append(userDto.getName()).append(" ~ ").append(userDto.getEmail())
                            .append(" added by ").append(historyDto.getCreatedBy());
            historyDto.setNote(sbr.toString());
        }
        else if(action.equals(Action.UPDATE)){
            sbr.append("User - ").append(userDto.getName()).append(" ~ ").append(userDto.getEmail())
                    .append(" updated by ").append(historyDto.getCreatedBy());
            historyDto.setNote(sbr.toString());
        }
        else if(action.equals(Action.MASTER)){
            sbr.append("New Master user ").append(userDto.getName()).append(" ~ ").append(userDto.getEmail())
                    .append(" created  ");
            historyDto.setNote(sbr.toString());
        }else if(action.equals(Action.CHANGE)){
            sbr.append("User : ").append(userDto.getName()).append(" ~ ").append("changed the password ("+password+") on ").append(new Date().toString());
            historyDto.setNote(sbr.toString());
        }
        historyDao.savingToUserHistory(historyDto);
    }
}
