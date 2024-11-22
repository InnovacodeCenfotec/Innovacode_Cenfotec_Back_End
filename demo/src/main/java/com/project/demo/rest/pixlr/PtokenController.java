package com.project.demo.rest.pixlr;
import com.project.demo.logic.entity.auth.PtokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PtokenController {
    private  PtokenService pixlrTokenService;

    @Value("${pixlr.open-url}")
    private String openUrl;

    @Value("${pixlr.save-url}")
    private String saveUrl;

    public PtokenController(PtokenService pixlrTokenService) {
        this.pixlrTokenService = pixlrTokenService;
    }

    @GetMapping("/pixlr-token")
    public ResponseEntity<String> getPixlrToken() {
        String token = pixlrTokenService.generatePixlrToken(openUrl, saveUrl);
        return ResponseEntity.ok(token);
    }

}
