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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    @Value("${library.loanPeriodInDays}")
    private int loanPeriodInDays;

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

    private LocalDate calculateDueDate(LocalDate checkoutDate) {
        return checkoutDate.plusDays(loanPeriodInDays);
    }
}
