package com.project.demo.logic.entity.request;



public  class BackgroundRequest {
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




