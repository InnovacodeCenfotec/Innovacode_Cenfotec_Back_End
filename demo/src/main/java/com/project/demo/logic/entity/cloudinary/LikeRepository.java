package com.project.demo.logic.entity.cloudinary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    boolean existsByUserIdAndImageId(Long userId, Long imageId);

    void deleteByUserIdAndImageId(Long userId, Long imageId);
}
