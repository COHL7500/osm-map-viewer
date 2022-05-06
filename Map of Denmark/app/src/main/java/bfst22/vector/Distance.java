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

    //Computes the straight-line distance between two pairs of lat/lon using Haversine formula.
    public double haversineFormula(PolyPoint start, PolyPoint target){
        final int R = 6371; //Earth's radius in kilometers.
        double dLat = toRadians(start.lat - target.lat);
        double dLon = toRadians(start.lon - target.lon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadians(start.lat)) * Math.cos(toRadians(target.lat))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; //Returns d
    }

    public double toRadians(double val){
        return val * Math.PI / 180;
    }

}
