package com.project.demo.rest.cloudinary;

import com.project.demo.logic.entity.cloudinary.Image;
import com.project.demo.logic.entity.cloudinary.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageRestController {

    @Autowired
    ImageRepository imageRepository;

    @GetMapping("user/{userId}")
    public List<Image> getUserImages(@PathVariable Long userId) {
        return imageRepository.findByUser(userId);
    }

    @GetMapping
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }


}
