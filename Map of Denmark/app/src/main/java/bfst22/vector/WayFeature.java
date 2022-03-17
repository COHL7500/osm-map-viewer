package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WayFeature implements Serializable, SerialVersionIdentifiable {
	public Map<String, features> ways = new HashMap<>();
}

class features implements Serializable, SerialVersionIdentifiable {
	public WayDraw draw;
	public Map<String,SubFeature> subfeatures = new HashMap<>();
}

class WayDraw implements Serializable, SerialVersionIdentifiable {
	public boolean fill;
	public String fill_color;
	public boolean stroke;
	public String stroke_color;
	public double line_width;
	public double dash_size;
	public int zoom_level;
}

class SubFeature implements Serializable, SerialVersionIdentifiable {
	public WayDraw draw;
	public List<Drawable> drawable = new ArrayList<>();
	public String name;
}