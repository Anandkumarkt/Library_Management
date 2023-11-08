package com.library.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.management.entity.Books;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BooksDto {
    private long bookId;
    private  String bookName;
    private String author;
    private String genre;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date issuedOn;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date returnDate;
    @JsonIgnore
    private Date createdAt;
    @JsonIgnore
    private Date updatedAt;
    @JsonIgnore
    private String available;

    public BooksDto convertEntityToDto(Books book) {
        BooksDto booksDto = new BooksDto();
        booksDto.setBookName(book.getBookName());
        booksDto.setBookId(book.getBookId());
        booksDto.setAuthor(book.getAuthor());
        booksDto.setGenre(book.getGenre());
        if(book.getIssuedOn()!=null)
            booksDto.setIssuedOn(book.getIssuedOn());
        if(book.getReturnDate()!=null)
            booksDto.setReturnDate(book.getReturnDate());
        if(book.getCreatedAt()==null)
        booksDto.setCreatedAt(book.getCreatedAt());
        booksDto.setUpdatedAt(book.getUpdatedAt());
        booksDto.setAvailable(book.getAvailable());
        return booksDto;

    }
    public List<BooksDto> convertEntityToDto(List<Books> books) {

        List<BooksDto> bookList = books.stream().map(book->{
            BooksDto dto = new BooksDto();
            dto.setBookId(book.getBookId());
            dto.setGenre(book.getGenre());
            dto.setAuthor(book.getAuthor());
            dto.setBookName(book.getBookName());
            dto.setReturnDate(book.getReturnDate());
            dto.setIssuedOn(book.getIssuedOn());
            dto.setAvailable(book.getAvailable());
            return dto;
        }).collect(Collectors.toList());

        return bookList;
    }
}
