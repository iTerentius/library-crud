package com.library.libraryapp.service;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.entity.CheckoutRegister;

import java.util.List;

public interface RegisterService {
   RegisterDTO createRegister(RegisterDTO registerDTO);

   List<RegisterDTO> getAllRegisters();

   RegisterDTO getRegisterById(Long id);

   RegisterDTO updateRegister(RegisterDTO registerDTO);

   void deleteRegister(Long id);
}
