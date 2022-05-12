package bfst22.vector;

public class Directions {
    private float angle;
    private float distance;
    Distance d = new Distance();

    public float getAngleDifference(PolyPoint previous1, PolyPoint previous2,PolyPoint current1, PolyPoint current2){
        float angle1 = getAngle(previous1, previous2);
        float angle2 = getAngle(current1, current2);
        return angle2 - angle1;
    }

    /* Returns a String with the description of the next turn */
    public String turn(float angle, float angledifference, PolyPoint current1, PolyPoint current2){

            if(angledifference >= -100 && angledifference <= -45 ){
                if(angle > 270 && angle <= 360){
                    return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                if(angle > 180 && angle <= 270){
                    return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                if(angle > 90 && angle <= 180){
                    return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                if(angle > 0 && angle <= 90){
                    return "Turn left onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
            }

            if(angledifference <= 100 && angledifference >= 45) {
                if(angle > 270 && angle <= 360){
                    return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                if(angle > 180 && angle <= 270){
                    return "Turn left onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                if(angle > 90 && angle <= 180){
                    return "Turn right onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                if(angle > 0 && angle <= 90){
                    return "Turn left onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
                }
                return "Turn left onto " + current2.address + " and continue for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;
            }
            else return "Continue along " + current2.address + " for " + stringDistance(current1,current2) + " meters" + " Angle: " + getAngle(current1,current2) + " Point " + current1.lat + " " + current2.lon + " speedlimit: " + current2.speedLimit;


            /*
            if (angle > 45 && angle < 135) {
                return
            }
            if (angle < 45 && angle > 315) {

            }
            if(angle > 135 && angle < 225){
                return "Turn left onto /Certain Address/ for " + distanceString + " meters" + " Angle: " + angle + start.lat + " " + start.lon;
            }
            else return "Invalid Angle - Not beetween 0 <= angle <= 360, " + "The invalid Angle: " + angle + " " + start.lat + " " + start.lon;
            */
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

    public String stringDistance(PolyPoint start, PolyPoint target){
        this.distance = d.haversineFormula(start,target) * 1000; //Gives distance and converts it to meter
        return String.format("%.0f",distance); //Converting into 0 floating point
    }


}
