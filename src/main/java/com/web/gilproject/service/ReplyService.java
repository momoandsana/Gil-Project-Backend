package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.Reply;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.ReplyDTO.ReplyDTO;
import com.web.gilproject.dto.ReplyDTO.ReplyPostRequestDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.ReplyRepository;
import com.web.gilproject.repository.UserRepository_JHW;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository_JHW userRepository;

    public List<ReplyDTO> getRepliesByPostId(Long postId) {
        Post post=boardRepository.findById(postId).orElseThrow(()->new RuntimeException("No post found"));
        List<Reply>replyEntities=replyRepository.findByPost(post);
        return replyEntities.stream().map(ReplyDTO::from).toList();
    }


    public ReplyDTO createReply(Long postId, ReplyPostRequestDTO replyPostRequestDTO, Long userId) {
        Post post=boardRepository.findById(postId).orElseThrow(()->new RuntimeException("No post found"));
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No user found"));
        Reply reply=replyRepository.save(
                Reply.of(replyPostRequestDTO.content(),user,post)
        );

        post.setRepliesCount(post.getRepliesCount()+1);
        return ReplyDTO.from(reply);
    }


}
