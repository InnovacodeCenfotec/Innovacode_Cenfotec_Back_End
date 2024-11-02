package com.project.demo.rest.api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
class MyController {

    @PostMapping("/generate-outfit")
    public String generateOutfit(@RequestBody OutfitRequest outfitRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = "tu_clave_api_de_lightx";
        String url = "https://api.lightxeditor.com/external/api/v1/outfit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        String requestJson = String.format("{\"imageUrl\": \"%s\", \"textPrompt\": \"%s\"}",
                outfitRequest.getImageUrl(), outfitRequest.getTextPrompt());

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    @PostMapping("/generate-background")
    public String generateBackground(@RequestBody BackgroundRequest backgroundRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = "tu_clave_api_de_lightx";
        String url = "https://api.lightxeditor.com/external/api/v1/background-generator";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        String requestJson = String.format("{\"imageUrl\": \"%s\", \"styleImageUrl\": \"%s\", \"textPrompt\": \"%s\"}",
                backgroundRequest.getImageUrl(), backgroundRequest.getStyleImageUrl(), backgroundRequest.getTextPrompt());

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}

class OutfitRequest {
    private String imageUrl;
    private String textPrompt;

    // Getters and setters

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTextPrompt() {
        return textPrompt;
    }

    public void setTextPrompt(String textPrompt) {
        this.textPrompt = textPrompt;

    }
}

class BackgroundRequest {
    private String imageUrl;
    private String styleImageUrl;
    private String textPrompt;

    // Getters y setters

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStyleImageUrl() {
        return styleImageUrl;
    }

    public void setStyleImageUrl(String styleImageUrl) {
        this.styleImageUrl = styleImageUrl;
    }

    public String getTextPrompt() {
        return textPrompt;
    }

    public void setTextPrompt(String textPrompt) {
        this.textPrompt = textPrompt;
    }
}
