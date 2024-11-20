package com.web.gilproject.repository;

import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.PostDTO_YJ.PostDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GilListRepository extends JpaRepository<Post, Long> {

    /**
     * 삭제되지 않은 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.state=0")
    //@Query("select p from Post p left join p.postLikes pl group by p.id order by count(pl) desc")
    @Query("select p  from Post p left join p.postLikes pl left join p.replies r where (p.state = 0 and (r.state is null or r.state != 1)) group by (p.id ) order by size(pl) desc")
    //@Query("select p  from Post p left join p.postLikes pl left join p.replies r where p.state = 0  group by p.id order by size(pl) desc")
    //@Query(value = "select * from Post p left join Post_Like pl left join (select * from reply r where r.state!=1) where p.state=0 group by p.id order by size(pl) desc", nativeQuery = true)
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.state=0 group by p.id order by count(pl) desc")
    List<PostDTO> findAllPostDTO();

    /**
     * 작성자 닉네임이 일치하면서 삭제되지 않은 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.user.nickName = :nickName and p.state=0")
    //@Query("select p from Post p left join p.postLikes pl where p.user.nickName = :nickName and p.state = 0 group by p.id order by count(pl) desc")
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.nickName = :nickName and p.state = 0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.user.nickName = :nickName and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.user.nickName = :nickName and p.state=0 group by p.id order by count(pl) desc")
    List<PostDTO> findByNickName(@Param("nickName") String nickName);

    /**
     * 작성자 이름이 일치하면서 삭제되지 않은 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.user.name = :name and p.state=0")
    //@Query("select p from Post p left join p.postLikes pl where p.user.name = :name and p.state = 0 group by p.id order by count(pl) desc")
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.name = :name and p.state = 0 and (r is null or r.state = 0) group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.user.name = :name and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.user.name = :name and p.state=0 group by p.id order by count(pl) desc")
    List<PostDTO> findByName(@Param("name") String userName);

    /**
     * 제목이 키워드와 일치할 때 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.title like %:keyword% and p.state=0")
    //@Query("select p from Post p left join p.postLikes pl where p.title like %:keyword% and p.state = 0 group by p.id order by count(pl) desc")
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.title like %:keyword% and p.state = 0 and (r is null or r.state = 0) group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.title like %:keyword% and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.title like %:keyword% and p.state=0 group by p.id order by count(pl) desc")
    Set<PostDTO> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * 글 내용이 키워드와 일치할 때 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.content like %:keyword% and p.state=0")
    //@Query("select p from Post p left join p.postLikes pl where p.content like %:keyword% and p.state = 0 group by p.id order by count(pl) desc")
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.content like %:keyword% and p.state = 0 and (r is null or r.state = 0) group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.content like %:keyword% and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.content like %:keyword% and p.state=0 group by p.id order by count(pl) desc")
    Set<PostDTO> findByContentContaining(@Param("keyword") String keyword);

    /**
     * 글 작성자가 키워드와 일치할 때 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.user.nickName like %:keyword% and p.state=0")
    //@Query("select p from Post p left join p.postLikes pl where p.user.nickName like %:keyword% and p.state = 0 group by p.id order by count(pl) desc")
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.nickName like %:keyword% and p.state = 0 and (r is null or r.state = 0) group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.user.nickName like %:keyword% and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.user.nickName like %:keyword% and p.state=0 group by p.id order by count(pl) desc")
    Set<PostDTO> findByNickNameContaining(@Param("keyword") String keyword);

    /**
     * 글의 산책로 시작점이 키워드와 일치할 때 전체 게시글 조회
     * */
    //@Query("select p from Post p where p.path.startAddr like %:keyword% and p.state=0")
    //@Query("select p from Post p left join p.postLikes pl where p.path.startAddr like %:keyword% and p.state = 0 group by p.id order by count(pl) desc")
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.path.startAddr like %:keyword% and p.state = 0 and (r is null or r.state = 0) group by p.id order by count(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.path.startAddr like %:keyword% and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.path.startAddr like %:keyword% and p.state=0 group by p.id order by count(pl) desc")
    Set<PostDTO> findByStartAddrContaining(@Param("keyword") String keyword);

    /**
     * 작성자 id가 일치하는 전체 게시글 조회
     * */
    @Query("select p from Post p left join p.postLikes pl left join p.replies r where p.user.id = :userId and p.state=0 and (r is null or r.state=0) group by p.id order by count(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join (select r from p.replies r where r.state!=1) where p.user.id = :userId and p.state=0 group by p.id order by size(pl) desc")
    //@Query("select p from Post p left join p.postLikes pl left join p.replies r on r.state!=1 where p.user.id = :userId and p.state=0 group by p.id order by count(pl) desc")
    List<PostDTO> findByUserId(@Param("userId") Long userId);
}