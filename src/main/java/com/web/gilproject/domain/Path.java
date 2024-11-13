package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Path {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "path_id")
    @SequenceGenerator(name="path_id",sequenceName = "path_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    private Integer state;

    private String title;

    private String content;

    @CreationTimestamp
    private LocalDateTime createdDate;

    private Integer time;

    private double distance;

    @Column(name = "start_lat")
    private double startLat;

    @Column(name = "start_long")
    private double startLong;

    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString route;

    @OneToMany(mappedBy = "path",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Pin> pins;

    @OneToOne(mappedBy = "path",cascade = CascadeType.ALL,orphanRemoval = true)
    private Post post;
}
