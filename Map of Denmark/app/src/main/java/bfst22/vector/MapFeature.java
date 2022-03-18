package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Wraps around the YAML file strcture and contains all its data inside its map for future use
public class MapFeature implements Serializable, SerialVersionIdentifiable {
	public Map<String, keyFeature> ways = new HashMap<>();
}

// Contains all for one of the many generalised key features inside the map i.e water, natural, buildings etc.
// Draw is used for styling for features that are not styled on their own, which overrides the default styling
class keyFeature implements Serializable, SerialVersionIdentifiable {
	public featureDraw draw;
	public Map<String, valueFeature> valuefeatures = new HashMap<>();
}

// Contains all for one of many specialised value features inside the map i.e. lake, vineyard, bicycle routes etc.
// Drawis used for styling for this specific feature, which overrides the default and key feature styling
class valueFeature implements Serializable, SerialVersionIdentifiable {
	public featureDraw draw;
	public List<Drawable> drawable = new ArrayList<>();
	public String name;
	public double[] nameCenter;
	public boolean display = true;
}

// All the different options for a feature to be drawn
class featureDraw implements Serializable, SerialVersionIdentifiable {
	public boolean fill;
	public String fill_color;
	public boolean stroke;
	public String stroke_color;
	public double line_width;
	public double dash_size;
	public int zoom_level;
}