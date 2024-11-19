package com.web.gilproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.web.gilproject.domain.Path;
import com.web.gilproject.domain.Post;
import com.web.gilproject.dto.BoardDTO.BoardPathResponseDTO;
import com.web.gilproject.dto.BoardDTO.PostRequestDTO;
import com.web.gilproject.dto.BoardDTO.PostResponseDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.PathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final PathRepository pathRepository;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private static final String TMP_DIR=System.getProperty("java.io.tmpdir");


    public List<BoardPathResponseDTO> getAllPathsById(Long userId) {
        List<Path> paths = pathRepository.findByUserId(userId);

        return paths.stream()
                .map(BoardPathResponseDTO::from)
                .collect(Collectors.toList());
        //return paths;
    }


    public PostResponseDTO createPost(Long userId, PostRequestDTO postRequestDTO) {
        //Path path=pathRepository.findById(postRequestDTO.routeId());

    }



//    public String saveBase64ToTmp(String base64Data) throws IOException {
//        String fileName="tmp_"+System.currentTimeMillis()+".jpg";
//        String filePath=TMP_DIR+ File.separator+fileName;
//
//        byte[] decodedBytes= Base64.getDecoder().decode(base64Data);
//        // 표준 Base64 방식으로 디코딩
//
//        /*
//        Base64.getUrlDecoder()
//        Base64.getMimeDecoder()
//        로 변경 가능
//         */
//
//        try(FileOutputStream fos=new FileOutputStream(filePath))
//        {
//            fos.write(decodedBytes);
//        }
//
//        return filePath;
//    }
//
//    public String uploadFileFromTemp(String filePath) throws IOException {
//        File file = new File(filePath);
//
//        String fileName = file.getName();
//        String key = "upload_images/" + fileName;
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.length());
//        metadata.setContentType(Files.probeContentType(file.toPath()));
//
//
//        try (FileInputStream inputStream = new FileInputStream(filePath)) {
//            amazonS3.putObject(bucketName, key, inputStream, metadata);
//        }
//
//
//        String awsUrl = amazonS3.getUrl(bucketName, key).toString();
//
//
//        // 업로드하고 로컬에서는 해당 파일 삭제
//        if (!file.delete()) {
//            throw new IOException("사진 파일 삭제 실패");
//        }
//
//        return awsUrl;
//    }
//
//    @Transactional
//    public PostResponseDTO createPost(PostRequestDTO postRequestDTO,Long id) {
//        Post postEntity=boardRepository.save(postRequestDTO.of(postRequestDTO,id));
//        // 사용자 검증하기 나중에 추가 exception
//        return PostResponseDTO.from(postEntity);
//    }
//
//    @Transactional
//    public PostResponseDTO updatePost(Long postId,PostPatchRequestDTO postPatchRequestDTO,Long userId) {
//        Post postEntity=boardRepository
//                .findById(postId)
//                .orElseThrow(
//                        ()->new RuntimeException("post id not found")
//                );
//
//        if(postPatchRequestDTO.content()!=null)
//        {
//            postEntity.setContent(postPatchRequestDTO.content());
//        }
//        if(postPatchRequestDTO.tag()!=null)
//        {
//            postEntity.setTag(postPatchRequestDTO.tag());
//        }
//
//        if(!postEntity.getUser().getId().equals(userId))
//        {
//            throw new RuntimeException("no user found");// 임시
//        }
//
//        Post updatedPost=boardRepository.save(postEntity);
//        return PostResponseDTO.from(updatedPost);
//    }

    @Transactional
    public void deletePost(Long postId,Long userId) {
        Post postEntity=boardRepository
                .findById(postId)
                .orElseThrow(()->new RuntimeException("post not found"));// 임시 exception

        if(!postEntity.getUser().getId().equals(userId))
        {
            throw new RuntimeException("no user found");
        }

        postEntity.setState(1);// 삭제한 상태
        boardRepository.save(postEntity);
    }
}













