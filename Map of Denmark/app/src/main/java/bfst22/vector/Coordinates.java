package bfst22.vector;

import javafx.geometry.Point2D;

public class Coordinates {

    public Coordinates(float minX, float minY, float maxX, float maxY){
        //Makes sure that min values are actually min and vice versa for max.
        float minX1;
        float maxX1;
        if(minX < maxY){
            minX1 = minX;
            maxX1 = maxX;
            }
        else {
            minX1 = maxX;
            maxX1 = minX;
        }
        float minY1;
        float maxY1;
        if(minY < maxY){
            minY1 = minY;
            maxY1 = maxY;
        }
        else {
            minY1 = maxY;
            maxY1 = minY;
        }
    }
}