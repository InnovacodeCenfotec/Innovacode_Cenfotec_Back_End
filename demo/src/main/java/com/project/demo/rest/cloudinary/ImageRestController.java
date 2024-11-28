package com.project.demo.rest.cloudinary;


import com.project.demo.logic.entity.cloudinary.Image;
import com.project.demo.logic.entity.cloudinary.ImageRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.cloudinary.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/cloudinary")
public class ImageRestController {

    private final Cloudinary cloudinary;
    @Autowired
    public ImageRestController(Cloudinary cloudinary) { this.cloudinary = cloudinary; }

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @GetMapping("user/{userId}")
    public List<Image> getUserImages(@PathVariable Long userId) {
        return imageRepository.findByUser(userId);
    }

    @GetMapping
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        String result = imageService.deleteImage(id);

        if ("Image deleted successfully.".equals(result)) {
            return ResponseEntity.ok(result);
        } else if ("Image not found.".equals(result)) {
            return ResponseEntity.status(404).body(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }




}
