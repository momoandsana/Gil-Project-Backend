package com.web.gilproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "walk_alongs")
@ToString(exclude = {"user","path"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkAlongs {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "walk_alongs_id")
    @SequenceGenerator(name="walk_alongs_id",sequenceName = "walk_alongs_id_seq",allocationSize = 1)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PATH_ID",nullable = false)
    @JsonIgnore
    private Path path;

}
