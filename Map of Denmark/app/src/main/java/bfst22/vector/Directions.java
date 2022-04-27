package bfst22.vector;

public class Directions {
    Distance d = new Distance();
    Address adress;

    /* Returns a String with the description of the next turn */
    public String turn(PolyPoint start, PolyPoint to){
            float angle = getAngle(start, to);

            /*Driving Forward*/
            if (angle > 45 || angle < 135) {
                return "Continue along " + adress.street + "for " + d.haversineFormula(start, to) + " m";
            }
            /*Right Turn*/
            if (angle < 45 || angle > 315) {
                return "Turn right onto " + adress.street + "for " + d.haversineFormula(start, to) + " m";
            }
            /*Left Turn*/
            if(angle > 135 || angle < 225){
                return "Turn left onto " + adress.street + "for " + d.haversineFormula(start, to) + " m";
            }
            else return null; //Ved ikke lige hvad ellers man kan return for nu
    }

    /* Returns the angle between two nodes (Start of the road to the end of the road) */
    public float getAngle(PolyPoint from, PolyPoint to){
        float angle = (float) Math.toDegrees(Math.atan2(to.lat - from.lat,to.lon - from.lon));

        return angle;
    }

}
