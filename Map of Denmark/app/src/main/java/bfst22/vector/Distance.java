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

    public Double haversineFormula(Double lat1, Double lon1, Double lat2, Double lon2){
        Double DeltaLat = toRadians(lat1 - lat2);
        Double DeltaLon = toRadians(lon1 - lon2);

        Double a = Math.sin(DeltaLat / 2) * Math.sin(DeltaLat / 2) +
                    Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2))
                            * Math.sin(DeltaLon / 2) * Math.sin(DeltaLon / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; //Returns d
    }

    public Double toRadians(Double val){
        return val * Math.PI / 180;
    }

}
