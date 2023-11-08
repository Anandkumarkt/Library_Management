package com.library.management.repository;

import com.library.management.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books,Long> {


    Books findByBookName(String bookName);

    @Query(value = "select * from books where user_id=?1",nativeQuery = true)
    List<Books> findByUserId(long empId);

    @Query("select b from Books b where b.available=?1")
    List<Books> findAllBooksByAvailability(String available);

    @Query("select b from Books b where"+ "(:bookName IS NULL OR :bookName=b.bookName)AND"+
            "(:author IS NULL OR :author=b.author)AND"+
            "(:genre IS NULL OR :genre=b.genre)AND"+
            "(:available IS NULL OR :available=b.available)")
    List<Books> filterBooks(@Param("bookName") Optional<String> bookName,
                            @Param("author") Optional<String> author,
                            @Param("genre") Optional<String> genre,
                            @Param("available") Optional<String> available);

    @Modifying
    @Transactional
    @Query(value = "update books set user_id=null where book_id=?1",nativeQuery = true)
    void updateUserId(long bookId);
}
