package com.web.gilproject.controller;

import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.BoardDTO.*;
import com.web.gilproject.dto.PathDTO;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.dto.PathResDTO;
import com.web.gilproject.dto.PostDTO_YJ.PostResDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.PathService;

import com.web.gilproject.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class BoardController {
    private final PathService pathService;
    private final BoardService boardService;
    private final AmazonService s3Service;

    private final BoardRepository boardRepository;


    /**
     * 게시글 작성에서 사용자 아이디에 맞는 경로 얻기
     *
     * @param authentication
     * @return
     */
    //@GetMapping("/{userId}/paths")
    @GetMapping("/paths")
    public ResponseEntity<List<PathResDTO>> getAllPaths(Authentication authentication) {
        CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customMemberDetails.getId();
       // List<BoardPathResponseDTO> boardPathListDTO = boardService.getAllPathsById(userId);

        List<PathResDTO> pathListDTO=pathService.findPathByUserIdTransform(userId);
//        for (BoardPathDTO boardPathDTO : boardPathListDTO) {
//            System.out.println(boardPathDTO);
//        }
        return ResponseEntity.ok(pathListDTO);
    }

    /**
     * 게시글 작성
     *
     * @param authentication
     * @param postRequestDTO
     * @return
     * @throws IOException-> 나중에 수정하기
     */
    @PostMapping
    public ResponseEntity<Void> createPost(Authentication authentication, PostRequestDTO postRequestDTO) throws IOException {
        CustomUserDetails customMemberDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customMemberDetails.getId();
        boardService.createPost(userId, postRequestDTO);
        return ResponseEntity.ok().build();
    }
     /*
     원래는 201 created 응답이 맞지만 프론트에서 생성된 정보가 필요 없다고 해서
     200으로 성공여부만 전송
      */

    /**
     * 게시글 삭제
     * 게시글 삭제한 상태는 1
     * 게시글 소프트 딜리트
     *
     * @param postId
     * @param authentication
     * @return
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        boardService.deletePost(postId, userId);
        return ResponseEntity.noContent().build(); //204 no content
    }

    /**
     * 좋아요 기능
     *
     * @param postId
     * @param authentication
     * @return
     */
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable Long postId, Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        boardService.toggleLike(postId, userId);// 만약에 문제가 생긴다면 서비스에서 에러가 난다
        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 상세보기
     *
     * @param postId
     * @param authentication
     * @return
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResDTO> postDetails(@PathVariable Long postId,Authentication authentication)
    {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        //PostResDTO postResDTO=boardService.postDetails(postId);
        PostResDTO postResDTO=boardService.postDetails(postId,userId);
        return ResponseEntity.ok(postResDTO);
    }

    /**
     * 게시글 수정하기
     *
     * @param postId
     * @param postPatchRequestDTO
     * @param authentication
     * @return
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId,PostPatchRequestDTO postPatchRequestDTO,Authentication authentication)
    {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        boardService.updatePost(postId,userId,postPatchRequestDTO);
        return ResponseEntity.ok().build();
    }


//    /**
//     * 사진 저장 함수
//     * 사용자가 게시물을 작성하면서 사진을 업로드를 하게 되면 임시 저장소에 사진을 저장한다
//     * 저장한 사진의 주소를 프론트로 반환한다
//     * 여기서 만든 주소를 주면 WebConfig 에서 서버의 실제 파일 경로와 매핑해준다
//     *
//     * @param imageUploadDTO
//     * @param request
//     * @return
//     */
//    @PostMapping("/image")
//    public ResponseEntity<TempImageResponseDTO> saveImage(@RequestBody ImageUploadRequestDTO imageUploadDTO, HttpServletRequest request) {
//        // 클라이언트로부터 받은 Base64 데이터
//        String base64Data = imageUploadDTO.base64Data();
//        String tmpFilePath;
//        String fileName;
//
//        try {
//
//            tmpFilePath = boardService.saveBase64ToTmp(base64Data);
//            fileName = Paths.get(tmpFilePath).getFileName().toString();
//        } catch (IOException e) {
//
//            throw new RuntimeException("Failed to save image", e);
//        }
//
//        // 요청의 기본 URL을 기반으로 파일의 URL을 생성
//        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
//                .replacePath(null)// localhost:8080 뒤에는 제거한다. http://localhost:8080/api/resource → http://localhost:8080
//                .build()
//                .toUriString();
//
//        String tempImageUrl = baseUrl + "/temp-images/" + fileName;
//
//        TempImageResponseDTO response = new TempImageResponseDTO(tmpFilePath, tempImageUrl);
//
//
//        return ResponseEntity.ok(response);
//    }
//
//
//
//    /**
//     * ToDo:마지막에 모든 jpg 삭제하는 기능
//     * @param filePaths
//     * @return
//     */
//    @PostMapping("/image-s3")
//    public ResponseEntity<List<S3ImageResponseDTO>> uploadFromTemp(@RequestBody List<String> filePaths) {
//        List<S3ImageResponseDTO> responseList = filePaths.stream()
//                .map(path -> {
//                    try {
//                        String awsUrl = boardService.uploadFileFromTemp(path);
//                        return new S3ImageResponseDTO(path, awsUrl);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(responseList);
//    }
//
//
//    /**
//     * 선택된 경로,
//     * 제목,본문, 대표사진을 받아와서 설정한다
//     * 프론트가 사용자의 선택을 받고 해당 routeId(pathId) 를 들고 있다가 나중에 json 으로 전송할 때 함께 전송
//     *
//     * @param postRequestDTO
//     * @param authentication
//     * @return
//     */
//    @PostMapping
//    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO postRequestDTO, Authentication authentication) {
//        PostResponseDTO createdPost = boardService.createPost(postRequestDTO, (CustomUserDetails) authentication.getPrincipal().getId());
//        return ResponseEntity.created(createdPost);// 201 created
//    }
//
//
//    /**
//     * 게시물 수정
//     * @param postId
//     * @param postPatchRequestDTO
//     * @param authentication
//     * @return
//     */
//    @PatchMapping("/{postid}")
//    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long postId,@RequestBody PostPatchRequestDTO postPatchRequestDTO, Authentication authentication) {
//        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = customUserDetails.getId();
//        PostResponseDTO updatedPost=boardService.updatePost(postId,postPatchRequestDTO,userId);
//        return ResponseEntity.ok(updatedPost);
//    }


    }
