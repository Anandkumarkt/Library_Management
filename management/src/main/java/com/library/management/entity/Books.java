package com.library.management.entity;

import com.library.management.dto.BooksDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "book_id")
    private long bookId;
    @Column(name = "book_name")
    private  String bookName;
    @Column(name = "author")
    private String author;
    @Column(name = "genre")
    private String genre;

    @Column(name = "issued_on")
    private Date issuedOn;

    @Column(name = "return_date")
    private Date returnDate;

    @Column(name = "available")
    private String available;
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;


    public Books convertDtoToEntity(BooksDto booksDto) {
        Books book = new Books();
        book.setBookName(booksDto.getBookName());
        book.setAuthor(booksDto.getAuthor());
        book.setGenre(booksDto.getGenre());
        if(booksDto.getIssuedOn()!=null)
            book.setIssuedOn(booksDto.getIssuedOn());
        if(booksDto.getReturnDate()!=null)
            book.setReturnDate(booksDto.getReturnDate());
        book.setAvailable(booksDto.getAvailable());
        return book;

    }


}
