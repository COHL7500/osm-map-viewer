package bfst22.vector.TernarySearchTree;

public class SearchNode {
    public char character;
    public boolean isEndOString;
    public SearchNode left;
    public SearchNode equal;
    public SearchNode right;
    int index;
    public SearchNode(char _character, int _index) {
        this.character = _character;
        this.index = _index;
        this.isEndOString = false;
        this.left = null;
        this.equal = null;
        this.right = null;
    }
}