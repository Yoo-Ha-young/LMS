package com.lms.lms.member.service.impl;

import com.lms.lms.components.MailComponents;
import com.lms.lms.member.entity.Member;
import com.lms.lms.member.model.MemberInput;
import com.lms.lms.member.repository.MemberRepository;
import com.lms.lms.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;

    @Override
    public boolean register(MemberInput parameter) {

        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());

        if(optionalMember.isPresent()){
            return false;
        }

        String uuid = UUID.randomUUID().toString();
        Member member = Member.builder()
            .userId(parameter.getUserId())
            .userName(parameter.getUserId())
            .phone(parameter.getPhone())
            .password(parameter.getPassword())
            .regDt(LocalDateTime.now())
            .emailAuthYn(false)
            .emailAuthKey(uuid)
            .build();

        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "LMS 사이트 가입을 축하드립니다.";
        String text = "<p>LMS 사이트 가입을 축하드립니다.</p> <p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
            + "<div><a href='http://localhost:8080/member/email-auth?id=" + uuid+ "'> 가입 완료 </a></div>";
        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean emailAuth(String uuid) {
        Optional<Member> optionalMember =
        memberRepository.findByEmailAuthKey(uuid);

        if(optionalMember.isEmpty()) {
            return false;
        }

        Member member = optionalMember.get();
        member.setEmailAuthKey(String.valueOf(true));
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }
}