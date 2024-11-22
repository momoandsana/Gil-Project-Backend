package com.web.gilproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;
import java.util.List;
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
    @JsonIgnore
    private User user;

    private Integer state; //경로 삭제 여부(softDelete)

    private String title;

    private String content;

    private LocalDateTime createdDate;

    private Integer time; //소요시간(sec)

    private Double distance;

    @Column(name = "start_lat")
    private Double startLat;

    @Column(name = "start_long")
    private Double startLong;

    private String startAddr;

    @Column(columnDefinition = "geometry(LineString, 4326)") //GIS에서 제공하는 수식
    private LineString route;

    @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Pin> pins;

    @OneToOne(mappedBy = "path",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private Post post;

    @OneToMany(mappedBy = "path",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private Set<WalkAlong> walkAlongs;
}
