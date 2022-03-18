package bfst22.vector;

import bfst22.vector.OSMNode;
import javafx.geometry.BoundingBox;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class KDTree <Key extends Comparable<Key>, Value> {

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
    //Fields to be used in the future
    private KDNode root;
    private KDNode best;
    private double shortestDistance;
    private int visited;

    //For search
    private Key lo;
    private Key hi;

    private Point2D point;

    //BoundingBox boundingBox = new BoundingBox();

    //Initialize empty symbol table (Tree)
    public KDTree(){
        root = null;
    }

    //Initialize a non-empy KD-Tree
    public KDTree(List<? extends MapElement> elements){
        //Not yet implemented
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

    public Value keyRange(){
        return null;
    }

    public boolean add(Point2D point){
        return false;
    }

}
