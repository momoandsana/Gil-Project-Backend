package com.web.gilproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @Column(nullable = false,columnDefinition = "int default 0")
    private Long likesCount=0L;
    
    @OneToMany(mappedBy = "reply",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<ReplyLike> replyLikes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID",nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime writeDate;

    public static Reply of(String content,User user,Post post){
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setUser(user);
        reply.setPost(post);
        reply.setState(0);
        reply.setLikesCount(0L);
        return reply;
    }

}
