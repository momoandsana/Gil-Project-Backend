package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "path_id")
    @SequenceGenerator(name="path_id",sequenceName = "path_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    private Integer state;
    // 수정 필요!! LineString
    private String route;

    private String title;

    private String content;

    @CreationTimestamp
    private LocalDateTime createdDate;

    private Integer time;

    private Double startLat;

    private Double startLong;

    @OneToMany(mappedBy = "path",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Pin> pins;

    @OneToOne(mappedBy = "path",cascade = CascadeType.ALL,orphanRemoval = true)
    private Post post;
}
