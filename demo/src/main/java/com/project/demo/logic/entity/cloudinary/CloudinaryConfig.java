package com.project.demo.logic.entity.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dt593yy9z",
                "api_key", "119499567173755",
                "api_secret", "VgMWlyoyQbLL8Y5pxBeQdBm8CgA"
        ));
    }
}
//