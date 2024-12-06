package com.project.demo.logic.entity.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    public String deleteImage(Long id) {
        Optional<Image> imageOpt = imageRepository.findById(id);

        if (imageOpt.isPresent()) {
            Image image = imageOpt.get();
            String publicId = extractPublicId(image.getUrl());

            try {
                // Eliminar de Cloudinary
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

                // Eliminar de la base de datos
                imageRepository.deleteById(id);

                return "Image deleted successfully.";
            } catch (Exception e) {
                return "Error deleting image: " + e.getMessage();
            }
        } else {
            return "Image not found.";
        }
    }

    private String extractPublicId(String url) {
        // Encontrar la parte después de '/upload/' y eliminar la versión
        String[] parts = url.split("/upload/");
        if (parts.length > 1) {
            String[] pathParts = parts[1].split("/");  // Separar por '/'
            String fileNameWithVersion = pathParts[pathParts.length - 1]; // Tomar la última parte con la versión
            String fileName = fileNameWithVersion.split("\\.")[0]; // Eliminar la extensión
            return fileName; // Devolver el publicId sin extensión
        }
        throw new IllegalArgumentException("Invalid Cloudinary URL format");
    }

    @Transactional  // Asegura que las operaciones se realicen dentro de una transacción
    public String likeImage(Long id) {
        Optional<Image> imageOpt = imageRepository.findById(id);

        if (imageOpt.isPresent()) {
            Image image = imageOpt.get();

            // Obtener el usuario logueado
            Long userId = getLoggedInUserId();
            Optional<User> userOpt = userRepository.findById(userId);

            if (!userOpt.isPresent()) {
                return "User not found.";
            }

            User user = userOpt.get();

            // Verificar si el usuario ya le dio like a la imagen
            if (likeRepository.existsByUserIdAndImageId(userId, id)) {
                // Si ya le dio like, eliminar el like
                likeRepository.deleteByUserIdAndImageId(userId, id);

                // Reducir los likes de la imagen
                image.decrementLikes();
                imageRepository.save(image);

                return "Like removed successfully. Total likes: " + image.getLikesCount();
            }

            // Si no ha dado like, dar like a la imagen
            image.incrementLikes();
            imageRepository.save(image);

            // Registrar el like en el repositorio
            Likes like = new Likes(user, image);
            likeRepository.save(like);

            return "Image liked successfully. Total likes: " + image.getLikesCount();
        } else {
            return "Image not found.";
        }
    }

    // Método para obtener el ID del usuario logueado
    private Long getLoggedInUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();  // El username es el correo electrónico

            // Buscar el usuario en la base de datos usando el correo electrónico
            Optional<User> userOpt = userRepository.findByEmail(username);

            if (userOpt.isPresent()) {
                return userOpt.get().getId();  // Devuelve el ID del usuario
            }
        }
        return null;  // En caso de que no se encuentre al usuario logueado
    }

    public int getLikesById(Long id) {
        Optional<Image> imageOpt = imageRepository.findById(id);

        if (imageOpt.isPresent()) {
            Image image = imageOpt.get();
            return image.getLikesCount();
        } else {
            throw new RuntimeException("Image not found");
        }
    }
}
