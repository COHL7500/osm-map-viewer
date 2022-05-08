package bfst22.vector;

import javafx.geometry.Point2D;
import java.util.*;

public class TernarySearchTree {
    private SearchNode root;
    private final List<Address> addresses;

    public TernarySearchTree() {
        this.addresses = new ArrayList<>();
        this.insertAddress();
    }

    public void insertAddress(){
        if(this.addresses.isEmpty() || !addresses.get(addresses.size()-1).isEmpty())
            this.addresses.add(new Address());
    }

    public void setAddressPos(float lon, float lat){
        addresses.get(addresses.size()-1).setPos(lon,lat);
    }

    public void addAddressElement(String key, String value){
        addresses.get(addresses.size()-1).addElement(key,value);
    }

    public void generate(){
        Collections.sort(this.addresses);
        this.addresses.forEach(address -> {
            if(!address.isEmpty()){
                int id = this.addresses.indexOf(address);
                char[] addrList = address.toString().toLowerCase().toCharArray();
                this.root = insert(root, addrList, 0, id);
            }
        });
    }

    private SearchNode insert(SearchNode node, char[] word, int index, int id) {
        if (node == null) node = new SearchNode(word[index], id);
        if (word[index] < node.character) node.left = insert(node.left, word, index, id);
        else if (word[index] > node.character) node.right = insert(node.right, word, index, id);
        else {
            if ((index+1) < word.length) node.equal = insert(node.equal, word, index + 1, id);
            else node.isEndOString = true;
        }

        return node;
    }

    private SearchNode search(SearchNode node, char[] word, int index) {
        if (node == null || word.length == 0) return null;
        if (word[index] < node.character) return search(node.left, word, index);
        else if (word[index] > node.character) return search(node.right, word, index);
        else {
            if (node.isEndOString && index == (word.length-1)) return node;
            else if (index == (word.length-1)) return null;
            else return search(node.equal, word, index + 1);
        }
    }

    private SearchNode nodeOfLastLetter(SearchNode node, char[] word, int index) {
        if (node == null || word.length == 0) return null;
        if (word[index] < node.character) return nodeOfLastLetter(node.left, word, index);
        else if (word[index] > node.character) return nodeOfLastLetter(node.right, word, index);
        else return (index == (word.length-1)) ? node : nodeOfLastLetter(node.equal, word, index+1);
    }

    public List<Address> suggestions(String string) {
        return (List<Address>)(List<?>) traverse(new ArrayList<>(), nodeOfLastLetter(this.root, string.toCharArray(), 0), string, true);
    }

    private List<Object> traverse(List<Object> tree, SearchNode node, String string, boolean suggestion) {
        if(node == null) return tree;

        tree = traverse(tree, node.left, string, suggestion);
        string += node.character;

        if (node.isEndOString) tree.add(suggestion ? this.addresses.get(node.id) : string);

        tree = traverse(tree, node.equal, string, suggestion);
        string = string.substring(0, string.length() - 1);

        tree = traverse(tree, node.right, string, suggestion);
        return tree;
    }

    public String autoComplete(String word) {
        return autoComplete(root, word.toLowerCase().toCharArray(), 0);
    }

    private String autoComplete(SearchNode node, char[] word, int index) {
        String result;
        if (node == null || word.length <= index) return null;
        if (word[index] == node.character) {
            if (word[index] == (word.length-1)) return Arrays.toString(word);
            else {
                result = autoComplete(node.equal, word, index+1);
                if (result == null) result = Arrays.toString(word);
            }
        }

        result = autoComplete((word[index] > node.character ? node.right : node.left), word, index + 1);
        return result;
    }

    public Address addressSearch(String searchString) {
        return this.addresses.get(this.search(root, searchString.toLowerCase().toCharArray(), 0).id);
    }

    public List<Address> searchSuggestions(String searchString) {
        return this.suggestions(searchString).stream().limit(6).toList();
    }

    private static class SearchNode {
        public char character;
        public boolean isEndOString;
        public SearchNode left;
        public SearchNode equal;
        public SearchNode right;
        public int id;

        public SearchNode(char character, int id) {
            this.character = character;
            this.id = id;
            this.isEndOString = false;
        }
    }

    public static class Address implements Comparable<Address> {
        //private final static String REGEX = "^ *(?<street>[1-9A-Za-zÆØÅæøåÉéÈèÄäÜüŸÿÖö. ]+?) +(?<house>[0-9]+[., ]+)((?<floor>[0-9]?[., ]+)?(?<side>[A-Za-zæøå0-9]+[., ]?))?( +)?(?<postcode>[0-9]{4})?( +)?(?<city>[A-Za-zÆØÅæøåÉéÈèÄäÜüŸÿÖö .]+)?$";
        //private final static Pattern PATTERN = Pattern.compile(REGEX);
        public Map<String,String> elements;
        public Point2D coordPos;

        public Address(){
            this.elements = new HashMap<>();
        }

        public void setPos(float lon, float lat){
            this.coordPos = new Point2D(lon,lat);
        }

        public void addElement(String key, String value){
            this.elements.put(key,this.elements.containsKey(key) ? value : value.intern());
        }

        public boolean isEmpty(){
            return !this.elements.containsKey("street");
        }

        public String toString() {
            return toStringKey("street"," ") +
                    toStringKey("house",", ") +
                    toStringKey("floor",", ") +
                    toStringKey("side"," ") +
                    toStringKey("postcode"," ") +
                    toStringKey("city","");
        }

        private String toStringKey(String key, String ifTrue){
            return (this.elements.containsKey(key) ? this.elements.get(key) + ifTrue : "");
        }

        @Override public int compareTo(Address that) {
            if (!this.elements.containsKey("street") && !that.elements.containsKey("street")) return 0;
            else if (!this.elements.containsKey("street")) return 1;
            else if (!that.elements.containsKey("street")) return -1;
            else return this.elements.get("street").toLowerCase().compareTo(that.elements.get("street").toLowerCase());
        }
    }
}