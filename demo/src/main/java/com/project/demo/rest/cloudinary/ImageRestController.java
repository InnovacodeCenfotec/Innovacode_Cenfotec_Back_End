package com.project.demo.rest.cloudinary;


import com.project.demo.logic.entity.cloudinary.Image;
import com.project.demo.logic.entity.cloudinary.ImageRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.entity.cloudinary.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
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
        imagen.setSaveUrl("https://520a-2800-860-7193-2e2-ad48-a1fa-d7a-a142.ngrok-free.app/"+"auth/saveImage/"+userId);
        return imageRepository.save(imagen);
    }

    @PostMapping("{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<String> likeImage(@PathVariable Long id) {
        // Llamamos al método del servicio que maneja el "like"
        String response = imageService.likeImage(id);

        // Determinamos la respuesta según el mensaje que se obtiene del servicio
        if ("Image liked successfully".equals(response)) {
            return ResponseEntity.ok(response);  // Retorna OK si se pudo dar like
        } else if ("Image not found".equals(response)) {
            return ResponseEntity.status(404).body(response);  // Imagen no encontrada
        } else if ("You have already liked this image".equals(response)) {
            return ResponseEntity.status(400).body(response);  // Ya se ha dado like
        } else if ("Like removed successfully".equals(response)) {
            return ResponseEntity.ok(response);  // Retorna OK si se quitó el like
        } else {
            // Si se da algún otro error no esperado, se retorna un error general 500
            return ResponseEntity.status(500).body("An unexpected error occurred: " + response);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<String> getLikesById(@PathVariable Long id) {
        try {
            int likes = imageService.getLikesById(id);
            return ResponseEntity.ok("Likes count: " + likes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Image not found.");
        }
    }
}
