package com.library.libraryapp.service;

import com.library.libraryapp.dto.MemberDTO;

import java.util.List;

public interface MemberService {

   MemberDTO addMember(MemberDTO memberDTO);

   List<MemberDTO> getAllMembers();

   MemberDTO getMemberByID(Long id);

   MemberDTO updateMember(MemberDTO memberDTO);

   void deleteMember(Long id);

   List<MemberDTO> findMembersByCriteria(Long id, String firstName, String lastName, String barcodeNumber);
}
