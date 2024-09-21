package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.entity.PostalAddress;
import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.mapper.AddressMapper;
import com.library.libraryapp.repository.AddressRepository;
import com.library.libraryapp.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private AddressRepository addressRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
       PostalAddress postalAddress = AddressMapper.mapToAddressEntity(addressDTO);
       postalAddress = addressRepository.save(postalAddress); // save entity to DB

        return AddressMapper.mapToAddressDTO(postalAddress);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<PostalAddress> postalAddresses = addressRepository.findAll();
        return postalAddresses.stream()
                .map(AddressMapper::mapToAddressDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressDTO getAddressById(Long id) {
        Optional<PostalAddress> optionalPostalAddress = addressRepository.findById(id);
        PostalAddress postalAddress = optionalPostalAddress.orElseThrow(
                () -> new ResourceNotFoundException("Address", "ID", id));
        return AddressMapper.mapToAddressDTO(postalAddress);
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO) {
        // 1. Find existing by ID
        Optional<PostalAddress> optionalPostalAddress = addressRepository.findById(addressDTO.getId());
        PostalAddress addressToUpdate = optionalPostalAddress.orElseThrow(
                () -> new ResourceNotFoundException("Address", "ID", addressDTO.getId())
        );
        // 2. Do partial update of the address
        updateAddressEntityFromDTO(addressToUpdate, addressDTO);
        // 3. Save updated address in DB
        PostalAddress updatedAddress = addressRepository.save(addressToUpdate);
        // 4. Return Address DTO using Mapper
        return AddressMapper.mapToAddressDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long id) {
        if(!addressRepository.existsById(id)){
            throw new ResourceNotFoundException("Address", "ID", id);
        }
        addressRepository.deleteById(id);
    }

    public void updateAddressEntityFromDTO(PostalAddress addressToUpdate, AddressDTO addressDTO) {
        if(addressDTO.getStreetName() != null) addressToUpdate.setStreetName(addressDTO.getStreetName());
        if(addressDTO.getStreetNumber() != null) addressToUpdate.setStreetNumber(addressDTO.getStreetNumber());
        if(addressDTO.getZipCode() != null) addressToUpdate.setZipCode(addressDTO.getZipCode());
        if(addressDTO.getPlaceName() != null) addressToUpdate.setPlaceName(addressDTO.getPlaceName());
        if(addressDTO.getCountry() != null) addressToUpdate.setCountry(addressDTO.getCountry());
        if(addressDTO.getAdditionalInfo() != null) addressToUpdate.setAdditionalInfo(addressDTO.getAdditionalInfo());
    }
}
