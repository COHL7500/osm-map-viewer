package bfst22.vector;

import javafx.geometry.Point2D;

public class Coordinates {
    private float minX, minY, maxX, maxY;

    public Coordinates(float minX, float minY, float maxX, float maxY){
        //Makes sure that min values are actually min and vice versa for max.
        if(minX < maxY){
            this.minX = minX;
            this.maxX = maxX;
            }
        else {
            this.minX = maxX;
            this.maxX = minX;
        }
        if(minY < maxY){
            this.minY = minY;
            this.maxY = maxY;
        }
        else {
            this.minY = maxY;
            this.maxY = minY;
        }
    }

    public double getMinX(){
        return this.minX;
    }

    public double getMinY(){
        return this.minY;
    }

    public double getMaxX(){
        return this.maxX;
    }

    public double getMaxY(){
        return this.maxY;
    }

    public Point2D getMinXY(){
        return new Point2D(minX,minY);
    }

    public Point2D getMaxXY(){
        return new Point2D(maxX,maxY);
    }

    public Point2D getCenterPoint(){
        return new Point2D((this.minX+this.maxX)/2, (this.minY+this.maxY)/2);
    }
}