package com.example.cchiv.kanema.objects;

public class Video {

    private String key;
    private String name;
    private String source;
    private String type;

    public Video(String key, String name, String source, String type) {
        this.key = key;
        this.name = name;
        this.source = source;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
