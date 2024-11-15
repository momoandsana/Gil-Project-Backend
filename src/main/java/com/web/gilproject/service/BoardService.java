package com.web.gilproject.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.web.gilproject.domain.Path;
import com.web.gilproject.dto.BoardDTO.BoardPathDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.PathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final PathRepository pathRepository;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private static final String TMP_DIR=System.getProperty("java.io.tmpdir");
    private final AmazonS3 amazonS3;

    public List<BoardPathDTO> getAllPathsById(Long userId) {
        List<Path> paths = pathRepository.findByUserId(userId);

        return paths.stream()
                .map(BoardPathDTO::from)
                .collect(Collectors.toList());
        //return paths;
    }

    public String saveBase64ToTmp(String base64Data) throws IOException {
        String fileName="tmp_"+System.currentTimeMillis()+".jpg";
        String filePath=TMP_DIR+ File.separator+fileName;

        byte[] decodedBytes= Base64.getDecoder().decode(base64Data);
        // 표준 Base64 방식으로 디코딩

        /*
        Base64.getUrlDecoder()
        Base64.getMimeDecoder()
        로 변경 가능
         */

        try(FileOutputStream fos=new FileOutputStream(filePath))
        {
            fos.write(decodedBytes);
        }

        return filePath;
    }

    public String uploadFileFromTemp(String  filePath) throws IOException {
        File file=new File(filePath);

        String fileName=file.getName();
        String key="upload_images/"+fileName;

        ObjectMetadata metadata=new ObjectMetadata();
        metadata.setContentLength(file.length());
        metadata.setContentType(Files.probeContentType(file.toPath()));

        try(FileInputStream inputStream=new FileInputStream(filePath))
        {
            amazonS3.putObject(bucketName,key,inputStream,metadata);
        }

        String awsUrl=amazonS3.getUrl(bucketName,fileName).toString();

        if(!file.delete())// 파일 업로드하고 나서 temp 폴더에서 해당 파일 삭제
        {
            throw new IOException("Failed to delete file");
        }
        return awsUrl;

    }
}













