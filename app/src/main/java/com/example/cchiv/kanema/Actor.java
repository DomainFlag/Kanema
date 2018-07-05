package com.example.cchiv.kanema;

public class Actor {

    private int ID;
    private String character;
    private String name;
    private String profilePath;
    private int gender;

    public Actor(int ID, String character, int gender, String name, String profilePath) {
        this.ID = ID;
        this.character = character;
        this.gender = gender;
        this.name = name;
        this.profilePath = profilePath;
    }

    public Actor(int ID, String character, String name, String profilePath) {
        this(ID, character, 0, name, profilePath);
    }

    public int getID() {
        return ID;
    }

    public int getGender() {
        return gender;
    }

    public String getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}
