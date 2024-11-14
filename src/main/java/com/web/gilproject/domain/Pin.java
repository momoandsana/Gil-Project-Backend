package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.awt.print.Pageable;

@Data
@Entity
@Builder
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pin_id")
    @SequenceGenerator(name="pin_id",sequenceName = "pin_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="path_id",nullable = false)
    private Path path;

    private String imageUrl;

    private String content;

    private Double latitude;

    private Double longitude;
}
