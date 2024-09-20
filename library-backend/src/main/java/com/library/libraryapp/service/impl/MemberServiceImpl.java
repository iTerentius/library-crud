package com.library.libraryapp.service.impl;

import com.library.libraryapp.dto.AddressDTO;
import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.entity.Book;
import com.library.libraryapp.entity.Member;
import com.library.libraryapp.entity.PostalAddress;
import com.library.libraryapp.exception.ResourceNotFoundException;
import com.library.libraryapp.mapper.AddressMapper;
import com.library.libraryapp.mapper.MemberMapper;
import com.library.libraryapp.repository.AddressRepository;
import com.library.libraryapp.repository.MemberRepository;
import com.library.libraryapp.service.AddressService;
import com.library.libraryapp.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private AddressRepository addressRepository;

    private MemberRepository memberRepository;

    private AddressServiceImpl addressService;

    @PersistenceContext
    private EntityManager entityManager;

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
    public MemberDTO getMemberByID(Long memberId) {

//        Optional<Member> optionalMember = memberRepository.findById(id);
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new ResourceNotFoundException("Member", "ID", memberId));

        return MemberMapper.mapToMemberDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(MemberDTO memberDTO) {
        // 1. find existing member by id
        Optional<Member> optionalMember = memberRepository.findById(memberDTO.getId());
        Member memberToUpdate = optionalMember.orElseThrow(
                () -> new ResourceNotFoundException("Member", "ID", memberDTO.getId())
        );

        // 2. do partial update of the member (only non-null fields)
        updateMemberEntityFromDTO(memberToUpdate, memberDTO);

        // 3. save update member to DB
        memberRepository.save(memberToUpdate);

        // 4. return updated member (DTO)
        return MemberMapper.mapToMemberDTO(memberToUpdate);
    }

    @Override
    public void deleteMember(Long memberId) {
        if(!memberRepository.existsById(memberId)){
            throw new ResourceNotFoundException("Member", "ID", memberId);
        }
        memberRepository.deleteById(memberId);
    }

    @Override
    public List<MemberDTO> findMembersByCriteria(Long id, String firstName, String lastName, String barcodeNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Member> cq = cb.createQuery(Member.class);
        Root<Member> memberRoot = cq.from(Member.class);
        List<Predicate> predicates = new ArrayList<>();

        if(id != null) predicates.add(cb.equal(memberRoot.get("id"), id));
        if(firstName != null)
            predicates.add(cb.like(cb.lower(memberRoot.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        if(lastName != null)
            predicates.add(cb.like(cb.lower(memberRoot.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        if(barcodeNumber != null)
            predicates.add(cb.like(cb.lower(memberRoot.get("barcodeNumber")), "%" + barcodeNumber.toLowerCase() + "%"));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Member> result = entityManager.createQuery(cq).getResultList();

        return result.stream()
                .map(MemberMapper::mapToMemberDTO)
                .collect(Collectors.toList());
    }

    private void updateMemberEntityFromDTO(Member memberToUpdate, MemberDTO memberDTO) {
        if(memberDTO.getFirstName() != null) memberToUpdate.setFirstName(memberDTO.getFirstName());
        if(memberDTO.getLastName() != null) memberToUpdate.setLastName(memberDTO.getLastName());
        if(memberDTO.getDateOfBirth() != null) memberToUpdate.setDateOfBirth(LocalDate.parse(memberDTO.getDateOfBirth()));
        if(memberDTO.getEmail() != null) memberToUpdate.setEmail(memberDTO.getEmail());
        if(memberDTO.getPhone() != null) memberToUpdate.setPhone(memberDTO.getPhone());
        if(memberDTO.getBarcodeNumber() != null) memberToUpdate.setBarcodeNumber(memberDTO.getBarcodeNumber());
        if(memberDTO.getMembershipStarted() != null) memberToUpdate.setMembershipStarted(LocalDate.parse(memberDTO.getMembershipStarted()));

        // the member is active if membershipEnded = null;
        if(memberDTO.getMembershipEnded() != null) {
            if (memberDTO.getMembershipEnded().isEmpty()){
                memberToUpdate.setMembershipEnded(null);
                memberToUpdate.setIsActive(true);
            } else {
                memberToUpdate.setMembershipEnded(LocalDate.parse(memberDTO.getMembershipEnded()));
                memberToUpdate.setIsActive(false);
            }
        }
        // udpate the address
        if(memberDTO.getAddress() != null){
            // if the member already has an address, update it
            // otherwise create a new PostalAddress entity
            PostalAddress addressToUpdate;

            if(memberToUpdate.getPostalAddress() != null){
                addressToUpdate = memberToUpdate.getPostalAddress();
            } else {
                addressToUpdate = new PostalAddress();
            }
            // to update PostalAddress entity, we will use our existing address service
            addressService.updateAddressEntityFromDTO(addressToUpdate, memberDTO.getAddress());
            // save the updated address to DB
            addressRepository.save(addressToUpdate);
            // associate the updated address with the member
            memberToUpdate.setPostalAddress(addressToUpdate);
        }
    }
}
