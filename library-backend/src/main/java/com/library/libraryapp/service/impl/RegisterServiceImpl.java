package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.entity.CheckoutRegister;
import com.library.libraryapp.mapper.RegisterMapper;
import com.library.libraryapp.repository.CheckoutRegistryRepository;
import com.library.libraryapp.service.RegisterService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
    @Value("${library.loanPeriodInDays}")
    private int loanPeriodInDays;

    @Value("${library.overdueFineRate}")
    private double overdueFineRate;

    private final RegisterMapper registerMapper;

    private final CheckoutRegistryRepository checkoutRegistryRepository;

    @Override
    public RegisterDTO createRegister(RegisterDTO registerDTO) {
        CheckoutRegister checkoutRegister = registerMapper.mapToCheckoutRegisterEntity(registerDTO);

        // caluclate due date
        LocalDate dueDate = calculateDueDate(checkoutRegister.getCheckoutDate());
        checkoutRegister.setDueDate(dueDate);

        checkoutRegister = checkoutRegistryRepository.save(checkoutRegister);
        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public List<RegisterDTO> getAllRegisters() {
        List<CheckoutRegister> checkoutRegisters = checkoutRegistryRepository.findAll();
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RegisterDTO getRegisterById(Long id) {
        Optional<CheckoutRegister> checkoutRegisterOptional = checkoutRegistryRepository.findById(id);
        CheckoutRegister checkoutRegister = checkoutRegisterOptional.get();

        return registerMapper.mapToRegisterDTO(checkoutRegister);
    }

    @Override
    public RegisterDTO updateRegister(RegisterDTO registerDTO) {
        // find existing register by ID
        Optional<CheckoutRegister> checkoutRegisterOptional =  checkoutRegistryRepository.findById(registerDTO.getId());
        CheckoutRegister checkoutRegisterToUpdate = checkoutRegisterOptional.get();

        // do partial update
        updateCheckoutRegisterFromDTO(checkoutRegisterToUpdate, registerDTO);

        // calculate overdue fine
        calculateOverdueFine(checkoutRegisterToUpdate);

        // save updated register to DB
        CheckoutRegister updatedCheckoutRegister = checkoutRegistryRepository.save(checkoutRegisterToUpdate);

        // return register DTO via Mapper
        return registerMapper.mapToRegisterDTO(updatedCheckoutRegister);
    }

    @Override
    public void deleteRegister(Long id) {
        checkoutRegistryRepository.deleteById(id);
    }

    @Override
    public List<RegisterDTO> getRegisterByMemberId(Long memberId) {
        List<CheckoutRegister> checkoutRegisters = checkoutRegistryRepository.findByMemberId(memberId);
        return checkoutRegisters.stream()
                .map(registerMapper::mapToRegisterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RegisterDTO> getRegisterByBookId(Long bookId) {
        List<CheckoutRegister> checkoutRegisters = checkoutRegistryRepository.findByBookId(bookId);
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
        if(registerDTO.getReturnDate() != null)
            checkoutRegisterToUpdate.setDueDate(LocalDate.parse(registerDTO.getDueDate()));
        if(registerDTO.getReturnDate() != null)
            checkoutRegisterToUpdate.setReturnDate(LocalDate.parse(registerDTO.getReturnDate()));
    }

    private LocalDate calculateDueDate(LocalDate checkoutDate) {
        return checkoutDate.plusDays(loanPeriodInDays);
    }
}
