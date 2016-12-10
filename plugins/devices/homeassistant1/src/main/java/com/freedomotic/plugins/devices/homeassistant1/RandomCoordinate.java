/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins.devices.homeassistant1;

import java.util.Random;

/**
 *
 * @author ricca
 */
public class RandomCoordinate {
    
    private int x;
    private int y;
    private int startX = 1;
    private int endX = 1048;
    private int startY = 281;
    private int endY = 848;
    Random rand = new Random();
    
    RandomCoordinate(){
        x = 0;
        y = 0;
    }
    
    public int getX() {
        x = rand.nextInt((endX - startX) + 1) + startX;
        return x;
    }
    public int getY() {
        y = rand.nextInt((endY - startY) + 1) + startY;
        return y;
    }
    
    
    
}
