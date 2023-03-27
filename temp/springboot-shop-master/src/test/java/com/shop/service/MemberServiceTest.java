package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setName("홍길동");
        memberFormDto.setUsername("hong");
        memberFormDto.setPassword("1234");
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setAddress("서울시 OO구 OO동");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest() {
        Member member = this.createMember();

        Long savedId = memberService.saveMember(member);
        Member findMember = memberService.findOne(savedId).get();

        assertEquals(member.getName(), findMember.getName());
        assertEquals(member.getUsername(), findMember.getUsername());
        assertEquals(member.getPassword(), findMember.getPassword());
        assertEquals(member.getEmail(), findMember.getEmail());
        assertEquals(member.getAddress(), findMember.getAddress());
        assertEquals(member.getRole(), findMember.getRole());
    }

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest() {
        Member member1 = this.createMember();
        Member member2 = this.createMember();

        memberService.saveMember(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.saveMember(member2));
        assertThat(e.getMessage()).isEqualTo("이미 가입된 회원입니다.");
    }
}