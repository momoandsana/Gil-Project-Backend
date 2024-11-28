package com.web.gilproject.service;

import com.web.gilproject.dto.MailDTO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "team.stackunderflow@gmail.com";

    private MimeMessage CreateMail(MailDTO mailDTO) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mailDTO.getReceiver());
            message.setSubject("[길따라] " + mailDTO.getSubject());
            String body = "";
            body += "<h3>" + mailDTO.getTitle() + "</h3>";
            body += "<h1>" + mailDTO.getContent() + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");

            System.out.println("이메일 발송 성공");
        } catch (MessagingException e) {
            System.err.println("이메일 발송 실패: " + e.getMessage());
            //e.printStackTrace();
        }

        return message;
    }

    /**
     * 랜덤 인증코드 생성
     * @return
     */
    private int createCode(){
        return (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    /**
     * 랜덤 비밀번호 생성
     * @return
     */
    private String createRandomPassword(){
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public int sendMail(MailDTO mailDto){
        int code = createCode();

        MailDTO m = MailDTO.builder()
                .receiver(mailDto.getReceiver())
                .subject("인증번호")
                .title("요청하신 인증번호입니다.")
                .content(code)
                .build();

        MimeMessage message = CreateMail(m);

        javaMailSender.send(message);

        return code;
    }

    /**
     * 새로 발급된 비밀번호 이메일로 전송
     * @param mailDto
     */
    public String sendPasswordMail(MailDTO mailDto){
        String password = createRandomPassword();

        MailDTO m = MailDTO.builder()
                .receiver(mailDto.getReceiver())
                .subject("비밀번호 재발급")
                .title("재발급된 비밀번호 안내드립니다. 로그인 후 반드시 비밀번호 변경을 해주세요.")
                .content(password)
                .build();

        MimeMessage message = CreateMail(m);
        javaMailSender.send(message);

        return password;
    }
}
