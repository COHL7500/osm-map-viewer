package bfst22.vector;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipInputStream;
import javax.xml.stream.*;
import javafx.geometry.Point2D;
import bfst22.vector.TernarySearchTree.TernarySearchTree;
import javafx.scene.paint.Color;
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
    public Graph graph;
    public DijkstraSP dijkstraSP;


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
                    this.graph = (Graph) input.readObject();
                    this.dijkstraSP = (DijkstraSP) input.readObject();
                }
                this.loadTime = System.nanoTime() - this.loadTime;
                this.filesize = Files.size(Paths.get(this.currFileName));
            }
        }
    }

    public void unload(){
        this.kdtree = null;
        this.yamlObj = null;
        this.graph = null;
        this.dijkstraSP = null;
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
            out.writeObject(graph);
            out.writeObject(dijkstraSP);
        }
    }

    // Parses and reads the loaded .osm file, interpreting the data however it is configured.
    private void loadOSM(InputStream input) throws XMLStreamException, FactoryConfigurationError, IOException {
        this.loadTime = System.nanoTime();
        this.filesize = Files.size(Paths.get(this.currFileName));
        this.yamlObj = new Yaml(new Constructor(MapFeature.class)).load(this.getClass().getResourceAsStream("WayConfig.yaml"));
        this.kdtree = new KdTree();
        this.graph = new Graph();


        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new BufferedInputStream(input)); // Reads the .osm file, being an XML file.
        NodeMap id2node = new NodeMap(); // Converts IDs into nodes (uncertain about this).
        Map<Long, PolyLine> id2way = new HashMap<>(); // Saves the ID of a particular way (Long) and stores the way as a value (OSMWay).
        List<PolyPoint> nodes = new ArrayList<>(); // A list of nodes drawing a particular element of map. Is cleared when fully drawn.
        List<PolyLine> rel = new ArrayList<>(); // Saves all relations.
        List<String> highwayTypes = new ArrayList<>(Arrays.asList("primary", "secondary", "tertiary", "residential"));
        Random rand = new Random();
        int HwyCount = 0;

        // , "secondary", "tertiary", "residential"

        long relID = 0; // ID of the current relation.
        String keyType = null, valueType = null, name = null;
        boolean isHighway = false;
        Map<Integer, List<PolyPoint>> index2way = new HashMap<>();

        // Reads the entire .OSM file.
        while (reader.hasNext()) {
            int element = reader.next();

            if (element == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "bounds" -> { // Configures the longitude and latitude. An element present in all OSM files. Uncertain as to why, though adjusting the floats will make the map not draw.
                        double maxlat = -Float.parseFloat(reader.getAttributeValue(null, "minlat"));
                        double minlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                        double minlat = -Float.parseFloat(reader.getAttributeValue(null, "maxlat"));
                        double maxlon = 0.56f * Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                        this.minBoundsPos = new Point2D(minlat, minlon);
                        this.maxBoundsPos = new Point2D(maxlat, maxlon);
                        this.originBoundsPos = new Point2D((maxlon + minlon) / 2, (maxlat + minlat) / 2);
                    }
                    case "node" -> { // Parses information from a node, adding it to the id2node list.
                        long id = Long.parseLong(reader.getAttributeValue(null, "id"));
                        float lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                        float lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                        id2node.add(new PolyPoint(id, 0.56f * lon, -lat));
                        this.nodecount++;
                        //adds lat and lon to address builder
                        builder = builder.lat(-lat);
                        builder = builder.lon(0.56f * lon);
                    }
                    case "nd" -> { // parses reference to a node (ID) and adds it to the node list.
                        long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                        nodes.add(id2node.get(ref));
                    }
                    case "way" -> { // Parses the ID of the way and sets a default type. For future reference, type could probably be configured properly in this step.
                        relID = Long.parseLong(reader.getAttributeValue(null, "id"));
                    }
                    case "tag" -> { // Parses the key and value of tags, changing the waytype to the corresponding type.
                        String k = reader.getAttributeValue(null, "k");
                        String v = reader.getAttributeValue(null, "v");
                        if (k.equals("name")) name = v;
                        // parses address tags and adds to address builder
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
                            break;
                        }
                        if (this.yamlObj.ways.containsKey(k)) {
                            keyType = k;
                            valueType = v;
                            isHighway = false;

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
                                    e.isOneway = v.equals("yes");
                                    break;
                                case "maxspeed":
                                    e.speedLimit = Integer.parseInt(v);
                                    break;
                                case "restriction":
                                    if (v.equals("no_left_turn")) e.leftTurn = false;
                                    else if (v.equals("no_right_turn")) e.rightTurn = false;
                                    break;
                                case "highway":
                                    if (highwayTypes.contains(v)) {
                                        isHighway = true;
                                        graph.add(nodes);
                                    }
                                    break;
                            }
                        }
                    }
                    case "member" -> { // parses a member (a reference to a way belonging to a collection of ways; relations)
                        long ref = Long.parseLong(reader.getAttributeValue(null, "ref"));
                        PolyLine elm = id2way.get(ref);
                        if (elm != null) rel.add(elm);
                    }
                }
            } else if (element == XMLStreamConstants.END_ELEMENT) {
                switch (reader.getLocalName()) {
                    //adds finished address object to arraylist
                    case "node" -> {
                        if (!builder.isEmpty()) {
                            addresses.add(builder.build());
                            builder.emptyBuilder();
                        }
                    }
                    case "way" -> { // "way" - All lines in the program; linking point A to B
                        PolyLine way = new PolyLine(nodes);
                        id2way.put(relID, way);
                        this.kdtree.add(way);
                        this.waycount++;
                        if (isHighway) {
                            index2way.put(HwyCount, new LinkedList<>());
                            for (PolyPoint p : nodes) index2way.get(HwyCount).add(p);
                            HwyCount++;
                        }

                        nodes.clear();

                        if (this.yamlObj.ways.containsKey(keyType) && this.yamlObj.ways.get(keyType).valuefeatures.containsKey(valueType)) {
                            this.yamlObj.ways.get(keyType).valuefeatures.get(valueType).drawable.add(way);
                        }

                    }
                    case "relation" -> { // is a collection of ways and has to be drawn separately with MultiPolygon.
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
        this.loadTime = System.nanoTime() - this.loadTime;

        //sorts addresses and adds to ternary search tree
        Collections.sort(addresses);
        for (Address address : addresses) {
            searchTree.insertAddress(address.toString(), addresses.indexOf(address));
        }

        graph.generate();
        for(int i = 0; i < index2way.size() - 1 ; i++){
            for(int j = 0; j < index2way.get(i).size() - 1; j++){
                graph.addEdge(index2way.get(i).get(j),index2way.get(i).get(j+1), graph.setWeightDistance(index2way.get(i).get(j),index2way.get(i).get(j+1),75));
                graph.addEdge(index2way.get(i).get(j+1),index2way.get(i).get(j), graph.setWeightDistance(index2way.get(i).get(j+1),index2way.get(i).get(j),75));
            }
        }

        PolyPoint from = graph.nodes.get(rand.nextInt(graph.nodes.size()-1));
        PolyPoint to = graph.nodes.get(rand.nextInt(graph.nodes.size()-1));
        this.dijkstraSP = new DijkstraSP(graph,from,to);

        System.out.println(dijkstraSP.pathToString(dijkstraSP.pathTo(to)));


    }


        


        /*
        for(int i = 0; i < (graph.nodes.size() - 1); i++) {
            graph.addEdge(graph.nodes.get(i), graph.nodes.get(i + 1), graph.setWeightDistance(graph.nodes.get(i), graph.nodes.get(i + 1), graph.speedlimit));
            //For now all roads go back and forth
            //graph.addEdge(graph.nodes.get(i+1),graph.nodes.get(i), graph.setWeight(graph.nodes.get(i+1),graph.nodes.get(i), graph.speedlimit));
        }

         */


        //graph.clearList();
    

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public TernarySearchTree getSearchTree() {
        return searchTree;
    }

}