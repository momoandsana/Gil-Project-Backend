package com.web.gilproject.controller;

import com.web.gilproject.domain.User;
import com.web.gilproject.dto.MailDTO;
import com.web.gilproject.service.JoinService;
import com.web.gilproject.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final JoinService joinService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String mailPage() {
        return "Mail";
    }

    @PostMapping("/send")
    public int mailSend(@RequestBody MailDTO mailDto) {
        return mailService.sendMail(mailDto);
    }

    @PostMapping("/pwsend")
    public int mailSendPw(@RequestBody MailDTO mailDto) {
//        System.out.println(mailDto.getName());
//        System.out.println(mailDto.getReceiver());
        User user = User.builder().name(mailDto.getName()).email(mailDto.getReceiver()).build();

        if (joinService.existEmailUser(user)) {
            System.out.println("이름과 이메일로 된 이메일 계정이 있음");
            String password = mailService.sendPasswordMail(mailDto);
            password = bCryptPasswordEncoder.encode(password);

            return joinService.updateUserPassword(user, password);
        } else {
            System.out.println("유저 확인안됨");
            return 0;
        }

    }
}
