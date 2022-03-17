package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFeature implements Serializable, SerialVersionIdentifiable {
	public Map<String, keyFeature> ways = new HashMap<>();
}

class keyFeature implements Serializable, SerialVersionIdentifiable {
	public featureDraw draw;
	public Map<String, valueFeature> valuefeatures = new HashMap<>();
}

class featureDraw implements Serializable, SerialVersionIdentifiable {
	public boolean fill;
	public String fill_color;
	public boolean stroke;
	public String stroke_color;
	public double line_width;
	public double dash_size;
	public int zoom_level;
}

class valueFeature implements Serializable, SerialVersionIdentifiable {
	public featureDraw draw;
	public List<Drawable> drawable = new ArrayList<>();
	public String name;
}