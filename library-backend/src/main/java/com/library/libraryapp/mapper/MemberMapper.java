package com.library.libraryapp.mapper;

import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.entity.Member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberMapper {
    // Map member entity to dto
    public static MemberDTO mapToMemberDTO(Member member){
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setFirstName(member.getFirstName());
        dto.setLastName(member.getLastName());

        // store LocalDate as String
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (member.getDateOfBirth() != null)
            dto.setDateOfBirth(member.getDateOfBirth().format(formatter));

        // map postal address
        if(member.getPostalAddress() != null)
            dto.setAddress(AddressMapper.mapToAddressDTO(member.getPostalAddress()));

        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());
        dto.setBarcodeNumber(member.getBarcodeNumber());

        if(member.getMembershipStarted() != null)
            dto.setMembershipStarted(member.getMembershipStarted().format(formatter));

        if(member.getMembershipEnded() != null)
            dto.setMembershipEnded(member.getMembershipEnded().format(formatter));

        dto.setIsActive(member.getIsActive());

        return dto;
    }
    // Map dto to entity
    public static Member mapToMemberEntity(MemberDTO memberDTO){
        Member member = new Member();

        member.setId(memberDTO.getId());
        member.setFirstName(memberDTO.getFirstName());
        member.setLastName(memberDTO.getLastName());

        // store String as LocalDate
        member.setDateOfBirth(LocalDate.parse(memberDTO.getDateOfBirth()));

        // postal address mapping
        if(memberDTO.getAddress() != null)
            member.setPostalAddress(AddressMapper.mapToAddressEntity(memberDTO.getAddress()));

        member.setEmail(memberDTO.getEmail());
        member.setPhone(memberDTO.getPhone());
        member.setBarcodeNumber(memberDTO.getBarcodeNumber());
        if(memberDTO.getMembershipStarted() != null)
            member.setMembershipStarted(LocalDate.parse(memberDTO.getMembershipStarted()));
        if(memberDTO.getMembershipEnded() != null)
            member.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded()));

        member.setIsActive(memberDTO.getIsActive());

        return member;
    }
}
