package com.example.MyBlogApp;

import com.google.firebase.database.ServerValue;

public class Blog {
    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    private String postKey;
    private  String entry;
    private String header;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String userid;
    private String image;
    public Blog(String userid, String entry, String header, String image, String circle){
        this.userid = userid;
        this.entry = entry;
        this.header = header;
        this.image = image;
        this.circle = circle;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    private  Object timestamp;



    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    private String circle;
    public Blog(){

    }

    public Blog(String entry, String header, String image) {
        this.entry = entry;
        this.header = header;
        this.image = image;

    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
