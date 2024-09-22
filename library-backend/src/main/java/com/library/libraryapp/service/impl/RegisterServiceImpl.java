package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.entity.CheckoutRegister;
import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.mapper.RegisterMapper;
import com.library.libraryapp.repository.CheckoutRegistryRepository;
import com.library.libraryapp.service.RegisterService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Value("${library.loanPeriodInDays}")
    private int loanPeriodInDays;

    @Value("${library.overdueFineRate}")
    private double overdueFineRate;

    private final RegisterMapper registerMapper;

    private final CheckoutRegistryRepository checkoutRegistryRepository;

    @Override
    public RegisterDTO createRegister(RegisterDTO registerDTO) {
        logger.info("Trying to add a checkout register: {}", registerDTO);
        CheckoutRegister checkoutRegister = registerMapper.mapToCheckoutRegisterEntity(registerDTO);

        // caluclate due date
        LocalDate dueDate = calculateDueDate(checkoutRegister.getCheckoutDate());
        checkoutRegister.setDueDate(dueDate);

        checkoutRegister = checkoutRegistryRepository.save(checkoutRegister);
        logger.info("The checkout register successfully saved in the database: {}", checkoutRegister);
        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public List<RegisterDTO> getAllRegisters() {
        List<CheckoutRegister> checkoutRegisters = checkoutRegistryRepository.findAll();
        logger.info("Retrieve all checkout registers: {}", checkoutRegisters);
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RegisterDTO getRegisterById(Long id) {
        logger.info("Retrieve checkout register by ID: {}", id);
        Optional<CheckoutRegister> checkoutRegisterOptional = checkoutRegistryRepository.findById(id);
        CheckoutRegister checkoutRegister = checkoutRegisterOptional.orElseThrow(
                () -> new ResourceNotFoundException("Checkout Register", "ID", id)
        );

        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public RegisterDTO updateRegister(RegisterDTO registerDTO) {
        logger.info("Try to update checkout register: {}", registerDTO);
        // find existing register by ID
        Optional<CheckoutRegister> checkoutRegisterOptional =  checkoutRegistryRepository.findById(registerDTO.getId());
        CheckoutRegister checkoutRegisterToUpdate = checkoutRegisterOptional.orElseThrow(
                () -> new ResourceNotFoundException("Checkout Register", "ID", registerDTO.getId())
        );

        // do partial update
        updateCheckoutRegisterFromDTO(checkoutRegisterToUpdate, registerDTO);

        // calculate overdue fine
        calculateOverdueFine(checkoutRegisterToUpdate);

        // save updated register to DB
        CheckoutRegister updatedCheckoutRegister = checkoutRegistryRepository.save(checkoutRegisterToUpdate);
        logger.info("Checkout register successfully saved: {}", updatedCheckoutRegister);
        // return register DTO via Mapper
        return registerMapper.mapToRegisterDTO(updatedCheckoutRegister);
    }

    @Override
    public void deleteRegister(Long id) {
        logger.info("Deleting checkout register by ID: {}", id);
        if(!checkoutRegistryRepository.existsById(id)){
            throw new ResourceNotFoundException("Checkout Register", "ID", id);
        }
        checkoutRegistryRepository.deleteById(id);
    }

    @Override
    public List<RegisterDTO> getRegisterByMemberId(Long memberId) {
        logger.info("Retrieve checkout registers by member id: {}", memberId);
        List<CheckoutRegister> checkoutRegisters = checkoutRegistryRepository.findByMemberId(memberId);
        logger.info("Checkout registers found by member id: {}", checkoutRegisters);
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegisterDTO> getRegisterByBookId(Long bookId) {
        logger.info("Retrieve checkout registers by book id: {}", bookId);
        List<CheckoutRegister> checkoutRegisters = checkoutRegistryRepository.findByBookId(bookId);
        logger.info("Checkout registers found by book id: {}", checkoutRegisters);
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .collect(Collectors.toList());
    }

    private void calculateOverdueFine(CheckoutRegister checkoutRegister) {
        if(checkoutRegister.getReturnDate() != null &&
            checkoutRegister.getReturnDate().isAfter(checkoutRegister.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(checkoutRegister.getDueDate(), checkoutRegister.getReturnDate());
            // overdue fine = daysOverdue * overdueFineRate
            double overdueFine = daysOverdue * overdueFineRate;
            checkoutRegister.setOverdueFine(overdueFine);
        }
    }

    private void updateCheckoutRegisterFromDTO(CheckoutRegister checkoutRegisterToUpdate, RegisterDTO registerDTO) {
        // the user can either prolong the due date of the book or record the return of the book
        if(registerDTO.getDueDate() != null)
            checkoutRegisterToUpdate.setDueDate(LocalDate.parse(registerDTO.getDueDate()));
        if(registerDTO.getReturnDate() != null)
            checkoutRegisterToUpdate.setReturnDate(LocalDate.parse(registerDTO.getReturnDate()));
    }

    private LocalDate calculateDueDate(LocalDate checkoutDate) {
        return checkoutDate.plusDays(loanPeriodInDays);
    }
}
