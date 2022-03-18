package bfst22.vector;

import java.io.*;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

// Handles the logic of our data and storing it appropriately.
public class Model {
    float minlat, minlon, maxlat, maxlon;

    // Declares and instantiates lines, containing all lines needed to be drawn.
    // Like HashMap, it has key (the enum waytype) and value (list of all lines w/ that waytype).
    MapFeature yamlObj;
    List<Runnable> observers;

    // Loads our OSM file, supporting various formats: .zip and .osm, then convert it into an .obj.
    public Model(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        this.observers = new ArrayList<>();
        this.yamlObj = new Yaml(new Constructor(MapFeature.class)).load(this.getClass().getResourceAsStream("WayConfig.yaml"));

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
                yamlObj = (MapFeature) input.readObject();
            }
        }

        if (!filename.endsWith(".obj")) save(filename);
    }

    // Saves the .obj file in our project hierachy.
    public void save(String basename) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(yamlObj);
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

        String suptype = null, subtype = null, name = null;

        // Reads the entire .OSM file.
        while (reader.hasNext()) {
            switch (reader.next()) {

                // Reads the first element of a line.
                case XMLStreamConstants.START_ELEMENT:
                    switch (reader.getLocalName()) {

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
                            break;

                        // Parses the key and value of tags, changing the waytype to the corresponding type.
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");
                            if(k.equals("name")) name = v;
                            if(this.yamlObj.ways.containsKey(k)){
                                suptype = k;
                                subtype = v;
                            }
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
                            if(this.yamlObj.ways.containsKey(suptype) && this.yamlObj.ways.get(suptype).valuefeatures.containsKey(subtype)) {
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).drawable.add(way);
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).name = name;
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).nameCenter = way.getCenter();
                                //if(name != null)
                                //    System.out.println(name + " " + way.getCenter()[0] + ", " + way.getCenter()[1]);
                            }
                            subtype = suptype = name = null;
                            nodes.clear();
                            break;

                        // is a collection of ways and has to be drawn separately with MultiPolygon.
                        case "relation":
                            if(suptype != null && !rel.isEmpty() && this.yamlObj.ways.containsKey(suptype) && this.yamlObj.ways.get(suptype).valuefeatures.containsKey(subtype)) {
                                var multipoly = new MultiPolygon(rel);
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).drawable.add(multipoly);
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).name = name;
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).nameCenter = multipoly.getCenter();
                                //if(name != null)
                                //    System.out.println(name + " " + multipoly.getCenter()[0] + ", " + multipoly.getCenter()[1]);
                            }
                            subtype = suptype = name = null;
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
        observers.forEach(Runnable::run);
    }
}