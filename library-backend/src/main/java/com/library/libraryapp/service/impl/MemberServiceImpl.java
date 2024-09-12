package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.entity.Member;
import com.library.libraryapp.entity.PostalAddress;
import com.library.libraryapp.mapper.AddressMapper;
import com.library.libraryapp.mapper.MemberMapper;
import com.library.libraryapp.repository.AddressRepository;
import com.library.libraryapp.repository.MemberRepository;
import com.library.libraryapp.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private AddressRepository addressRepository;

    private MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberDTO addMember(MemberDTO memberDTO) {
        PostalAddress postalAddress = new PostalAddress();
        // first we have to deal with postal_address
        AddressDTO addressDTO = memberDTO.getAddress();
        if(addressDTO != null) {
            postalAddress = AddressMapper.mapToAddressEntity(addressDTO);
            postalAddress = addressRepository.save(postalAddress);
        }
        // convert memberDTO to member Entity
        Member member = MemberMapper.mapToMemberEntity(memberDTO);

        // add the address in the Entity (set postal address ID field in DB)
        if(postalAddress != null) member.setPostalAddress(postalAddress);

        // save the Entity in the DB
        member = memberRepository.save(member);

        // convert the Entity back to DTO and return it
        return MemberMapper.mapToMemberDTO(member);
    }

    @Override
    public List<MemberDTO> getAllMembers() {

        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberMapper::mapToMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO getMemberByID(Long id) {

        Optional<Member> optionalMember = memberRepository.findById(id);

        Member member = optionalMember.get();

        return MemberMapper.mapToMemberDTO(member);
    }
}
