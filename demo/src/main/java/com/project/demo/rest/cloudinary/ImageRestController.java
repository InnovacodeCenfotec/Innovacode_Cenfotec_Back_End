package com.project.demo.rest.cloudinary;


import com.project.demo.logic.entity.auth.JwtService;
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
import com.project.demo.logic.entity.auth.JwtService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cloudinary")
public class ImageRestController {

    private final Cloudinary cloudinary;
    @Autowired
    public ImageRestController(JwtService jwtService, Cloudinary cloudinary) {

        this.cloudinary = cloudinary; }

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
    public Image addImagen(@RequestParam("file") MultipartFile file,
                           @RequestParam("userId") Long userId,
                           @RequestParam("imageName") String imageName) throws IOException {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("url");
        Image imagen = new Image();
        imagen.setUrl(imageUrl);
        imagen.setName(imageName);
        imagen.setUser(user);
        imagen.setSaveUrl("https://520a-2800-860-7193-2e2-ad48-a1fa-d7a-a142.ngrok-free.app/"+"auth/saveImage/"+userId);
        return imageRepository.save(imagen);
    }



}
