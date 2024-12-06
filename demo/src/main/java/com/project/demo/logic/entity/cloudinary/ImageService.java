package com.project.demo.logic.entity.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageRepository imageRepository;

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

}
