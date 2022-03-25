package bfst22.vector;

import java.util.ArrayList;
import java.util.Collections;

public class Search {
    private ArrayList<Address> sixBestResults;

    public ArrayList<Address> searchResults(String searchString, ArrayList<Address> addresses) {
        Address _searchString = null;
        sixBestResults = new ArrayList<>();

        _searchString = Address.parse(searchString);
        int index = Collections.binarySearch(addresses, _searchString);
        sixBestResults = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (index >= 0) {
                sixBestResults.add(addresses.get(index + i));
            }
            else{
                sixBestResults.add(addresses.get(-index - 1 + i));
                }
            }
            return sixBestResults;
        }

        public void showResults() {
            for (Address result : sixBestResults) {
                System.out.println(result.toString());
            }
        }
}

