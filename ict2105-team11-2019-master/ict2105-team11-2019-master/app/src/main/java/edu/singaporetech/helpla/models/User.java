package edu.singaporetech.helpla.models;

import java.util.ArrayList;
import java.util.Map;

public class User {
    private String email;
    private String name;
    private String image_url;
    private int points;
    private String uid;
    private Map<String,chat> chat;

    public Map<String,chat> getChat() {
        return chat;
    }


    public void setChat(Map<String,chat> chat) {
        this.chat = chat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static class chat{
        String chat_name;
        String status;
        String key;
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getChat_name() {
            return chat_name;
        }

        public void setChat_name(String chat_name) {
            this.chat_name = chat_name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}