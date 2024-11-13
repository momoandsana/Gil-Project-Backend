package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "notice_id")
    @SequenceGenerator(name="notice_id",sequenceName = "notice_id_seq",allocationSize = 1)
    private Long id;

    private String title;

    private String content;

}
