package com.project.demo.rest.pixlr;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@RestController
@RequestMapping("/api")
public class PixlrController {

    @PostMapping("/saveImage")
    public ResponseEntity<String> saveImage(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file locally or to a database
            String filePath = "/save" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            return ResponseEntity.ok("Imágen guardada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imágen");
        }
    }
}
