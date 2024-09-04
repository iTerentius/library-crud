package com.library.libraryapp.repository;

import com.library.libraryapp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // find book by title
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByTitleAndAuthorContainingIgnoreCase(String title, String Author);
}
