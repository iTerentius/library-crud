package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.BookDTO;
import com.library.libraryapp.entity.Book;
import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.mapper.BookMapper;
import com.library.libraryapp.repository.BookRepository;
import com.library.libraryapp.service.BookService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private BookRepository bookRepository;

    private EntityManager entityManager;

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        logger.info("Trying to add a book: {}", bookDTO);
        Book book = BookMapper.mapToBookEntity(bookDTO);
        logger.info("Book entity after mapping: {}", book);
        // CREATE book in DB
        book = bookRepository.save(book);
        logger.info("The book successfully saved in database: {}", book);
        return BookMapper.mapToBookDTO(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        // we have to iterate over the list of entities
        // then map every entity to dto,
        // then return the list of dto's
        logger.info("Retrieve all books: {}", books);
        return books.stream()
                .map(BookMapper::mapToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long bookId) {
//        Optional<Book> optionalBook = bookRepository.findById(bookId);
//        Book book = optionalBook.get();
        logger.info("Retrieve book by ID: {}", bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new ResourceNotFoundException("Book", "ID", bookId));
        return BookMapper.mapToBookDTO(book);
    }

    @Override
    public BookDTO updateBook(BookDTO bookDTO) {
        logger.info("Try to update book by ID: {}", bookDTO);
        //1. find the existing book by id
        Optional<Book> bookOptional = bookRepository.findById(bookDTO.getId());
        //2. do partial update of the book (we will update only non-null fields)
        Book bookToUpdate = bookOptional.orElseThrow(
                () -> new ResourceNotFoundException("Book", "ID", bookDTO.getId())
        );
        updateBookEntityFromDTO(bookToUpdate, bookDTO);
        //3. save update updated book to db
        Book savedBook = bookRepository.save(bookToUpdate);
        logger.info("Book successfully saved: {}", savedBook);
        //4. return existing book dto

        return BookMapper.mapToBookDTO(savedBook);
    }

    @Override
    public void deleteBook(Long bookId) {
        logger.info("Deleting book by ID: {}", bookId);
        if(!bookRepository.existsById(bookId)){
            throw new ResourceNotFoundException("Book", "ID", bookId);
        }

        bookRepository.deleteById(bookId);
    }

    @Override
    public List<BookDTO> findBooksByTitle(String title) {
        logger.info("Retrieve book by title: {}", title);
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        logger.info("Books found by title: {}", books);
        return books.stream()
                .map(BookMapper::mapToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> findBooksByTitleAndAuthor(String title, String author) {
        logger.info("Retrieve book by title: {}", title);
        logger.info("Retrieve book by author: {}", author);
        List<Book> books = bookRepository.findByTitleAndAuthorContainingIgnoreCase(title, author);
        logger.info("Books found by title and author", books);
        return books.stream()
                .map(BookMapper::mapToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> findBooksByCriteria(String title, String author, String isbn, String barcodeNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        if(title != null && !title.isEmpty()){
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if(author != null && !author.isEmpty()){
            predicates.add(cb.like(cb.lower(book.get("author")), "%" + author.toLowerCase() + "%"));
        }
        if(isbn != null && !isbn.isEmpty()){
            predicates.add(cb.like(cb.lower(book.get("isbn")), "%" + isbn.toLowerCase() + "%"));
        }
        if(barcodeNumber != null && !barcodeNumber.isEmpty()){
            predicates.add(cb.like(cb.lower(book.get("barcodeNumber")), "%" + barcodeNumber.toLowerCase() + "%"));
        }
        logger.info("Retrieve books by criteria: {}", predicates);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        List<Book> result = entityManager.createQuery(cq).getResultList();
        logger.info("Books found by criteria: {}", result);
        return result.stream()
                .map(BookMapper::mapToBookDTO)
                .collect(Collectors.toList());
    }

    private void updateBookEntityFromDTO(Book book, BookDTO bookDTO) {
        if(bookDTO.getTitle() != null){
            book.setTitle(bookDTO.getTitle());
        }
        if(bookDTO.getAuthor() != null){
            book.setAuthor(bookDTO.getAuthor());
        }
        if(bookDTO.getIsbn() != null){
            book.setIsbn(bookDTO.getIsbn());
        }
        if(bookDTO.getPublisher() != null){
            book.setPublisher(bookDTO.getPublisher());
        }
        if(bookDTO.getYearOfPublication() != null){
            book.setYearOfPublication(bookDTO.getYearOfPublication());
        }
        if(bookDTO.getPlaceOfPublication() != null){
            book.setPlaceOfPublication(bookDTO.getPlaceOfPublication());
        }
        if(bookDTO.getNoOfAvailableCopies() != null){
            book.setNoOfAvailableCopies(bookDTO.getNoOfAvailableCopies());
        }
        if(bookDTO.getBarcodeNumber() != null){
            book.setBarcodeNumber(bookDTO.getBarcodeNumber());
        }
    }


}
