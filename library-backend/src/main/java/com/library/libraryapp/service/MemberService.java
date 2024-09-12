package com.library.libraryapp.service;

import com.library.libraryapp.dto.MemberDTO;

import java.util.List;

public interface MemberService {

   MemberDTO addMember(MemberDTO memberDTO);

   List<MemberDTO> getAllMembers();

   MemberDTO getMemberByID(Long id);
}
