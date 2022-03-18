package bfst22.vector;

import java.util.regex.Pattern;

public class Address {
    public final String street, house, postcode, city;

    private Address(
            String _street, String _house, String _postcode, String _city) {
        street = _street;
        house = _house;

        postcode = _postcode;
        city = _city;
    }

    public String toString() {
        return street + " " + house + ", " + postcode + " " + city;
    }

    private final static String REGEX = "^(?<street>[A-Za-zæøå ]+?) +(?<house>[0-9]+)[, ]+(?<postcode>[0-9]{4}) +(?<city>[A-Za-zæøå ]+)$";

    private final static Pattern PATTERN = Pattern.compile(REGEX);

    public static Address parse(String input) {
        var builder = new Builder();
        var matcher = PATTERN.matcher(input);
        if (matcher.matches()) {
            builder.street(matcher.group("street"));
            builder.house(matcher.group("house"));
            builder.postcode(matcher.group("postcode"));
            builder.city(matcher.group("city"));
        }
        return builder.build();
    }

    public static class Builder {
        private String street, house, postcode, city;

        public Builder street(String _street) {
            street = _street;
            return this;
        }

        public Builder house(String _house) {
            house = _house;
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

        public Address build() {
            return new Address(street, house, postcode, city);
        }
    }
}