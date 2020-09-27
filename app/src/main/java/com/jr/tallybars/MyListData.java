package com.jr.tallybars;

import android.graphics.Color;

public class MyListData {

    private String description;
    private int colour;
    private int background_colour;

    private int x;
    private int y;
    private int rad;

    public MyListData(String description, int colour) {
        this.description = description;
        this.colour = colour;
        this.x = 60;
        this.y = 120;
        this.rad = 50;
        this.background_colour = Color.WHITE;
    }

    public MyListData(String description, int colour, int background_color, int x, int y, int rad){
        this.description = description;
        this.colour = colour;
        this.x = x;
        this.y = y;
        this.rad = rad;
        this.background_colour = background_color;
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

    public int getBackgroundColour() { return this.background_colour; }


    public int getX(){ return this.x; }
    public int getY(){ return this.y; }
    public int getRad(){ return this.rad; }

}
