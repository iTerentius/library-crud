package com.library.libraryapp.controller;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.service.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/registers")
public class RegisterController {

    private RegisterService registerService;

    @PostMapping("createRegister")
    public ResponseEntity<RegisterDTO> createRegister(@RequestBody RegisterDTO registerDTO){
       RegisterDTO register = registerService.createRegister(registerDTO);

       return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    public ResponseEntity<List<RegisterDTO>> getAllRegisters(){
        List<RegisterDTO> registers = registerService.getAllRegisters();
        return new ResponseEntity<>(registers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<RegisterDTO> getRegisterById(@PathVariable Long id){
       RegisterDTO register = registerService.getRegisterById(id);
       return new ResponseEntity<>(register, HttpStatus.OK);
    }

    @PatchMapping("updateRegister/{id}")
    public ResponseEntity<RegisterDTO> updateRegister(@PathVariable Long id, @RequestBody RegisterDTO registerDTO){
        registerDTO.setId(id);
        RegisterDTO updatedRegister = registerService.updateRegister(registerDTO);
        return new ResponseEntity<>(updatedRegister, HttpStatus.OK);
    }
}
