package com.library.libraryapp.controller;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("listAll")
    public ResponseEntity<List<AddressDTO>> getAllAddress(){
       List<AddressDTO> allAddresses = addressService.getAllAddresses();
       return new ResponseEntity<>(allAddresses, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id){
       AddressDTO addressDTO =  addressService.getAddressById(id);
       return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PatchMapping("updateAddress/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO){
        addressDTO.setId(id);
        AddressDTO updatedAddress = addressService.updateAddress(addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }
}
