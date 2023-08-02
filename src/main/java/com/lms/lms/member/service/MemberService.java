package com.lms.lms.member.service;

import com.lms.lms.member.model.MemberInput;
import org.springframework.stereotype.Service;


public interface MemberService {

    boolean register(MemberInput parameter);

    boolean emailAuth(String uuid);

}
