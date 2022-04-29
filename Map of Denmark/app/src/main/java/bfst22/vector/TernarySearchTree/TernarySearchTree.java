package bfst22.vector.TernarySearchTree;

import java.util.ArrayList;

public class TernarySearchTree {

    private SearchNode root;
    private ArrayList entireTree;
    private ArrayList suggestions;

    public TernarySearchTree() {
        root = null;
    }

    public SearchNode getRoot() {
        return root;
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

    public SearchNode search(String word) {
        return search(root, word.toLowerCase().toCharArray(), 0);
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

    public ArrayList<SearchNode> suggestions(String string) {
        suggestions = new ArrayList<>();
        traverseForSuggestions(nodeOfLastLetter(string), string);
        return suggestions;
    }

    public void traverseForSuggestions(SearchNode node, String string) {
        if (node != null) {
            traverseForSuggestions(node.left, string);

            string = string + node.character;
            if (node.isEndOString)
                suggestions.add(node);

            traverseForSuggestions(node.equal, string);
            string = string.substring(0, string.length() - 1);

            traverseForSuggestions(node.right, string);
        }
    }

    //Helper method for searchsuggestions
    private SearchNode nodeOfLastLetter(String word) {
        return nodeOfLastLetter(root, word.toCharArray(), 0);
    }

    //Helper method for searchsuggestions
    private SearchNode nodeOfLastLetter(SearchNode node, char[] word, int index) {
        if (node == null || word.length == 0)
            return null;
        if (word[index] < node.character)
            return nodeOfLastLetter(node.left, word, index);
        else if (word[index] > node.character)
            return nodeOfLastLetter(node.right, word, index);
        else {
            if (index == word.length - 1)
                return node;
            else
                return nodeOfLastLetter(node.equal, word, index + 1);
        }
    }

    public String toString() {
        entireTree = new ArrayList<String>();
        traverse(root, "");
        return "\nTernary Search Tree : "+ entireTree;
    }

    public void traverse(SearchNode node, String string) {
        if (node != null) {
            traverse(node.left, string);

            string = string + node.character;
            if (node.isEndOString)
                entireTree.add(string);

            traverse(node.equal, string);
            string = string.substring(0, string.length() - 1);

            traverse(node.right, string);
        }
    }

    //Experimental code for better autocomplete/suggestions - not working at the moment

    public String autoComplete(String word) {
        return autoComplete(root, word.toLowerCase().toCharArray(),0);
    }

    private String autoComplete(SearchNode node, char[] word, int index) {
        String result;
        if (node == null || word.length <= index) return null;
        if (word[index] == node.character) {
            if (word[index] == word.length - 1) return word.toString();
            else {
                result = (node.equal == null ? null : autoComplete(node.equal, word, index + 1));
                if (result == null) result = word.toString();
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