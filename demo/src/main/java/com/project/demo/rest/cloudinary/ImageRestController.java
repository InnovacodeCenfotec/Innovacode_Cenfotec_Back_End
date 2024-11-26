package com.project.demo.rest.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.cloudinary.Image;
import com.project.demo.logic.entity.cloudinary.ImageRepository;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/images")
public class ImageRestController {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("user/{userId}")
    public List<Image> getUserImages(@PathVariable Long userId) {
        return imageRepository.findByUser(userId);
    }

    @GetMapping
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }


}
