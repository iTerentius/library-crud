package com.library.libraryapp.controller;

import com.library.libraryapp.dto.MemberDTO;
import com.library.libraryapp.service.MemberService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/members")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private MemberService memberService;

    @PostMapping("addMember")
    public ResponseEntity<MemberDTO> addMember(@RequestBody MemberDTO memberDTO){
        logger.info("Trying to add member...");
       MemberDTO savedMember = memberService.addMember(memberDTO);
       return new ResponseEntity<>(savedMember, HttpStatus.CREATED);
    }

    @GetMapping("listAll")
    public ResponseEntity<List<MemberDTO>> getAllMembers(){
        logger.info("Listing all members...");
       List<MemberDTO> allMembers = memberService.getAllMembers();
       return new ResponseEntity<>(allMembers, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberDTO> getMemberByID(@PathVariable Long id){
        logger.info("Trying to get member by id: {}", id);
       MemberDTO memberDTO = memberService.getMemberByID(id);
       return new ResponseEntity<>(memberDTO, HttpStatus.OK);
    }

    @PatchMapping("updateMember/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO){
        logger.info("Trying to update member by id: {}", id);
        memberDTO.setId(id);
        MemberDTO updatedMember = memberService.updateMember(memberDTO);
        return new ResponseEntity<>(updatedMember, HttpStatus.OK);
    }

    @DeleteMapping("deleteMember/{id}")
    public ResponseEntity<String> deleteMemberById(@PathVariable Long id){
        logger.info("Trying to delete member by id: {}", id);
        memberService.deleteMember(id);
        return new ResponseEntity<>("Member has been successfully deleted.", HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<MemberDTO>> searchMembers(
            @RequestParam(required = false) Long cardNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String barcodeNumber
    ){
        logger.info("Searching members...");
        List<MemberDTO> members = memberService.findMembersByCriteria(cardNumber, firstName, lastName, barcodeNumber);

        return new ResponseEntity<>(members, HttpStatus.OK);
    }
}
