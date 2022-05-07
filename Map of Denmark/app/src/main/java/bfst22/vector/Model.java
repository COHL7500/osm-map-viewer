package bfst22.vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;
import javax.xml.stream.*;
import javafx.geometry.Point2D;
import bfst22.vector.TernarySearchTree.TernarySearchTree;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

// Handles the logic of our data and storing it appropriately.
public class Model {

    // Declares and instantiates lines, containing all lines needed to be drawn.
    // Like HashMap, it has key (the enum waytype) and value (list of all lines w/ that waytype).
    public MapFeature yamlObj;
    public KdTree kdtree;
    public Point2D minBoundsPos, maxBoundsPos, originBoundsPos; // lat, lon
    public int nodecount, waycount, relcount;
    public String currFileName;
    public long loadTime, filesize;
	public VehicleType vehicleType;
    public Edge e;
    public ArrayList<Address> addresses = new ArrayList<>();
    public Address.Builder builder = new Address.Builder();
    public TernarySearchTree searchTree = new TernarySearchTree();


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

        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // Reads the .osm file, being an XML file.
        NodeMap id2node = new NodeMap(); // Converts IDs into nodes (uncertain about this).
        Map<Long, Drawable> id2way = new HashMap<>(); // Saves the ID of a particular way (Long) and stores the way as a value (OSMWay).
        List<PolyPoint> nodes = new ArrayList<>(); // A list of nodes drawing a particular element of map. Is cleared when fully drawn.
        List<Drawable> rel = new ArrayList<>(); // Saves all relations.
        long relID = 0; // ID of the current relation.
        String keyFeature = null, valueFeature = null;
        boolean isMultiPoly = false;

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
                        PolyPoint point = new PolyPoint(id, 0.56f * lon, -lat);

                        id2node.add(point);
                        id2way.put(relID,point);
                        this.nodecount++;
						//adds lat and lon to address builder
                        builder = builder.lat(-lat);
                        builder = builder.lon(0.56f * lon);
                    } case "nd" -> { // parses reference to a node (ID) and adds it to the node list.
                        long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                        nodes.add(id2node.get(ref));
                    } case "relation", "way" -> { // Parses the ID of the way and sets a default type. For future reference, type could probably be configured properly in this step.
                        relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                    } case "tag" -> { // Parses the key and value of tags, changing the waytype to the corresponding type.
                        String k = reader.getAttributeValue(null, "k");
                        String v = reader.getAttributeValue(null, "v");
                        isMultiPoly = (k.equals("type") && v.equals("multipolygon") || isMultiPoly);

						if (k.contains("addr:")) { // parses address tags and adds to address builder
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
							break;
						}
                        if (this.yamlObj.keyfeatures.containsKey(k)) {
                            keyFeature = k;
                            valueFeature = v;

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
                        Drawable elm = id2way.get(Long.parseLong(reader.getAttributeValue(null, "ref")));

                        if (elm != null){
                            switch (reader.getAttributeValue(null, "role")){
                                case "outer" -> rel.add(new PolyGon((PolyLine) elm,false));
                                case "inner" -> rel.add(new PolyGon((PolyLine) elm,true));
                                default -> rel.add(elm);
                            }
                        }
                    }
                }
            } else if(element == XMLStreamConstants.END_ELEMENT){
                switch (reader.getLocalName()) {
					case "node" -> { //adds finished address object to arraylist
						if (!builder.isEmpty()) {
							addresses.add(builder.build());
							builder.emptyBuilder();
						}
					} case "way" -> { // "way" - All lines in the program; linking point A to B
                        PolyLine way = new PolyLine(nodes);
                        this.waycount++;

                        id2way.put(relID, way);
                        this.kdtree.add(way,way);
                        this.yamlObj.addDrawable(keyFeature,valueFeature,way);
                        nodes.clear();
                    } case "relation" -> { // is a collection of ways and has to be drawn separately with MultiPolygon.
                        PolyRelation multipoly = new PolyRelation(rel,isMultiPoly);
                        this.kdtree.add(multipoly,multipoly);
                        this.relcount++;

                        id2way.put(relID, multipoly);
                        this.yamlObj.addDrawable(keyFeature,valueFeature,multipoly);
                        isMultiPoly = false;
                        rel.clear();
                    }
                }
            }
        }

        this.kdtree.generateTree();
        this.kdtree.generateSplits();
        this.loadTime = System.nanoTime() - this.loadTime;
		
        //sorts addresses and adds to ternary search tree
        Collections.sort(addresses);
        for (Address address : addresses) {
            searchTree.insertAddress(address.toString(), addresses.indexOf(address));
        }
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public TernarySearchTree getSearchTree() {
        return searchTree;
    }
}