package com.web.gilproject.repository;

import com.web.gilproject.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    Optional<PostImage> findByImageUrl(String imageUrl);

}
