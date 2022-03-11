package bfst22.vector;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// Handles the logic of our data and storing it appropriately.

public class Model {
    float minlat, minlon, maxlat, maxlon;

    // Declares and instantiates lines, containing all lines needed to be drawn.
    // Like HashMap, it has key (the enum waytype) and value (list of all lines w/ that waytype).
    Map<WayType,List<Drawable>> lines = new EnumMap<>(WayType.class); {
        for (var type : WayType.values()) lines.put(type, new ArrayList<>());
    }
    List<Runnable> observers = new ArrayList<>();

    // Loads our OSM file, supporting various formats: .zip and .osm, then convert it into an .obj.
    @SuppressWarnings("unchecked")
    public Model(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        var time = -System.nanoTime();
        if (filename.endsWith(".zip")) {
            var zip = new ZipInputStream(new FileInputStream(filename));
            zip.getNextEntry();
            loadOSM(zip);
        } else if (filename.endsWith(".osm")) {
            loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".obj")) {
            try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                lines = (Map<WayType,List<Drawable>>) input.readObject();
            }
        }

        time += System.nanoTime();
        System.out.println("Load time: " + (long)(time / 1e6) + " ms");
        if (!filename.endsWith(".obj")) save(filename);
    }

    // Saves the .obj file in our project hierachy.
    public void save(String basename) throws FileNotFoundException, IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(lines);
        }
    }

    // Parses and reads the loaded .osm file, interpreting the data however it is configured.
    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError {

        // Reads the .osm file, being an XML file.
        var reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input));

        // Converts IDs into nodes (uncertain about this).
        var id2node = new NodeMap();

        // Saves the ID of a particular way (Long) and stores the way as a value (OSMWay).
        var id2way = new HashMap<Long, OSMWay>();

        // A list of nodes drawing a particular element of map. Is cleared when fully drawn.
        var nodes = new ArrayList<OSMNode>();

        // Saves all relations.
        var rel = new ArrayList<OSMWay>();

        // ID of the current relation.
        long relID = 0;

        var type = WayType.UNKNOWN;

        // Reads the entire .OSM file.
        while (reader.hasNext()) {
            switch (reader.next()) {

                // Reads the first element of a line.
                case XMLStreamConstants.START_ELEMENT:
                    var name = reader.getLocalName();
                    switch (name) {

                        // Configures the longitude and latitude. An element present in all OSM files.
                        // Uncertain as to why, though adjusting the floats will make the map not draw.
                        case "bounds":
                            maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                            minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                            maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            break;

                        // Parses information from a node, adding it to the id2node list.
                        case "node":
                            var id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            var lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            var lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            id2node.add(new OSMNode(id, 0.56f * lon, -lat));
                            break;

                        // parses reference to a node (ID) and adds it to the node list.
                        case "nd":
                            var ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            nodes.add(id2node.get(ref));
                            break;

                        // Parses the ID of the way and sets a default type.
                        // For future reference, type could probably be configured properly in this step.
                        case "way":
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            type = WayType.UNKNOWN;
                            break;

                        // Parses the key and value of tags, changing the waytype to the corresponding type.
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");

                            if (k.equals("natural") && v.equals("water")) type = WayType.WATER;

                            break;

                        // parses a member (a reference to a way belonging to a collection of ways; relations)
                        case "member":
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            var elm = id2way.get(ref);
                            if (elm != null) rel.add(elm);
                            break;
                        }
                        break;

                // Reads the last element, such as "way", "relation" etc.
                case XMLStreamConstants.END_ELEMENT:
                    switch (reader.getLocalName()) {

                        // "way" - All lines in the program; linking point A to B
                        case "way":
                            var way = new PolyLine(nodes);
                            id2way.put(relID, new OSMWay(nodes));
                            lines.get(type).add(way);
                            nodes.clear();
                            break;

                        // is a collection of ways and has to be drawn separately with MultiPolygon.
                        case "relation":

                            // if the relation is water, then it draws
                            if (type == WayType.WATER && !rel.isEmpty()) {
                                lines.get(type).add(new MultiPolygon(rel));
                            }
                            rel.clear();
                            break;
                    }
                    break;
            }
        }
    }

    public void addObserver(Runnable observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (var observer : observers) {
            observer.run();
        }
    }

    // iterates through arraylist of lines
    public Iterable<Drawable> iterable(WayType type) {
        return lines.get(type);
    }
}
