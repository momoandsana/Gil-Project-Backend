package com.web.gilproject.dto.BoardDTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record PostPatchRequestDTO(String title, String content, String tag, List<String> deleteUrls, List<MultipartFile> newImages) {
}
