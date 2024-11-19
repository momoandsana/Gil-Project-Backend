package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@Entity
@ToString(exclude="post")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "reply_id")
    @SequenceGenerator(name="reply_id",sequenceName = "reply_id_seq",allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="POST_ID",nullable = false)
    private Post post;

    private Integer state; //댓글 삭제 여부

    private String content;
    
    @OneToMany(mappedBy = "reply",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<ReplyLike> replyLikes;

}
