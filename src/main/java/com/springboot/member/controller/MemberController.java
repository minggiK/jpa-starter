package com.springboot.member.controller;

import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v11/members")
@Validated
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/v11/members";
    private final MemberService memberService;
    private final MemberMapper mapper;

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postMember(@Valid @RequestBody MemberPostDto memberPostDto) {
        Member member = memberService.createMember(mapper.memberPostDtoToMember(memberPostDto));
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@Valid @RequestBody MemberPatchDto memberPatchDto,
                                      @Positive @PathVariable("member-id") long memberId) {
       memberPatchDto.setMemberId(memberId);
        Member member = memberService.updateMemeber(mapper.memberPatchDtoToMember(memberPatchDto));

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@Positive@PathVariable("member-id") long memberId) {
        Member member = memberService.findMember(memberId);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity getMembers(@Positive @RequestParam("page") int page,
                                     @Positive @RequestParam("size") int size) {
        Page<Member> memberPage = memberService.findMembers(page, size);
        //page객체가 가진 를 List로 반환 -> mapper에서 responseDto를 List로 갖고있음
        List<Member> members = memberPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.membersToMemberResponseDtos(members), memberPage),
                HttpStatus.OK
        );
    }

    @DeleteMapping
    public ResponseEntity deleteMember(@Positive @PathVariable("member-id") long memberId){
        memberService.deleteMember(memberId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

