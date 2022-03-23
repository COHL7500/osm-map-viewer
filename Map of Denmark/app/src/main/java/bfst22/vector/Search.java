package bfst22.vector;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Search {
    private String searchString;

    public Search(String _searchString) {
        this.searchString = _searchString;
    }

    public ArrayList<Address> searchResults(String searchString, ArrayList<Address> addresses) {
        Address _searchString = Address.parse(searchString);
        ArrayList<Address> bestResults = new ArrayList<>();
        int index = Collections.binarySearch(addresses, _searchString);
        for (int i = 0; i < 5; i ++) {
            if (index >= 0) {
                bestResults.add(addresses.get(index+i));
            }
        }
        return bestResults;
    }
}
