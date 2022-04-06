package bfst22.vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    // Declares and instantiates lines, containing all lines needed to be drawn.
    // Like HashMap, it has key (the enum waytype) and value (list of all lines w/ that waytype).
    public MapFeature yamlObj;
    public KdTree kdtree;
    public boolean isOMSloaded;
    public float minlat, minlon, maxlat, maxlon;
    public int nodecount, waycount, relcount;
    public String currFileName;
    public long loadTime, filesize;
	public VehicleType vehicleType;
    public Edge e;

    public Model(){
        this.isOMSloaded = false;
    }

    // Loads our OSM file, supporting various formats: .zip and .osm, then convert it into an .obj.
    public void loadMapFile(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        this.currFileName = filename;
        if (filename.endsWith(".zip")) {
            var zip = new ZipInputStream(new FileInputStream(filename));
            zip.getNextEntry();
            this.loadOSM(zip);
        } else if (filename.endsWith(".osm")) {
            this.loadOSM(new FileInputStream(filename));
        } /*else if (filename.endsWith(".obj")) {
            try (var input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                //yamlObj = (MapFeature) input.readObject();
            }
        }*/

        //if (!filename.endsWith(".obj")) save(filename);
    }

    // Saves the .obj file in our project hierachy.
    public void save(String basename) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            //out.writeObject(yamlObj);
        }
    }

    // Parses and reads the loaded .osm file, interpreting the data however it is configured.
    public void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError, IOException {
        this.loadTime = System.nanoTime();
        this.filesize = Files.size(Paths.get(this.currFileName));
        /*
        long start = System.nanoTime();
        methodToBeTimed();
        long elapsedTime = System.nanoTime() - start;
        * */

        this.yamlObj = new Yaml(new Constructor(MapFeature.class)).load(this.getClass().getResourceAsStream("WayConfig.yaml"));
        this.kdtree = new KdTree();
        this.isOMSloaded = true;

<<<<<<< HEAD
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

        String valueType = null, keyType = null, name = null;
=======
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // Reads the .osm file, being an XML file.
        NodeMap id2node = new NodeMap(); // Converts IDs into nodes (uncertain about this).
        Map<Long, PolyLine> id2way = new HashMap<>(); // Saves the ID of a particular way (Long) and stores the way as a value (OSMWay).
        List<PolyPoint> nodes = new ArrayList<>(); // A list of nodes drawing a particular element of map. Is cleared when fully drawn.
        List<PolyLine> rel = new ArrayList<>(); // Saves all relations.
        long relID = 0; // ID of the current relation.
<<<<<<< HEAD
        String keyType = null, valueType = null, name = null;
>>>>>>> main
=======
        String suptype = null, subtype = null, name = null;
>>>>>>> parent of 7cfd49f (refactor code)

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
                            this.nodecount++;
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
                            if(this.yamlObj.ways.containsKey(k))
                            {
<<<<<<< HEAD
<<<<<<< HEAD
                                valueType = k;
                                keyType = v;
=======
                                keyType = k;
                                valueType = v;
>>>>>>> main
=======
                                suptype = k;
                                subtype = v;
>>>>>>> parent of 7cfd49f (refactor code)

                                switch(k) {
                                    case "motorcar":
                                        vehicleType = VehicleType.MOTORCAR;
                                        e.isAllowed = v.equals("yes");
                                        break;

                                    case "bicycle":
                                        vehicleType = VehicleType.BICYCLE;
                                        e.isAllowed = v.equals("yes");
                                        break;
                                    case "foot":
                                        vehicleType = VehicleType.FOOT;
                                        e.isAllowed = v.equals("yes");
                                        break;
                                    case "oneway":
                                        if(v.equals("yes")) e.isOneway = true;
                                        break;
                                    case "maxspeed":
                                        e.speedLimit = Integer.parseInt(v);
                                        break;
                                    case "restriction":
                                        if(v.equals("no_left_turn")) e.leftTurn = false;
                                        else if(v.equals("no_right_turn")) e.rightTurn = false;
                                        break;
                                }
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
                            this.kdtree.add(way);
                            this.waycount++;
                            nodes.clear();
<<<<<<< HEAD
                            if(this.yamlObj.ways.containsKey(valueType) && this.yamlObj.ways.get(valueType).valuefeatures.containsKey(keyType)) {
                                this.yamlObj.ways.get(valueType).valuefeatures.get(keyType).drawable.add(way);
=======

<<<<<<< HEAD
                            if(this.yamlObj.ways.containsKey(keyType) && this.yamlObj.ways.get(keyType).valuefeatures.containsKey(valueType)) {
                                this.yamlObj.ways.get(keyType).valuefeatures.get(valueType).drawable.add(way);
>>>>>>> main
=======
                            if(this.yamlObj.ways.containsKey(suptype) && this.yamlObj.ways.get(suptype).valuefeatures.containsKey(subtype)) {
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).drawable.add(way);
>>>>>>> parent of 7cfd49f (refactor code)
                            }
                            keyType = valueType = name = null;
                            break;

                        // is a collection of ways and has to be drawn separately with MultiPolygon.
                        case "relation":
                            var multipoly = new MultiPolygon(rel);
                            this.kdtree.add(multipoly);
                            this.relcount++;
                            rel.clear();
<<<<<<< HEAD
                            keyType = valueType = name = null;
=======

<<<<<<< HEAD
>>>>>>> main
=======
                            /*if(suptype != null && !rel.isEmpty() && this.yamlObj.ways.containsKey(suptype) && this.yamlObj.ways.get(suptype).valuefeatures.containsKey(subtype)) {
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).drawable.add(multipoly);
                            }*/

>>>>>>> parent of 7cfd49f (refactor code)
                            break;
                    }
                    break;
            }
        }

        this.kdtree.generate();
        this.loadTime = System.nanoTime() - this.loadTime;
    }

    public void unloadOSM(){
        this.isOMSloaded = false;
        this.kdtree = new KdTree();
    }
}