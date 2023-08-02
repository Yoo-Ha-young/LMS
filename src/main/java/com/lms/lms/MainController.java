package com.lms.lms;

import com.lms.lms.components.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MailComponents mailComponents;

    @RequestMapping("/")
    public String index() {

//        String email = "bluekitten74@naver.com";
//        String subject = "안녕하세요";
//        String text = "<p>안녕하세요.</p><p>반값습니다.</p>";
//        mailComponents.sendMail(email, subject, text);

        return "index";
    }

}
