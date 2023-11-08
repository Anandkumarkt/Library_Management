package com.library.management.dto;

import com.library.management.entity.BookHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookHistoryDto {


    private long bHistId;
    private String bookName;
    private Date borrowDate;
    private Date returnDate;
    private String note;
    private String userName;


    public BookHistoryDto convertEntityToDto(BookHistory history){
        BookHistoryDto dto = new BookHistoryDto();
        dto.setBHistId(history.getBHistId());
        dto.setBookName(history.getBookName());
        dto.setBorrowDate(history.getBorrowDate());
        dto.setReturnDate(history.getReturnDate());
        dto.setNote(history.getNote());
        dto.setUserName(history.getUsername());
        return dto;
    }


}
