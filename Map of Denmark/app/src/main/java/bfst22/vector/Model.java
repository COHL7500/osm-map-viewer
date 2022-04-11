package bfst22.vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.xml.stream.*;

import bfst22.vector.TernarySearchTree.TernarySearchTree;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

// Handles the logic of our data and storing it appropriately.
public class Model {

    // Declares and instantiates lines, containing all lines needed to be drawn.
    // Like HashMap, it has key (the enum waytype) and value (list of all lines w/ that waytype).
    public MapFeature yamlObj;
    public KdTree kdtree;
    public List<Runnable> observers;
    public boolean isOMSloaded;
    public float minlat, minlon, maxlat, maxlon;
    public float tempLat, tempLon;
    public int nodecount, waycount, relcount;
    public String currFileName;
    public long loadTime, filesize;
	public VehicleType vehicleType;
    public Edge e;
    public ArrayList<Address> addresses = new ArrayList<>();
    public Address.Builder builder = new Address.Builder();
    public TernarySearchTree searchTree = new TernarySearchTree();


    public Model(){
        this.isOMSloaded = false;
    }

    // Loads our OSM file, supporting various formats: .zip and .osm, then convert it into an .obj.
    public void loadMapFile(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        this.currFileName = filename;
        if (filename.endsWith(".zip")) {
            ZipInputStream zip = new ZipInputStream(new FileInputStream(filename));
            zip.getNextEntry();
            this.loadOSM(zip);
        } else if (filename.endsWith(".osm")) {
            this.loadOSM(new FileInputStream(filename));
        } else if (filename.endsWith(".obj")) {
            this.loadTime = System.nanoTime();
            try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                minlat = input.readFloat();
                minlon = input.readFloat();
                maxlat = input.readFloat();
                maxlon = input.readFloat();
                kdtree = (KdTree) input.readObject();
                yamlObj = (MapFeature) input.readObject();
            }

            this.isOMSloaded = true;
            this.loadTime = System.nanoTime() - this.loadTime;
            this.observers = new ArrayList<>();
            this.filesize = Files.size(Paths.get(this.currFileName));
        }

        if (!filename.endsWith(".obj")) save(filename);
    }

    // Saves the .obj file in our project hierachy.
    public void save(String basename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeFloat(minlat);
            out.writeFloat(minlon);
            out.writeFloat(maxlat);
            out.writeFloat(maxlon);
            out.writeObject(kdtree);
            out.writeObject(yamlObj);
        }
    }

    // Parses and reads the loaded .osm file, interpreting the data however it is configured.
    public void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError, IOException {
        this.loadTime = System.nanoTime();
        this.filesize = Files.size(Paths.get(this.currFileName));
        this.yamlObj = new Yaml(new Constructor(MapFeature.class)).load(this.getClass().getResourceAsStream("WayConfig.yaml"));
        this.observers = new ArrayList<>();
        this.kdtree = new KdTree();
        this.isOMSloaded = true;

        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // Reads the .osm file, being an XML file.
        NodeMap id2node = new NodeMap(); // Converts IDs into nodes (uncertain about this).
        Map<Long, PolyLine> id2way = new HashMap<>(); // Saves the ID of a particular way (Long) and stores the way as a value (OSMWay).
        List<PolyPoint> nodes = new ArrayList<>(); // A list of nodes drawing a particular element of map. Is cleared when fully drawn.
        List<PolyLine> rel = new ArrayList<>(); // Saves all relations.
        long relID = 0; // ID of the current relation.
        String suptype = null, subtype = null, name = null;

        // Reads the entire .OSM file.
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT: // Reads the first element of a line.
                    switch (reader.getLocalName()) {
                        case "bounds":// Configures the longitude and latitude. An element present in all OSM files. Uncertain as to why, though adjusting the floats will make the map not draw.
                            this.maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                            this.minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            this.minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                            this.maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            break;
                        case "node": // Parses information from a node, adding it to the id2node list.
                            long id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            float lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            float lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            id2node.add(new PolyPoint(id, 0.56f * lon, -lat));
                            builder = builder.lat(-lat);
                            builder = builder.lon(0.56f * lon);
                            this.nodecount++;
                            break;
                        case "nd": // parses reference to a node (ID) and adds it to the node list.
                            long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            nodes.add(id2node.get(ref));
                            break;
                        case "way":// Parses the ID of the way and sets a default type. For future reference, type could probably be configured properly in this step.
                            relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                            break;
                        case "tag": // Parses the key and value of tags, changing the waytype to the corresponding type.
                            String k = reader.getAttributeValue(null, "k");
                            String v = reader.getAttributeValue(null, "v");
                            if(k.equals("name")) name = v;
                            if (k.contains("addr:")) {
                                switch (k) {
                                    case "addr:city":
                                        builder = builder.city(v);
                                        break;
                                    case "addr:housenumber":
                                        builder = builder.house(v);
                                        break;
                                    case "addr:postcode":
                                        builder = builder.postcode(v);
                                        break;
                                    case "addr:street":
                                        builder = builder.street(v);
                                        break;
                                }
                            }
                            if(this.yamlObj.ways.containsKey(k))
                            {
                                suptype = k;
                                subtype = v;

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
                        case "member": // parses a member (a reference to a way belonging to a collection of ways; relations)
                            ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            PolyLine elm = id2way.get(ref);
                            if (elm != null) rel.add(elm);
                            break;
                        }
                        break;
                case XMLStreamConstants.END_ELEMENT: // Reads the last element, such as "way", "relation" etc.
                    switch (reader.getLocalName()) {
                        case "way": // "way" - All lines in the program; linking point A to B
                            PolyLine way = new PolyLine(nodes);
                            id2way.put(relID, way);
                            this.kdtree.add(way);
                            this.waycount++;
                            nodes.clear();

                            if(this.yamlObj.ways.containsKey(suptype) && this.yamlObj.ways.get(suptype).valuefeatures.containsKey(subtype)) {
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).drawable.add(way);
                            }

                            break;
                        case "relation": // is a collection of ways and has to be drawn separately with MultiPolygon.
                            MultiPolygon multipoly = new MultiPolygon(rel);
                            this.kdtree.add(multipoly);
                            this.relcount++;
                            rel.clear();

                            /*if(suptype != null && !rel.isEmpty() && this.yamlObj.ways.containsKey(suptype) && this.yamlObj.ways.get(suptype).valuefeatures.containsKey(subtype)) {
                                this.yamlObj.ways.get(suptype).valuefeatures.get(subtype).drawable.add(multipoly);
                            }*/

                            break;

                        case "node":
                            if (!builder.isEmpty()) {
                                addresses.add(builder.build());
                            }
                            break;
                    }
                    break;
            }
        }
        this.kdtree.generate();
        this.loadTime = System.nanoTime() - this.loadTime;
        Collections.sort(addresses);
        for (Address address : addresses) {
            //System.out.println(address.toString());
            searchTree.insertAddress(address.toString(), addresses.indexOf(address));
        }
        //System.out.println(searchTree.search("admiralgade 1, 1066 københavn") ? "Found" : "Not found");
        //System.out.println(searchTree.toString());
    }

    public void unloadOSM(){
        this.isOMSloaded = false;
        this.kdtree = new KdTree();
    }
}