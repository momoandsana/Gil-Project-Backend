package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.Reply;
import com.web.gilproject.domain.ReplyLike;
import com.web.gilproject.domain.User;
import com.web.gilproject.dto.ReplyDTO.ReplyDTO;
import com.web.gilproject.dto.ReplyDTO.ReplyPostRequestDTO;
import com.web.gilproject.exception.BoardErrorCode;
import com.web.gilproject.exception.BoardException;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.ReplyLikeRepository;
import com.web.gilproject.repository.ReplyRepository;
import com.web.gilproject.repository.UserRepository_JHW;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository_JHW userRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ReplyDTO> getRepliesByPostId(Long postId,Long userId) {
        Post post=boardRepository.findById(postId).orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));
//        List<Reply>replyEntities=replyRepository.findByPost(post);
        List<Reply>replyEntities=replyRepository.findByPostOrderByWriteDateDesc(post);

        return replyEntities.stream()
                .map(reply->ReplyDTO.from(reply,userId))
                .toList();
    }

    @Transactional
    public ReplyDTO createReply(Long postId, ReplyPostRequestDTO replyPostRequestDTO, Long userId) {
        Post post=boardRepository.findById(postId).orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));
        User user=userRepository.findById(userId).orElseThrow(()->new BoardException(BoardErrorCode.USER_NOT_FOUND));
        Reply reply=replyRepository.save(
                Reply.of(replyPostRequestDTO.content(),user,post)
        );

        //게시물 유저에게 댓글 알림
        notificationService.notifyComment(reply.getId());

        post.setRepliesCount(post.getRepliesCount()+1);
        return ReplyDTO.from(reply,userId);
    }

    @Transactional
    public void deleteReply(Long postId, Long replyId, Long userId)
    {
        Post postEntity=boardRepository.findById(postId).orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));

        Reply replyEntity=replyRepository.findById(replyId).orElseThrow(()->new BoardException(BoardErrorCode.REPLY_NOT_FOUND));

        // 본인이 작성한 댓글 아니면 삭제 불가능
        if(!replyEntity.getUser().getId().equals(userId))
        {
            throw new BoardException(BoardErrorCode.USER_NOT_ALLOWED);
        }

        replyRepository.delete(replyEntity);

        postEntity.setRepliesCount(postEntity.getRepliesCount()-1);

        boardRepository.save(postEntity);
    }

    @Transactional
    public void toggleLike(Long replyId, Long userId) {
        User userEntity=userRepository.findById(userId).orElseThrow(()->new BoardException(BoardErrorCode.USER_NOT_FOUND));
        Reply replyEntity=replyRepository.findById(replyId).orElseThrow(()->new BoardException(BoardErrorCode.REPLY_NOT_FOUND));
        Optional<ReplyLike> replyLike=replyLikeRepository.findByUserAndReply(userEntity,replyEntity);

        if(replyLike.isPresent())
        {
            replyLikeRepository.delete(replyLike.get());
            replyEntity.setLikesCount(replyEntity.getLikesCount()-1);
        }
        else
        {
            ReplyLike replyLikeEntity=ReplyLike.of(userEntity,replyEntity);
            replyLikeRepository.save(replyLikeEntity);
            replyEntity.setLikesCount(replyEntity.getLikesCount()+1);
        }

    }
}
