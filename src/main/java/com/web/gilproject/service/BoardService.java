package com.web.gilproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.web.gilproject.domain.*;
import com.web.gilproject.dto.BoardDTO.BoardPathResponseDTO;
import com.web.gilproject.dto.BoardDTO.PostPatchRequestDTO;
import com.web.gilproject.dto.BoardDTO.PostRequestDTO;
import com.web.gilproject.dto.BoardDTO.PostResponseDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.exception.BoardErrorCode;
import com.web.gilproject.exception.BoardException;
import com.web.gilproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final PathRepository pathRepository;
    private final AmazonS3 amazonS3;
    private final UserRepository_JHW userRepository;
    private final AmazonService amazonService;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PathService pathService;
    private final ElasticsearchService elasticsearchService;
    private final NotificationService notificationService;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private static final String TMP_DIR=System.getProperty("java.io.tmpdir");


    @Transactional
    public List<BoardPathResponseDTO> getAllPathsById(Long userId) {
        List<Path> paths = pathRepository.findByUserId(userId);

        return paths.stream()
                .map(BoardPathResponseDTO::from)
                .collect(Collectors.toList());
        //return paths;
    }

    @Transactional
    public PostResponseDTO createPost(Long userId, PostRequestDTO postRequestDTO)
    {
        User user=userRepository.findById(userId)
                .orElseThrow(()->new BoardException(BoardErrorCode.USER_NOT_FOUND));

        Path path=pathRepository.findById(postRequestDTO.pathId())
                .orElseThrow(()->new BoardException(BoardErrorCode.PATH_NOT_FOUND));

        Post post=Post.builder()
                .title(postRequestDTO.title()!=null?postRequestDTO.title():"")// null 값이 들어가면 안 되기 때문에 아무것도 내용이 없다면 빈 문자열이 들어간다
                .content(postRequestDTO.content()!=null?postRequestDTO.content():"")
                .tag(postRequestDTO.tag()!=null?postRequestDTO.tag():"")
                .user(user)
                .path(path)
                .state(0)
                .readNum(0)
                .likesCount(0)
                .repliesCount(0)
                .build();


        Post dbpost = boardRepository.save(post);// 게시글 저장
        
        //작성자를 구독한 유저들에게 새로운 글 알림
        notificationService.notifyPost(dbpost.getId());

        List<String> imageUrls=new ArrayList<>();

        // 사용자가 사진을 보내면 postRequestDTO.images()를 리스트에 넣고, 사진을 보내지 않은 상태라면 빈 리스트를 만들어서 리스트에 넣는다
        List<MultipartFile> images = postRequestDTO.images() != null ? postRequestDTO.images() : new ArrayList<>();

        // 사용자가 게시글에 사진을 첨부하지 않는 경우
        if (!images.isEmpty()) {
            List<PostImage> postImages = new ArrayList<>();
            try{
                for (MultipartFile image : images)
                {
                    String imageUrl = amazonService.uploadFile(image, "upload_images/" + post.getId() + "/" + image.getOriginalFilename());
                    PostImage postImage = PostImage.builder()
                            .post(post)
                            .imageUrl(imageUrl)
                            .build();
                    postImages.add(postImage);
                    post.addPostImage(postImage);
                }
            }
            catch(IOException e)
            {
                throw new BoardException(BoardErrorCode.IMAGE_UPLOAD_FAILED);
            }

            // 빈 리스트가 아닌 경우에만 저장
            if (!postImages.isEmpty()) {
                postImageRepository.saveAll(postImages);
            }
        }

        //postImageRepository.saveAll(post.getPostImages());// 일괄 저장

        //엘라스틱서치 인덱싱 추가
        String re = elasticsearchService.indexDocument(
                "post-index", ""+post.getId(), Map.of("title", post.getTitle(), "content", post.getContent(), "startAddr", post.getPath().getStartAddr(), "nickName", post.getUser().getNickName())
        );
        System.out.println("re = " + re);

        return PostResponseDTO.from(post);
    }

    @Transactional
    public void deletePost(Long postId,Long userId)
    {
        Post postEntity=boardRepository
                .findById(postId)
                .orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));

        // 본인이 작성한 글만 삭제 가능
//        if(!postEntity.getUser().getId().equals(userId))
//        {
//            throw new BoardException(BoardErrorCode.USER_NOT_ALLOWED);
//        }

        Post post=boardRepository.findById(postId).get();
        boardRepository.delete(post);

        //엘라스틱서치 인덱싱 제거
        String re = elasticsearchService.deleteDocument("post-index", ""+post.getId());
        System.out.println("re = " + re);
    }

    @Transactional
    public void toggleLike(Long postId,Long userId)
    {
        Post postEntity=boardRepository.findById(postId).orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));
        User userEntity=userRepository.findById(userId).orElseThrow(()->new BoardException(BoardErrorCode.USER_NOT_FOUND));
        Optional<PostLike> postLike=postLikeRepository.findByUserAndPost(userEntity,postEntity);

        if(postLike.isPresent())
        {
            postLikeRepository.delete(postLike.get());
            postEntity.setLikesCount(Math.max(0,postEntity.getLikesCount()-1));
        }
        else
        {
            PostLike postLikeEntity=PostLike.of(userEntity,postEntity);
            postLikeRepository.save(postLikeEntity);
            postEntity.setLikesCount(Math.max(0,postEntity.getLikesCount()+1));
        }
    }

    @Transactional
    public PostResDTO postDetails(Long postId,Long userId)
    {
        Post postEntity=boardRepository.findById(postId).orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));

        // 본인이 작성한 글 조회하면 조회수 오르지 않는다
        if(!postEntity.getUser().getId().equals(userId))
        {
            postEntity.setReadNum(postEntity.getReadNum()+1);
        }

        PostResDTO postResDTO=new PostResDTO(postEntity,userId);
        postResDTO.setPathResDTO(pathService.decodingPath(postEntity.getPath()));
        return postResDTO;
    }

    @Transactional
    public void updatePost(Long postId, Long userId, PostPatchRequestDTO postPatchRequestDTO)
    {
        //기존 엘라스틱서치 인덱싱 제거
        String re = elasticsearchService.deleteDocument("post-index", ""+postId);
        System.out.println("re = " + re);

        Post postEntity=boardRepository.findById(postId).orElseThrow(()->new BoardException(BoardErrorCode.POST_NOT_FOUND));
        User userEntity=userRepository.findById(userId).orElseThrow(()->new BoardException(BoardErrorCode.USER_NOT_FOUND));

        if(!userEntity.equals(postEntity.getUser()))
        {
            throw new BoardException(BoardErrorCode.USER_NOT_ALLOWED);
        }

        if(postPatchRequestDTO.title()!=null)
        {
            postEntity.setTitle(postPatchRequestDTO.title());
        }

        if(postPatchRequestDTO.content()!=null)
        {
            postEntity.setContent(postPatchRequestDTO.content());
        }

        if(postPatchRequestDTO.tag()!=null)
        {
            postEntity.setTag(postPatchRequestDTO.tag());
        }

        List<String> deleteUrls=postPatchRequestDTO.deleteUrls();

        // 사용자가 사진을 삭제하는 경우
        if(deleteUrls!=null && !deleteUrls.isEmpty())
        {
            for(String url:deleteUrls)
            {
                try
                {
                    amazonService.deleteFile(url);
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }

                PostImage postImage=postImageRepository.findByImageUrl(url)
                        .orElseThrow(()->new BoardException(BoardErrorCode.IMAGE_UPLOAD_FAILED));
                postEntity.removePostImage(postImage);
                //postImageRepository.delete(postImage); , orphanRemoval=true 이므로
            }
        }

        List<MultipartFile> newImages=postPatchRequestDTO.newImages();

        // 사용자가 사진을 추가한 경우
        if(newImages!=null && !newImages.isEmpty())
        {
            for(MultipartFile file:newImages)
            {
                String fileName = "upload_images/" + postId + "/" + file.getOriginalFilename();
                try{
                    String imageUrl= amazonService.uploadFile(file,fileName);
                    PostImage postImage=PostImage.builder()
                            .post(postEntity)
                            .imageUrl(imageUrl)
                            .build();

                    postEntity.addPostImage(postImage);
                }
                catch(IOException e)
                {
                    throw new BoardException(BoardErrorCode.FILE_WRITE_FAILED);
                }
            }

        }

        boardRepository.save(postEntity);
        //엘라스틱서치 인덱싱 추가
        re = elasticsearchService.indexDocument(
                "post-index", ""+postId, Map.of("title", postEntity.getTitle(), "content", postEntity.getContent(), "startAddr", postEntity.getPath().getStartAddr(), "nickName", postEntity.getUser().getNickName())
        );
        System.out.println("re = " + re);
    }

}













