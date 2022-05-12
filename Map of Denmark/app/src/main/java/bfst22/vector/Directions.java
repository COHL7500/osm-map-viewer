package bfst22.vector;

public class Directions {
    Distance d = new Distance();

    public Directions() {
    }

    public float getAngleDifference(PolyPoint previous1, PolyPoint previous2,PolyPoint current1, PolyPoint current2){
        float angle1 = getAngle(previous1, previous2);
        float angle2 = getAngle(current1, current2);
        return angle2 - angle1;
    }

    /* Returns a String with the description of the next turn */
    public String turn(float angle, float angledifference, PolyPoint current1, PolyPoint current2){

            if(angledifference >= 90 && angle > 180){ //Right turn
                return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon;
            } else if(angledifference >= 90 && angle < 180){
                return "Turn left onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon;
            }

            if(angledifference <= -90 && angle < 180) { //Left turn
                return "Turn left onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon;
            } else if (angledifference <= -90 && angle > 180){
                return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon;
            }
            else return "Continue along " + current2.address + " for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon;
    }

    /* Returns the angle between two nodes (Start of the road to the end of the road) */
    public float getAngle(PolyPoint from, PolyPoint to){

        if(to.lat > from.lat){
            return (float)(Math.toDegrees(Math.atan2(to.lat - from.lat,from.lon - to.lon)));
        }
        if(to.lat < from.lat){
            return (float)(360 - Math.toDegrees(Math.atan2(from.lat - to.lat, from.lon - to.lon)));
        }
        else return (float)Math.toDegrees(Math.atan2(0,0));
    }

    public String stringDistance(PolyPoint start, PolyPoint target){
        float distance = d.haversineFormula(start, target) * 1000; //Gives distance and converts it to meter
        return String.format("%.0f", distance); //Converting into 0 floating point
    }


}
