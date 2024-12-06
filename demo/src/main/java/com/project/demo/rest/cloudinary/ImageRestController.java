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

    @GetMapping("image/{id}")
    public List<Image> getImage(@PathVariable Long id){
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

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public Image addImagen(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("url");
        String imageName = (String) uploadResult.get("public_id");
        Image imagen = new Image();
        imagen.setUrl(imageUrl);
        imagen.setName(imageName);
        imagen.setUser(user);
        imagen.setSaveUrl("ngrok.url/"+"auth/saveImage/"+user);
        return imageRepository.save(imagen);
    }

    @PostMapping("{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<String> likeImage(@PathVariable Long id) {
        String response = imageService.likeImage(id);

        if (response.contains("liked successfully")) {
            return ResponseEntity.ok(response);
        } else if (response.contains("not found")) {
            return ResponseEntity.status(404).body(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }
}
