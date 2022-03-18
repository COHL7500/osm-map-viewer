package bfst22.vector;

import java.util.regex.Pattern;

public class Address {
    public final String street, house, floor, side, postcode, city;
    public final float lat, lon;

    private Address(
            String _street, String _house, String _floor, String _side, String _postcode, String _city, float _lat, float _lon) {
        street = _street;
        house = _house;
        floor = _floor;
        side = _side;
        postcode = _postcode;
        city = _city;
        lon = _lon;
        lat = _lat;
    }

    public String toString() {
        return (street !=null ? street + " " : "") + (house !=null ? house + ", " : "") + (floor != null ? floor + " " : "") + (side != null ? side + " " : "") + (postcode != null ? postcode + " " : "") + city;
    }

    private final static String REGEX = "^ *(?<street>[1-9A-Za-zÆØÅæøåÉéÈèÄäÜüŸÿÖö. ]+?) +(?<house>[0-9]+[., ]+)((?<floor>[0-9]?[., ]+)?(?<side>[A-Za-zæøå0-9]+[., ]?))?( +)?(?<postcode>[0-9]{4})?( +)?(?<city>[A-Za-zÆØÅæøåÉéÈèÄäÜüŸÿÖö .]+)?$";

    private final static Pattern PATTERN = Pattern.compile(REGEX);

    public static Address parse(String input) {
        var builder = new Builder();
        var matcher = PATTERN.matcher(input);
        if (matcher.matches()) {
            builder.street(matcher.group("street"));
            builder.house(matcher.group("house"));
            builder.floor(matcher.group("floor"));
            builder.side(matcher.group("side"));
            builder.postcode(matcher.group("postcode"));
            builder.city(matcher.group("city"));
        }
        return builder.build();
    }

    public static class Builder {
        private String street, house, floor, side, postcode, city;
        private float lat, lon;

        public Builder street(String _street) {
            street = _street;
            return this;
        }

        public Builder house(String _house) {
            house = _house;
            return this;
        }

        public Builder floor(String _floor) {
            floor = _floor;
            return this;
        }

        public Builder side(String _side) {
            side = _side;
            return this;
        }

        public Builder postcode(String _postcode) {
            postcode = _postcode;
            return this;
        }

        public Builder city(String _city) {
            city = _city;
            return this;
        }

        public Builder lat(float _lat) {
            lat = _lat;
            return this;
        }

        public Builder lon(float _lon) {
            lon = _lon;
            return this;
        }

        public Address build() {
            return new Address(street, house, floor, side, postcode, city, lat, lon);
        }
    }
}