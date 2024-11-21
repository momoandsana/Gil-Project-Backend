package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GilListRepository extends JpaRepository<Post, Long> {

    /**
     * 삭제되지 않은 전체 게시글 조회
     * */
    @Query("select p  from Post p left join p.postLikes pl left join p.replies r where p.state = 0 group by (p.id ) order by size(pl) desc")
    //List<PostDTO> findAllPostDTO();
    Page<Post> findAllPostDTO(Pageable pageable);

    /**
     * 작성자 닉네임이 일치하면서 삭제되지 않은 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.nickName = :nickName and p.state = 0 group by p.id order by size(pl) desc")
    //List<PostDTO> findByNickName(@Param("nickName") String nickName);
    Page<Post> findByNickName(@Param("nickName") String nickName, Pageable pageable);

    /**
     * 작성자 이름이 일치하면서 삭제되지 않은 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.name = :name and p.state = 0 group by p.id order by size(pl) desc")
    //List<PostDTO> findByName(@Param("name") String userName);
    Page<Post> findByName(@Param("name") String userName, Pageable pageable);

    /**
     * 제목이 키워드와 일치할 때 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.title like %:keyword% and p.state = 0 group by p.id order by size(pl) desc")
    //Set<PostDTO> findByTitleContaining(@Param("keyword") String keyword);
    Page<Post> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 글 내용이 키워드와 일치할 때 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.content like %:keyword% and p.state = 0 group by p.id order by size(pl) desc")
    //Set<PostDTO> findByContentContaining(@Param("keyword") String keyword);
    Page<Post> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 글 작성자가 키워드와 일치할 때 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.nickName like %:keyword% and p.state = 0 group by p.id order by size(pl) desc")
    //Set<PostDTO> findByNickNameContaining(@Param("keyword") String keyword);
    Page<Post> findByNickNameContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 글의 산책로 시작점이 키워드와 일치할 때 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.path.startAddr like %:keyword% and p.state = 0 group by p.id order by size(pl) desc")
    //Set<PostDTO> findByStartAddrContaining(@Param("keyword") String keyword);
    Page<Post> findByStartAddrContaining(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 작성자 id가 일치하는 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.id = :userId and p.state=0 group by p.id order by size(pl) desc")
    //List<PostDTO> findByUserId(@Param("userId") Long userId);
    Page<Post> findByUserId(@Param("userId") Long userId, Pageable pageable);
}