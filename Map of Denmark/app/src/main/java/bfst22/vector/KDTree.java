package bfst22.vector;

import bfst22.vector.OSMNode;
import javafx.geometry.BoundingBox;

import java.awt.geom.Point2D;
import java.util.List;

public class KDTree <Key extends Comparable<Key>, Value> implements SerialVersionIdentifiable{

    //Fields to be used in the future
    private KDNode root;
    //BoundingBox boundingBox = new BoundingBox();

    //For search
    private Key lo;
    private Key hi;
    private int depth;
    private Point2D point;

    //internal  KD-Tree Node class
    private class KDNode {
        private Key key;
        private Value val;
        private KDNode left, right;
        private int size;

        //Constructor
        public KDNode(Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }

    //Initialize empty symbol table (Tree)
    public KDTree(){
        root = null;
    }

    //Initialize a non-empty KD-Tree
    public KDTree(List<? > elements){

    }


    //getValue methods

    public Value get(Key key){
        return get(root, key);
    }

    public Value get(KDNode node, Key key){
        if(key == null) throw new IllegalArgumentException("You may not call get() with a key with value of null");
        if(node == null) return null;
        int compareKey = key.compareTo(node.key);

        if(compareKey < 0) return get(node.left, key);
        if(compareKey > 0) return get(node.right, key);
        else {
            return node.val;
        }
    }

    public void insert(Key key, Value val){
        if(key == null){
            throw new IllegalArgumentException("You can't call this method using a null key");
        } if(val == null){
            //Ikke sikker på hvad man skal gøre her
            throw new IllegalArgumentException("null value");
        }
        root = insert(root, key, val);
    }

    public KDNode insert(KDNode node, Key key, Value value){
        if (node == null){
            //bruh
        }
        int compareKey = key.compareTo(node.key);
        if (compareKey < 0){
            node.left = insert(node.left, key, value);
        } else if(compareKey > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.val = value;
        }
        return node;
    }


    public boolean contains(Key key){
        if (key == null){
            return false;
        }
        return get(key) != null;
    }


    public boolean add(Point2D point){
        return false;
    }

    //Can be used to see if we need to draw a vertical or horizontal line
    public boolean isEven(){
        return this.depth % 2 == 0;
    }

}
