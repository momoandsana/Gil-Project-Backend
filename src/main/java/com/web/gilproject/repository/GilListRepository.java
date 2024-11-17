package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.PostDTO_YJ.PostDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GilListRepository extends JpaRepository<Post, Long> {

    /**
     * 삭제되지 않은 전체 게시글 조회
     * */
    @Query("select p from Post p where p.state=0")
    List<Post> findAll();

    /**
     * 작성자 닉네임이 일치하면서 삭제되지 않은 전체 게시글 조회
     * */
    @Query("select p from Post p where p.user.nickName = :nickName and p.state=0")
    List<PostDTO> findByNickName(@Param("nickName") String nickName);

    /**
     * 작성자 이름이 일치하면서 삭제되지 않은 전체 게시글 조회
     * */
    @Query("select p from Post p where p.user.name = :name and p.state=0")
    List<PostDTO> findByName(@Param("name") String userName);
}