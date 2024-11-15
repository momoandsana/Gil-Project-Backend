package com.web.gilproject.service;

import com.web.gilproject.domain.Post;

import java.util.List;

public interface GilListService {
    List<Post> findByMyPosition(double nowY, double nowX);
}
