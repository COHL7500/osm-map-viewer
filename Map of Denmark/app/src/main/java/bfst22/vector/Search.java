package bfst22.vector;

import bfst22.vector.TernarySearchTree.SearchNode;

import java.util.ArrayList;

public class Search {
    private Model model;
    private ArrayList<Address> searchResults;

    public Search(Model _model) {
        this.model = _model;
    }

    public Address addressSearch(String searchString) {
        return model.addresses.get(model.getSearchTree().search(searchString).getId());
    }

   public ArrayList<Address> searchSuggestions(String searchString) {
        ArrayList<Address> searchSuggestions = new ArrayList<Address>();
            for (SearchNode node : model.getSearchTree().suggestions(searchString)) {
                    searchSuggestions.add(model.addresses.get(node.getId()));
            }
        return searchSuggestions;
     }
}