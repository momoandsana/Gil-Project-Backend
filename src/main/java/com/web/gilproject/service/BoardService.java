package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.dto.BoardDTO.BoardPathDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.PathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final PathRepository pathRepository;

    private static final String TMP_DIR=System.getProperty("java.io.tmpdir");

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
}
