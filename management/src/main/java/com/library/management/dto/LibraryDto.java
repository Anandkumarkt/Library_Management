package com.library.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.management.entity.Books;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryDto {

    private long bookId;
    private  String bookName;
    private String author;
    private String genre;

    private String available;

    public LibraryDto convertEntityToDto(Books book) {
        LibraryDto booksDto = new LibraryDto();
        booksDto.setBookName(book.getBookName());
        booksDto.setBookId(book.getBookId());
        booksDto.setAuthor(book.getAuthor());
        booksDto.setGenre(book.getGenre());

        booksDto.setAvailable(book.getAvailable());
        return booksDto;

    }

    public LibraryDto convertEntityToDto(BooksDto book) {
        LibraryDto booksDto = new LibraryDto();
        booksDto.setBookName(book.getBookName());
        booksDto.setBookId(book.getBookId());
        booksDto.setAuthor(book.getAuthor());
        booksDto.setGenre(book.getGenre());

        booksDto.setAvailable(book.getAvailable());
        return booksDto;

    }

}
