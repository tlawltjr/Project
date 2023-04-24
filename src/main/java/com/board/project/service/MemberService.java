package com.board.project.service;

import com.board.project.dto.ClubAuthMemberDTO;
import com.board.project.dto.MemberAdapter;
import com.board.project.dto.MemberDTO;
import com.board.project.entity.Member;
import com.board.project.entity.MemberRole;
import com.board.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Member dtoToEntity(MemberDTO memberDTO){

        Member member = Member.builder()
                .loginId(memberDTO.getLoginId())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .name(memberDTO.getName())
                .email(memberDTO.getEmail())
                .phone(memberDTO.getPhone())
                .addr(memberDTO.getAddr())
                .detailaddr(memberDTO.getDetailaddr())
                .role(MemberRole.ADMIN.getValue())
                .build();


        memberRepository.save(member);

        return member;

    }
    
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(()
        -> new UsernameNotFoundException("회원 정보가 없습니다."));


        return new MemberAdapter(member);
    }

    @Transactional
    public Member updateMember(Member member){

        Member members = memberRepository.findByLoginId(member.getLoginId()).orElseThrow(()
        -> new UsernameNotFoundException("수정할 회원 정보가 없습니다."));

        members.setPassword(passwordEncoder.encode(member.getPassword()));
        members.setName(member.getName());
        members.setEmail(member.getEmail());
        members.setPhone(member.getPhone());
        members.setAddr(member.getAddr());
        members.setDetailaddr(member.getDetailaddr());
        members.setRole(member.getRole());

        memberRepository.save(members);


        return member;
    }
}