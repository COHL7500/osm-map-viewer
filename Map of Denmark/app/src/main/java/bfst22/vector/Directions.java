package bfst22.vector;

public class Directions {
    public float angle;
    Distance d = new Distance();

    /* Returns a String with the description of the next turn */
    public String turn(PolyPoint start, PolyPoint to){
            this.angle = getAngle(start, to);

            /*Driving Forward*/
            if (angle > 45 && angle < 135) {
                return "Continue along /Certain Address/ for " + d.haversineFormula(start, to) * 1000 + " meter" + " Angle: " + angle + " " + start.lat + " " + start.lon;
            }
            /*Right Turn*/
            if (angle < 45 && angle > 315) {
                return "Turn right onto /Certain Address/ for " + d.haversineFormula(start, to) * 1000 + " meter" + " Angle: " + angle + start.lat + " " + start.lon;
            }
            /*Left Turn*/
            if(angle > 135 && angle < 225){
                return "Turn left onto /Certain Address/ for " + d.haversineFormula(start, to) * 1000 + " meter" + " Angle: " + angle + start.lat + " " + start.lon;
            }
            else return "Invalid Angle - Not beetween 0 <= angle <= 360, " + "The invalid Angle: " + angle + " " + start.lat + " " + start.lon;
    }

    /* Returns the angle between two nodes (Start of the road to the end of the road) */
    public float getAngle(PolyPoint from, PolyPoint to){
        /*
        public float getAngle(PolyPoint from, PolyPoint fixed, PolyPoint to){
        float angle1 = (float)Math.atan2(from.lat - fixed.lat, from.lon - fixed.lon);
        float angle2 = (float)Math.atan2(to.lat - fixed.lat, to.lon - fixed.lon);
        float result = (float)Math.toDegrees(angle1-angle2);

        if(result < 0) result += 360;
         */

        if(to.lat > from.lat){
            return (float)(Math.toDegrees(Math.atan2(to.lat - from.lat,from.lon - to.lon)));
        }
        if(to.lat < from.lat){
            return (float)(360 - Math.toDegrees(Math.atan2(from.lat - to.lat, from.lon - to.lon)));
        }
        else return (float)Math.toDegrees(Math.atan2(0,0));
    }


}
