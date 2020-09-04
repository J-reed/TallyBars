package com.jr.tallybars;

public class MyListData {

    private String description;
    private int colour;

    public MyListData(String description, int colour) {
        this.description = description;
        this.colour = colour;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getColour() {
        return colour;
    }
    public void setColour(int colour) {
        this.colour = colour;
    }
}
