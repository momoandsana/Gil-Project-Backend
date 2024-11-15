package com.web.gilproject.controller;

import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.BoardDTO.BoardPathDTO;
import com.web.gilproject.dto.BoardDTO.ImageUploadDTO;
import com.web.gilproject.dto.BoardDTO.TempFileUploadResponseDTO;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class BoardController {
//    private final PathService pathService;
    private final BoardService boardService;
    private final AmazonService s3Service;


    /*
    ToDo:나중에 토큰에서 사용자 정보 꺼내서 해당 사용자의 경로 가지고 오기
     임시로 사용자 아이디 받아서 해당 아이디 사용자의 경로들 가지고 오기
     나중에 front 에서 export interface 만들기
     */
    @GetMapping("/{userId}/paths")
    public ResponseEntity<List<BoardPathDTO>> getAllPaths(@PathVariable Long userId)
    {
        List<BoardPathDTO> boardPathListDTO=boardService.getAllPathsById(userId);
//        for (BoardPathDTO boardPathDTO : boardPathListDTO) {
//            System.out.println(boardPathDTO);
//        }
        return ResponseEntity.ok(boardPathListDTO);
    }

    /*
    사진 저장 함수
    사용자가 게시물을 작성하면서 사진을 업로드를 하게 되면 임시 저장소에 사진을 저장한다
    저장한 사진의 주소를 프론트로 반환한다
     */
    @PostMapping("/image")
    public ResponseEntity<Map<String,String>>saveImage(@RequestBody ImageUploadDTO imageUploadDTO)
    {
        String base64Data= imageUploadDTO.base64Data();
        String tmpFilePath= null;
        try {
            tmpFilePath = boardService.saveBase64ToTmp(base64Data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String,String> response=new HashMap<>();
        response.put("filePath",tmpFilePath);
        return ResponseEntity.ok(response);
    }

    /*
    ToDo:마지막에 모든 jpg 삭제하는 기능
     */
    @PostMapping("/image-s3")
    public ResponseEntity<List<TempFileUploadResponseDTO>> uploadFromTemp(@RequestBody List<String> filePaths)
    {
        List<TempFileUploadResponseDTO> responseList=filePaths.stream()
                .map(path->{
                    try{
                        String awsUrl=boardService.uploadFileFromTemp(path);
                        return new TempFileUploadResponseDTO(path,awsUrl);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }


    /*
    선택된 경로,
    제목,본문, 대표사진을 받아와서 설정한다
    프론트가 사용자의 선택을 받고 해당 routeId(pathId) 를 들고 있다가 나중에 json 으로 전송할 때 함께 전송

     */
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        //Post createdPost=boardService.createPost(post);
        //return ResponseEntity.ok(createdPost);
        return null;
    }
//
//    @GetMapping
//    public ResponseEntity<List<Post>> getAllPosts(){
//        List<Post> posts=boardService.getAllPosts();
//        return ResponseEntity.ok(posts);
//    }
//
//    @GetMapping
//    public ResponseEntity<Post> getPostById(@RequestParam Long postId){
//        return
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<Post> updatePost(@PathVariable Long id,@RequestBody Post post){
//
//    }


    // 게시글 삭제

    // 경로별





}
