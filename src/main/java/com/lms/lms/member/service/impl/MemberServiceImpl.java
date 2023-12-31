package com.lms.lms.member.service.impl;

import com.lms.lms.components.MailComponents;
import com.lms.lms.member.entity.Member;
import com.lms.lms.member.exception.MemberNotEmailAuthException;
import com.lms.lms.member.model.MemberInput;
import com.lms.lms.member.model.ResetPasswordInput;
import com.lms.lms.member.repository.MemberRepository;
import com.lms.lms.member.service.MemberService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
        String uuid = UUID.randomUUID().toString();

        Member member = Member.builder()
            .userId(parameter.getUserId())
            .userName(parameter.getUserName())
            .phone(parameter.getPhone())
            .password(encPassword)
            .regDt(LocalDateTime.now())
            .emailAuthYn(false)
            .emailAuthKey(uuid)
            .build();

        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "LMS 사이트 가입을 축하드립니다.";
        String text = "<p>LMS 사이트 가입을 축하드립니다.</p> <p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
            + "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid+ "'> 가입 완료 </a></div>";
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

        if(member.isEmailAuthYn()) {
            return false;
        }

        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {
        Optional<Member> optionalMember =
            memberRepository.findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName());

        if(optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();
        String uuid = UUID.randomUUID().toString();

        member.setResetPasswordKey(uuid);
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "[LMS] 비밀번호 초기화 메일입니다.";
        String text = "<p>LMS 비밀번호 초기화 메일입니다.</p> <p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p>"
            + "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid+ "'> 비밀번호 초기화 링크 </a></div>";
        mailComponents.sendMail(email, subject, text);

        return false;
    }

    @Override
    public boolean resetPassword(String uuid, String password) {
        Optional<Member> optionalMember =
            memberRepository.findByResetPasswordKey(uuid);

        if(optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if (member.getResetPasswordLimitDt() == null) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        member.setPassword(encPassword);
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        memberRepository.save(member);

        return true;
    }

    @Override
    public boolean checkResetPassword(String uuid) {
        Optional<Member> optionalMember =
            memberRepository.findByResetPasswordKey(uuid);

        if(optionalMember.isEmpty()) {
            return false;
        }

        Member member = optionalMember.get();

        if (member.getResetPasswordLimitDt() == null) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("유효한 날짜가 아닙니다.");
        }

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember =
        memberRepository.findById(username);

        if(optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        if(!member.isEmailAuthYn()) {
            throw new MemberNotEmailAuthException("계정 활성화 이후에 로그인을 해주세요.");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if(member.isAdminYn()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }


        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
    }
}