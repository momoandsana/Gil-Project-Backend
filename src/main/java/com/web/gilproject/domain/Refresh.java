package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "refresh_token_id")
    @SequenceGenerator(name="refresh_token_id",sequenceName = "refresh_token_id_seq",allocationSize = 1)
    private Long id;

    private Long userId;
    private String refreshToken;
    private LocalDateTime expiration; //만료날짜
}
