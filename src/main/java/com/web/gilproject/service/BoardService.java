package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.dto.BoardDTO.BoardPathDTO;
import com.web.gilproject.repository.BoardRepository;
import com.web.gilproject.repository.PathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final PathRepository pathRepository;

    public List<BoardPathDTO> getAllPathsById(Long userId) {
        List<Path> paths = pathRepository.findByUserId(userId);

        return paths.stream()
                .map(BoardPathDTO::from)
                .collect(Collectors.toList());
        //return paths;
    }
}
