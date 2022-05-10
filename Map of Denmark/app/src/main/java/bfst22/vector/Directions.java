package bfst22.vector;

public class Directions {
    Distance d = new Distance();
    public float angle;

    /* Returns a String with the description of the next turn */
    public String turn(PolyPoint start, PolyPoint to){
            angle = getAngle(start,to);

            /*Driving Forward*/
            if (angle > 165 && angle < 190) {
                return "Continue along /Certain Address/ for " + d.haversineFormula(start, to) * 1000 + " meter";
            }
            /*Right Turn*/
            if (angle < 165 && angle > 0) {
                return "Turn right onto /Certain Address/ for " + d.haversineFormula(start, to) * 1000 + " meter";
            }
            /*Left Turn*/
            else if(angle > 190 && angle < 360){
                return "Turn left onto /Certain Address/ for " + d.haversineFormula(start, to) * 1000 + " meter";
            }
            return null;
    }

    /* Returns the angle between two nodes (Start of the road to the end of the road) */
    public float getAngle(PolyPoint from, PolyPoint to){
        if(to.lat > from.lat){
            return (float)(Math.atan2((to.lat - from.lat),(from.lon - to.lon)) * 180 / Math.PI); //Above 0 to 180
        }
        else if(to.lat < from.lat){
            return 360 - (float)(Math.atan2((to.lat - from.lat), (from.lon - to.lon)) * 180 / Math.PI); //Above 180 to 360
        }
        return (float)Math.atan2(0,0);

        /*
        float angle1 = (float)Math.atan2(from.lon - middle.lon, from.lat - middle.lat);
        float angle2 = (float)Math.atan2(to.lon - middle.lon, to.lat - middle.lat);

        return (float)Math.toDegrees(angle1) - (float)Math.toDegrees(angle2);
        */
    }


}
