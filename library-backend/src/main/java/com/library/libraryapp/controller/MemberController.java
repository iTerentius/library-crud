package com.library.libraryapp.controller;

import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/members")
public class MemberController {

    private MemberService memberService;

    @PostMapping("addMember")
    public ResponseEntity<MemberDTO> addMember(@RequestBody MemberDTO memberDTO){
       MemberDTO savedMember = memberService.addMember(memberDTO);
       return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }
}
