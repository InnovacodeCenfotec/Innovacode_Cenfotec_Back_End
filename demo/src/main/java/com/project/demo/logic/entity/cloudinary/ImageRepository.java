package com.project.demo.logic.entity.cloudinary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.user.id = ?1")
    List<Image> findByUser(Long userId);
}
//