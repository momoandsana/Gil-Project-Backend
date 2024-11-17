package com.web.gilproject.controller;

import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.BoardDTO.*;
import com.web.gilproject.dto.CustomUserDetails;
import com.web.gilproject.service.AmazonService;
import com.web.gilproject.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    //    private final PathService pathService;
    private final BoardService boardService;
    private final AmazonService s3Service;


    /**
     * ToDo:나중에 front 에서 export interface 만들기
     *
     *
     *
     * @param authentication
     * @return
     */
    //@GetMapping("/{userId}/paths")
    @GetMapping("/paths")
    public ResponseEntity<List<BoardPathResponseDTO>> getAllPaths(Authentication authentication) {

        CustomUserDetails customMemberDetails=(CustomUserDetails)authentication.getPrincipal();
        Long userId=customMemberDetails.getId();
        List<BoardPathResponseDTO> boardPathListDTO = boardService.getAllPathsById(userId);
//        for (BoardPathDTO boardPathDTO : boardPathListDTO) {
//            System.out.println(boardPathDTO);
//        }
        return ResponseEntity.ok(boardPathListDTO);
    }


    /**
     * 사진 저장 함수
     * 사용자가 게시물을 작성하면서 사진을 업로드를 하게 되면 임시 저장소에 사진을 저장한다
     * 저장한 사진의 주소를 프론트로 반환한다
     * 여기서 만든 주소를 주면 WebConfig 에서 서버의 실제 파일 경로와 매핑해준다
     *
     * @param imageUploadDTO
     * @param request
     * @return
     */
    @PostMapping("/image")
    public ResponseEntity<TempImageResponseDTO> saveImage(@RequestBody ImageUploadRequestDTO imageUploadDTO, HttpServletRequest request) {
        // 클라이언트로부터 받은 Base64 데이터
        String base64Data = imageUploadDTO.base64Data();
        String tmpFilePath;
        String fileName;

        try {

            tmpFilePath = boardService.saveBase64ToTmp(base64Data);
            fileName = Paths.get(tmpFilePath).getFileName().toString();
        } catch (IOException e) {

            throw new RuntimeException("Failed to save image", e);
        }

        // 요청의 기본 URL을 기반으로 파일의 URL을 생성
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)// localhost:8080 뒤에는 제거한다. http://localhost:8080/api/resource → http://localhost:8080
                .build()
                .toUriString();

        String tempImageUrl = baseUrl + "/temp-images/" + fileName;

        TempImageResponseDTO response = new TempImageResponseDTO(tmpFilePath, tempImageUrl);


        return ResponseEntity.ok(response);
    }

    /*
    ToDo:마지막에 모든 jpg 삭제하는 기능
     */
    @PostMapping("/image-s3")
    public ResponseEntity<List<S3ImageResponseDTO>> uploadFromTemp(@RequestBody List<String> filePaths) {
        List<S3ImageResponseDTO> responseList = filePaths.stream()
                .map(path -> {
                    try {
                        String awsUrl = boardService.uploadFileFromTemp(path);
                        return new S3ImageResponseDTO(path, awsUrl);
                    } catch (IOException e) {
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
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO postRequestDTO,Authentication authentication) {
        PostResponseDTO createdPost=boardService.createPost(postRequestDTO,(CustomUserDetails)authentication.getPrincipal().getId());
        return ResponseEntity.created(createdPost);// 201 created
    }


//
//    @PatchMapping("/{id}")
//    public ResponseEntity<Post> updatePost(@PathVariable Long id,@RequestBody Post post){
//
//    }


    // 게시글 삭제

    // 경로별


}
