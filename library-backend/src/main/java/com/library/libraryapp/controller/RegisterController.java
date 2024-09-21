package com.library.libraryapp.controller;

import com.library.libraryapp.dto.RegisterDTO;
import com.library.libraryapp.entity.CheckoutRegister;
import com.library.libraryapp.service.RegisterService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/registers")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private RegisterService registerService;

    @PostMapping("createRegister")
    public ResponseEntity<RegisterDTO> createRegister(@RequestBody RegisterDTO registerDTO){
        logger.info("Trying to create new checkout register...");
       RegisterDTO register = registerService.createRegister(registerDTO);

       return new ResponseEntity<>(register, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    public ResponseEntity<List<RegisterDTO>> getAllRegisters(){
        logger.info("Listing all checkout registers...");
        List<RegisterDTO> registers = registerService.getAllRegisters();
        return new ResponseEntity<>(registers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<RegisterDTO> getRegisterById(@PathVariable Long id){
        logger.info("Trying to retrieve checkout register by id: {}", id);
       RegisterDTO register = registerService.getRegisterById(id);
       return new ResponseEntity<>(register, HttpStatus.OK);
    }

    @PatchMapping("updateRegister/{id}")
    public ResponseEntity<RegisterDTO> updateRegister(@PathVariable Long id, @RequestBody RegisterDTO registerDTO){
        logger.info("Trying to update checkout register by id: {}", id);
        registerDTO.setId(id);
        RegisterDTO updatedRegister = registerService.updateRegister(registerDTO);
        return new ResponseEntity<>(updatedRegister, HttpStatus.OK);
    }

    @DeleteMapping("deleteRegister/{id}")
    public ResponseEntity<String> deleteRegister(@PathVariable Long id){
        logger.info("Trying to delete checkout register by id: {}", id);
        registerService.deleteRegister(id);
        return new ResponseEntity<>("Checkout register successfully deleted.", HttpStatus.OK);
    }

    @GetMapping("member/{memberId}")
    public ResponseEntity<List<RegisterDTO>> getRegistersByMember(@PathVariable Long memberId){
        logger.info("Trying to get a list of checkout registers by member id: {}", memberId);
       List<RegisterDTO> registers = registerService.getRegisterByMemberId(memberId);
       return new ResponseEntity<>(registers, HttpStatus.OK);
    }

    @GetMapping("book/{bookId}")
    public ResponseEntity<List<RegisterDTO>> getRegistersByBook(@PathVariable Long bookId){
        logger.info("Trying to get a list of checkout registers by book id: {}", bookId);
        List<RegisterDTO> registers = registerService.getRegisterByBookId(bookId);
        return new ResponseEntity<>(registers, HttpStatus.OK);
    }
}
