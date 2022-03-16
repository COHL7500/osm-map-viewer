package bfst22.vector;

import bfst22.vector.OSMNode;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class KDTree <Key extends Comparable<Key>, Value> {

    private class KDNode {
        private Key key;
        private Value val;
        private KDNode left, right;

        public KDNode(Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }
    private KDNode root;
    private Point2D point;

    private Key lo;
    private Key hi;
    private int size;

    //Initialize empty symbol table (Tree)
    public KDTree(){
        root = null;
    }

    //Initialize a non-empy KD-Tree
    public KDTree(List<? extends MapElement> elements){
        //Not yet implemented
    }

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
