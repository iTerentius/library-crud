package com.library.libraryapp.service;

import com.library.libraryapp.dto.BookDTO;
import com.library.libraryapp.entity.Book;

import java.util.List;

public interface BookService {

    BookDTO addBook(BookDTO bookDTO);

    List <BookDTO> getAllBooks();

    BookDTO getBookById(Long bookId);

    BookDTO updateBook(BookDTO bookDTO);

    void deleteBook(Long bookId);

    List<BookDTO> findBooksByTitle(String title);

    List<BookDTO> findBooksByTitleAndAuthor(String title, String author);

    List<BookDTO> findBooksByCriteria(String title, String author, String isbn, String barcodeNumber);
}
