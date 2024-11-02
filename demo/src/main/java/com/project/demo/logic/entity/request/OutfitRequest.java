package com.project.demo.logic.entity.request;

public  class OutfitRequest {
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