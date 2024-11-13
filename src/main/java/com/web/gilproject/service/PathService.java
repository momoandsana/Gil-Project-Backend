package com.web.gilproject.service;

import com.web.gilproject.domain.Path;
import com.web.gilproject.repository.PathRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.mvc.entity.Path;
import web.mvc.repository.PathRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PathService {

    private final PathRepository pathRepository;

    public Path insert(Path path) {
        Path savedPath= pathRepository.save(path);
        return savedPath;
    }

    public List<String> getAllRoutesAsWKT() {
        return pathRepository.findAllRoutesAsWKT();
    }
}
