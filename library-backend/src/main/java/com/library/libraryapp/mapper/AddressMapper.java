package com.library.libraryapp.mapper;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.entity.PostalAddress;

public class AddressMapper {
    // map Entity to DTO
    public static AddressDTO mapToAddressDTO(PostalAddress postalAddress){
        AddressDTO dto = new AddressDTO();
        dto.setId(postalAddress.getId());
        dto.setStreetName(postalAddress.getStreetName());
        dto.setStreetNumber(postalAddress.getStreetNumber());
        dto.setZipCode(postalAddress.getZipCode());
        dto.setPlaceName(postalAddress.getPlaceName());
        dto.setCountry(postalAddress.getCountry());
        dto.setAdditionalInfo(postalAddress.getAdditionalInfo());
        return dto;
    }
    // map DTO to Enity
    public static PostalAddress mapToAddressEntity(AddressDTO dto){
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setId(dto.getId());
        postalAddress.setStreetName(dto.getStreetName());
        postalAddress.setStreetNumber(dto.getStreetNumber());
        postalAddress.setPlaceName(dto.getPlaceName());
        postalAddress.setZipCode(dto.getZipCode());
        postalAddress.setCountry(dto.getCountry());
        postalAddress.setAdditionalInfo(dto.getAdditionalInfo());
        return postalAddress;
    }
}
