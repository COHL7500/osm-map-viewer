package bfst22.vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.xml.stream.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

// Handles the logic of our data and storing it appropriately.
public class Model {

    // Declares and instantiates lines, containing all lines needed to be drawn.
    // Like HashMap, it has key (the enum waytype) and value (list of all lines w/ that waytype).
    public MapFeature yamlObj;
    public KdTree kdtree;
    public TernarySearchTree searchTree;
    public Point2D minBoundsPos, maxBoundsPos, originBoundsPos; // lat, lon
    public int nodecount, waycount, relcount;
    public String currFileName;
    public long loadTime, filesize;
	public VehicleType vehicleType;
    public Edge e;

    // Loads our OSM file, supporting various formats: .zip and .osm, then convert it into an .obj.
    public void load(String filename) throws IOException, XMLStreamException, FactoryConfigurationError, ClassNotFoundException {
        this.currFileName = filename;

        switch (filename.substring(filename.lastIndexOf('.') + 1)) {
            case "zip" -> {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(filename));
                zip.getNextEntry();
                this.loadOSM(zip);
                this.saveObj(filename);
            } case "osm" -> {
                this.loadOSM(new FileInputStream(filename));
                this.saveObj(filename);
            } case "obj" -> {
                this.loadTime = System.nanoTime();
                try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
                    this.minBoundsPos = new Point2D(input.readDouble(), input.readDouble());
                    this.maxBoundsPos = new Point2D(input.readDouble(), input.readDouble());
                    this.originBoundsPos = new Point2D(input.readDouble(), input.readDouble());
                    this.nodecount = input.readInt();
                    this.waycount = input.readInt();
                    this.relcount = input.readInt();
                    this.kdtree = (KdTree) input.readObject();
                    this.yamlObj = (MapFeature) input.readObject();
                }
                this.loadTime = System.nanoTime() - this.loadTime;
                this.filesize = Files.size(Paths.get(this.currFileName));
            }
        }
    }

    public void unload(){
        this.kdtree = null;
        this.yamlObj = null;
        this.minBoundsPos = this.maxBoundsPos = this.originBoundsPos = new Point2D(0,0);
        this.nodecount = this.waycount = this.relcount = 0;
        this.currFileName = "";
        this.loadTime = this.filesize = 0;
    }

    public boolean isLoaded(){
        return this.kdtree != null;
    }

    // Saves the .obj file in our project hierachy.
    private void saveObj(String basename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(basename + ".obj"))) {
            out.writeDouble(this.minBoundsPos.getX());
            out.writeDouble(this.minBoundsPos.getY());
            out.writeDouble(this.maxBoundsPos.getX());
            out.writeDouble(this.maxBoundsPos.getY());
            out.writeDouble(this.originBoundsPos.getX());
            out.writeDouble(this.originBoundsPos.getY());
            out.writeInt(nodecount);
            out.writeInt(waycount);
            out.writeInt(relcount);
            out.writeObject(kdtree);
            out.writeObject(yamlObj);
        }
    }

    // Parses and reads the loaded .osm file, interpreting the data however it is configured.
    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError, IOException {
        this.loadTime = System.nanoTime();
        this.filesize = Files.size(Paths.get(this.currFileName));
        this.yamlObj = new Yaml(new Constructor(MapFeature.class)).load(this.getClass().getResourceAsStream("WayConfig.yaml"));
        this.kdtree = new KdTree();
        this.searchTree = new TernarySearchTree();

        yamlObj.ways.forEach((key, value) -> value.valuefeatures.forEach((keyVF, valueVF) -> {

            if (valueVF != null) {

                if (key.equals("highway"))
                {
                    valueVF.draw.stroke_color = Color.web("#343742").toString();

                }
                else if (key.equals("building"))
                {
                    value.draw.stroke_color = Color.web("#586a8a").toString();
                    value.draw.fill_color = Color.web("#586a8a").toString();

                }
                else if (keyVF.equals("water"))
                {
                    valueVF.draw.stroke_color = Color.web("#31428c").toString();
                    valueVF.draw.fill_color = Color.web("#31428c").toString();
                }
                else
                {
                    valueVF.draw.stroke_color = Color.web("#3f4a5c").toString(); //Color.web(value.draw.stroke_color).darker().toString();
                    valueVF.draw.fill_color = Color.web("#3f4a5c").toString();
                }
            }
        }));

        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // Reads the .osm file, being an XML file.
        NodeMap id2node = new NodeMap(); // Converts IDs into nodes (uncertain about this).
        Map<Long, PolyLine> id2way = new HashMap<>(); // Saves the ID of a particular way (Long) and stores the way as a value (OSMWay).
        List<PolyPoint> nodes = new ArrayList<>(); // A list of nodes drawing a particular element of map. Is cleared when fully drawn.
        List<PolyLine> rel = new ArrayList<>(); // Saves all relations.
        long relID = 0; // ID of the current relation.
        String keyType = null, valueType = null, name = null;

        // Reads the entire .OSM file.
        while (reader.hasNext()) {
            int element = reader.next();

            if(element == XMLStreamConstants.START_ELEMENT){
                switch (reader.getLocalName()) {
                    case "bounds" -> { // Configures the longitude and latitude. An element present in all OSM files. Uncertain as to why, though adjusting the floats will make the map not draw.
                        double maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                        double minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                        double minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                        double maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                        this.minBoundsPos = new Point2D(minlat, minlon);
                        this.maxBoundsPos = new Point2D(maxlat, maxlon);
                        this.originBoundsPos = new Point2D((maxlon + minlon) / 2, (maxlat + minlat) / 2);
                    } case "node" -> { // Parses information from a node, adding it to the id2node list.
                        long id = Long.parseLong(reader.getAttributeValue(null, "id"));
                        float lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                        float lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                        id2node.add(new PolyPoint(id, 0.56f * lon, -lat));
                        this.searchTree.setAddressPos(0.56f * lon, -lat);
                        this.nodecount++;
                    } case "nd" -> { // parses reference to a node (ID) and adds it to the node list.
                        long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                        nodes.add(id2node.get(ref));
                    } case "way" -> { // Parses the ID of the way and sets a default type. For future reference, type could probably be configured properly in this step.
                        relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                    } case "tag" -> { // Parses the key and value of tags, changing the waytype to the corresponding type.
                        String k = reader.getAttributeValue(null, "k");
                        String v = reader.getAttributeValue(null, "v");
                        if (k.equals("name")) name = v;
						if (k.contains("addr:")) {
                            switch (k) {
                                case "addr:city" -> searchTree.addAddressElement("city",v);
                                case "addr:housenumber" -> searchTree.addAddressElement("house",v);
                                case "addr:postcode" -> searchTree.addAddressElement("postcode",v);
                                case "addr:street" -> searchTree.addAddressElement("street",v);
                            }
                        }
                        if (this.yamlObj.ways.containsKey(k)) {
                            keyType = k;
                            valueType = v;

                            switch (k) {
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
                                    if (v.equals("yes")) e.isOneway = true;
                                    break;
                                case "maxspeed":
                                    e.speedLimit = Integer.parseInt(v);
                                    break;
                                case "restriction":
                                    if (v.equals("no_left_turn")) e.leftTurn = false;
                                    else if (v.equals("no_right_turn")) e.rightTurn = false;
                                    break;
                            }
                        }
                    } case "member" -> { // parses a member (a reference to a way belonging to a collection of ways; relations)
                        long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                        PolyLine elm = id2way.get(ref);
                        if (elm != null) rel.add(elm);
                    }
                }
            } else if(element == XMLStreamConstants.END_ELEMENT){
                switch (reader.getLocalName()) {
					case "node" -> searchTree.insertAddress();
					case "way" -> { // "way" - All lines in the program; linking point A to B
                        PolyLine way = new PolyLine(nodes);
                        id2way.put(relID, way);
                        this.kdtree.add(way);
                        this.waycount++;
                        nodes.clear();
                        if (this.yamlObj.ways.containsKey(keyType) && this.yamlObj.ways.get(keyType).valuefeatures.containsKey(valueType)) {
                            this.yamlObj.ways.get(keyType).valuefeatures.get(valueType).drawable.add(way);
                        }
                    } case "relation" -> { // is a collection of ways and has to be drawn separately with MultiPolygon.
                        MultiPolygon multipoly = new MultiPolygon(rel);
                        this.kdtree.add(multipoly);
                        this.relcount++;
                        if (keyType != null && !rel.isEmpty() && this.yamlObj.ways.containsKey(keyType) && this.yamlObj.ways.get(keyType).valuefeatures.containsKey(valueType)) {
                            List<Drawable> yamlList = this.yamlObj.ways.get(keyType).valuefeatures.get(valueType).drawable;
                            yamlList.add(multipoly);
                        }
                        rel.clear();
                    }
                }
            }
        }

        this.kdtree.generate();
        this.searchTree.generate();
        this.loadTime = System.nanoTime() - this.loadTime;
    }
}