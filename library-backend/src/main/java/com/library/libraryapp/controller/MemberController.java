package com.library.libraryapp.controller;

import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("listAll")
    public ResponseEntity<List<MemberDTO>> getAllMembers(){
       List<MemberDTO> allMembers = memberService.getAllMembers();
       return new ResponseEntity<>(allMembers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberDTO> getMemberByID(@PathVariable Long id){
       MemberDTO memberDTO = memberService.getMemberByID(id);
       return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }
}
