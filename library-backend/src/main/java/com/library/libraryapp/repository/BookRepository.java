package com.library.libraryapp.repository;

import com.library.libraryapp.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    
}
