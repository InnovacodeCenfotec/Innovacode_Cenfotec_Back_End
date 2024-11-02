package com.project.demo.rest.api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.project.demo.logic.entity.request.OutfitRequest;
import com.project.demo.logic.entity.request.BackgroundRequest;
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
    String apiKey = "tu_clave_api_de_lightx";

    @PostMapping("/generate-outfit")
    public String generateOutfit(@RequestBody OutfitRequest outfitRequest) {
        RestTemplate restTemplate = new RestTemplate();

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




