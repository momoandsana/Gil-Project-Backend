package com.web.gilproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailDTO {
    private String receiver; //받는사람
    private String name; //받는사람 이름
    private String subject; //메일 제목
    private String title; //메일 내용 헤더
    private Object content; //메일 내용
}
