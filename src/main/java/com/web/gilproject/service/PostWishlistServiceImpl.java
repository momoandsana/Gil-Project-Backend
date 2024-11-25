package com.web.gilproject.service;

import com.web.gilproject.domain.Post;
import com.web.gilproject.domain.PostWishlist;
import com.web.gilproject.domain.User;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.PostWishlistRepository;
import com.web.gilproject.repository.UserRepository_emh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostWishlistServiceImpl implements PostWishlistService {

     private final PostWishlistRepository postWishlistRepository;
     private final BoardRepository boardRepository;
     private final UserRepository_emh userRepository;


    @Transactional
    @Override
    public int togglePostWishlist(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElse(null);
        Post post = boardRepository.findById(postId).orElse(null);
        log.info("userId={}, postId={}", userId, postId);
        PostWishlist dbWishlist = postWishlistRepository.findByUserIdAndPostId(userId,postId);

        //이미 찜목록에 있는건 삭제
        if(dbWishlist != null) {
            log.info("찜 목록에서 삭제");
            postWishlistRepository.delete(dbWishlist);
            return 0;
        }
        
        //없었다면 찜목록에 추가
        log.info("찜 목록에 추가");
        PostWishlist postWishlist= PostWishlist.builder().user(user).post(post).build();
        postWishlistRepository.save(postWishlist);
        return 1;
    }
}
