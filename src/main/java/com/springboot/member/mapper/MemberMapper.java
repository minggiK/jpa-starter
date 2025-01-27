package com.springboot.member.mapper;

import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.dto.MemberPostDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    Member memberPostDtoToMember(MemberPostDto memberPostDto);
    Member memberPatchDtoToMember(MemberPatchDto memberPatchDto);

    List<MemberResponseDto> membersToMemberResponseDtos(List<Member> members);

    default MemberResponseDto memberToMemberResponseDto(Member member){

        MemberResponseDto responseDto = new MemberResponseDto(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getMemberStatus(),
                member.getStamp().getStampCount()
        );
        return responseDto;
    }
}
