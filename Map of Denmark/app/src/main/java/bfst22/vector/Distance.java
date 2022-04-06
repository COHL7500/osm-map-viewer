package bfst22.vector;


public class Distance {

    Coordinates coordinates;
    /*
    Double lat1 = coordinates.getMinX();
    Double lon1 = coordinates.getMinY();
    Double lat2 = coordinates.getMaxX();
    Double lon2 = coordinates.getMaxY();
    */

    final int R = 6371;

    public Double minimumCost()


    //Computes the straight-line distance between two pairs of lat/lon using Haversine formula.
    /*
    public Double haversineFormula(Double lat1, Double lon1, Double lat2, Double lon2){
        final int R = 6371; //Earth's radius in kilometers.
        Double dLat = toRadians(lat1 - lat2);
        Double dLon = toRadians(lon1 - lon2);

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2))
                            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; //Returns d
    }
    */


    public float haversineFormula(OSMNode start, OSMNode target){
        final int R = 6371; //Earth's radius in kilometers.
        float dLat = toRadians(start.getLat() - target.getLat());
        float dLon = toRadians(start.getLon() - target.getLon());

        float a = (float)Math.sin(dLat / 2) * (float)Math.sin(dLat / 2) +
                (float)Math.cos(toRadians(start.getLat())) * (float)Math.cos(toRadians(target.getLat()))
                        * (float)Math.sin(dLon / 2) * (float)Math.sin(dLon / 2);

        float c = 2 * (float)Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; //Returns d
    }

    public float toRadians(float val){
        return val * (float)Math.PI / 180;
    }

}
