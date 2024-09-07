package com.library.libraryapp.controller;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/addresses")
public class AddressController {

    private AddressService addressService;

    @PostMapping("createAddress")
    public ResponseEntity<AddressDTO> addAddress(@RequestBody AddressDTO addressDTO) {
       AddressDTO savedAddressDTO =  addressService.createAddress(addressDTO);
       return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }
}
