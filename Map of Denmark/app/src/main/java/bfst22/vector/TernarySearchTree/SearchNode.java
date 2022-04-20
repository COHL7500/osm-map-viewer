package bfst22.vector.TernarySearchTree;

public class SearchNode {
    public char character;
    public boolean isEndOString;
    public SearchNode left;
    public SearchNode equal;
    public SearchNode right;
    int id;

    public SearchNode(char _character, int _id) {
        this.character = _character;
        this.id = _id;
        this.isEndOString = false;
        this.left = null;
        this.equal = null;
        this.right = null;
    }

    public int getId() {
        return id;
    }
}