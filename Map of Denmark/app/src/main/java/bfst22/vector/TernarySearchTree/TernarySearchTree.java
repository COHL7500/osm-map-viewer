package bfst22.vector.TernarySearchTree;

import bfst22.vector.TernarySearchTree.SearchNode;

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
    private void delete(SearchNode node, char[] word, int pointer) {
        if (node == null)
            return;
        if (word[pointer] < node.character)
            delete(node.left, word, pointer);
        else if (word[pointer] > node.character)
            delete(node.right, word, pointer);
        else {
            if (node.isEndOString && pointer == word.length - 1) {
                node.isEndOString = false;
            }
            else if (pointer + 1 < word.length) {
                delete(node.equal, word, pointer + 1);
            }
        }
    }

    public boolean search(String word) {
        return search(root, word.toCharArray(), 0);
    }

    private boolean search(SearchNode node, char[] word, int pointer) {
        if (node == null)
            return false;

        if (word[pointer] < node.character)
            return search(node.left, word, pointer);
        else if (word[pointer] > node.character)
            return search(node.right, word, pointer);
        else {
            if (node.isEndOString && pointer == word.length - 1)
                return true;
            else if (pointer == word.length - 1)
                return false;
            else
                return search(node.equal, word, pointer + 1);
        }
    }
    public String toString() {
        al = new ArrayList<String>();
        traverse(root, "");
        return "\nTernary Search Tree : "+ al;
    }

    public void traverse(SearchNode r, String str) {
        if (r != null) {
            traverse(r.left, str);

            str = str + r.character;
            if (r.isEndOString)
                al.add(str);

            traverse(r.equal, str);
            str = str.substring(0, str.length() - 1);

            traverse(r.right, str);
        }
    }
}