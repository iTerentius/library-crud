package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.entity.PostalAddress;
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
        PostalAddress postalAddress = optionalPostalAddress.get();
        return AddressMapper.mapToAddressDTO(postalAddress);
    }
}
