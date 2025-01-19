package com.springboot.member.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.Stamp;
import com.springboot.member.repository.MemberRepository;
import com.springboot.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final OrderService orderService;

    public MemberService(MemberRepository memberRepository, OrderService orderService) {
        this.memberRepository = memberRepository;
        this.orderService = orderService;
    }

    public Member createMember(Member member) {
        //새로운 회원이 맞는지 확인
        verifyExistsEmail(member.getEmail());
        //회원가입 동시에 stamp 생성 , new 사용 안되는 이유??
        member.setStamp(new Stamp());
        //새로 생성하면 레파지토리에 등록
        return memberRepository.save(member);
    }

    public Member updateMemeber(Member member) {
        //이미 존재하는 member가 맞는지
        Member findMember = findVerfiedMember(member.getMemberId());

        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getPhone())
                .ifPresent(phone -> findMember.setPhone(phone));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));

        return memberRepository.save(findMember);

    }


    public Member findMember(Long memberId) {

        return findVerfiedMember(memberId);
    }

    public Page<Member> findMembers(int page, int size) {
        //없으면 빈 페이지를 반환해야해
       return memberRepository.findAll(PageRequest.of(page, size, Sort.by("memberId").descending()));

    }

    public void deleteMember (Long memberId) {

        Member member = findVerfiedMember(memberId);
        memberRepository.delete(member);
    }

    //email 중복확인 : 이미 있다면 예외처리
    public void verifyExistsEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(optionalMember.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    //수정, 존재하는 member인지 확인
    public Member findVerfiedMember(long memberId) {
        Optional<Member> findMemberId = memberRepository.findById(memberId);
        return findMemberId.orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
