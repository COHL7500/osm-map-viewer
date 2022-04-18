package bfst22.vector;

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

    public Address autoComplete(String searchString) {
        return model.addresses.get(model.getSearchTree().autoComplete(searchString).getId());
    }
}
