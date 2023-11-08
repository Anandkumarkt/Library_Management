package com.library.management.entity;

import com.library.management.dto.BookHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book_history")
public class BookHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "hist_id")
    private long bHistId;
    @Column(name = "book_name")
    private String bookName;
    @Column(name = "borrow_date")
    private Date borrowDate;
    @Column(name = "return_date")
    private Date returnDate;
    @Column(name = "note")
    private String note;
    @Column(name = "username")
    private String username;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;


    public BookHistory convertDtoToEntity(BookHistoryDto dto){
        BookHistory history = new BookHistory();
        history.setNote(dto.getNote());
        history.setBHistId(dto.getBHistId());
        history.setBookName(dto.getBookName());
        history.setBorrowDate(dto.getBorrowDate());
        history.setReturnDate(dto.getReturnDate());
        history.setUsername(dto.getUserName());
        return history;

    }

}
