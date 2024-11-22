package com.web.gilproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@ToString(exclude = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkAlong {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "walk_along_id")
    @SequenceGenerator(name="walk_along_id",sequenceName = "walk_along_id_seq",allocationSize = 1)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    @JsonIgnore
    private User user;

    private Integer time;

    private Double distance;

}
