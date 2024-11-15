package com.web.gilproject.service;

import com.web.gilproject.dto.MailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "team.stackunderflow@gmail.com";
    private static int code;

    private MimeMessage CreateMail(String receiver){
        code = createCode();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, receiver);
            message.setSubject("[길따라] 인증번호");
            String body = "";
            body += "<h3>" + "요청하신 인증번호입니다." + "</h3>";
            body += "<h1>" + code + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");

            System.out.println("이메일 발송 성공");
        } catch (MessagingException e) {
            System.err.println("이메일 발송 실패: " + e.getMessage());
            //e.printStackTrace();
        }

        return message;
    }

    private int createCode(){
        return (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public int sendMail(MailDTO mailDto){
        MimeMessage message = CreateMail(mailDto.getReceiver());

        javaMailSender.send(message);

        return code;
    }
}
