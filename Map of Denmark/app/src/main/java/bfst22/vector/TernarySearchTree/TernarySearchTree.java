package bfst22.vector.TernarySearchTree;

import java.util.ArrayList;

public class TernarySearchTree {

    private SearchNode root;
    private ArrayList al;

    public TernarySearchTree() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public SearchNode getRoot() {
        return root;
    }

    public void clearTree() {
        root = null;
    }

    public void insertSearchString(String word) {
        root = insert(root, word.toLowerCase().toCharArray(), 0, 0);
    }

    public void insertAddress(String address, int id) {
        root = insert(root, address.toLowerCase().toCharArray(), 0, id);
    }

    public SearchNode insert(SearchNode node, char[] word, int index, int id) {
        if (node == null) {
            node = new SearchNode(word[index], id);
        }
        if (word[index] < node.character) {
            node.left = insert(node.left, word, index, id);
        }
        else if (word[index] > node.character) {
            node.right = insert(node.right, word, index, id);
        }
        else {
            if (index + 1 < word.length) {
                node.equal = insert(node.equal, word, index + 1, id);
            }
            else node.isEndOString = true;
        }
        return node;
    }

    public void delete(String word) {
        delete(root, word.toCharArray(), 0);
    }

    private void delete(SearchNode node, char[] word, int index) {
        if (node == null)
            return;
        if (word[index] < node.character)
            delete(node.left, word, index);
        else if (word[index] > node.character)
            delete(node.right, word, index);
        else {
            if (node.isEndOString && index == word.length - 1) {
                node.isEndOString = false;
            }
            else if (index + 1 < word.length) {
                delete(node.equal, word, index + 1);
            }
        }
    }

    public SearchNode search(String word) {
        return search(root, word.toCharArray(), 0);
    }

    private SearchNode search(SearchNode node, char[] word, int index) {
        if (node == null || word.length == 0)
            return null;
        if (word[index] < node.character)
            return search(node.left, word, index);
        else if (word[index] > node.character)
            return search(node.right, word, index);
        else {
            if (node.isEndOString && index == word.length - 1)
                return node;
            else if (index == word.length - 1)
                return null;
            else
                return search(node.equal, word, index + 1);
        }
    }
    
    public String toString() {
        al = new ArrayList<String>();
        traverse(root, "");
        return "\nSearch tree: "+ al;
    }

    private void traverse(SearchNode node, String string) {
        if (node != null) {
            traverse(node.left, string);

            string = string + node.character;
            if (node.isEndOString)
                al.add(string);

            traverse(node.equal, string);
            string = string.substring(0, string.length() - 1);

            traverse(node.right, string);
        }
    }

    public SearchNode autoComplete(String word) {
        return autoComplete(root, word.toCharArray(),0);
    }

    private SearchNode autoComplete(SearchNode node, char[] word, int index) {
        SearchNode result = null;
        if (node == null || word.length <= index) return null;
        if (word[index] == node.character) {
            if (word[index] == word.length - 1) return node;
            else {
                result = (node.equal == null ? null : autoComplete(node.equal, word, index + 1));
                if (result == null) result = node;
            }
        }
            if (word[index] > node.character) {
                result = (node.right == null ? null : autoComplete(node.right, word, index + 1));
            }
            else {
                result = (node.left == null ? null : autoComplete(node.left, word, index + 1));
            }
        return result;
    }
}