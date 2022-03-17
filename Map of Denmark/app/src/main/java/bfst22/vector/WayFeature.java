package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WayFeature implements Serializable {
	public static final long serialVersionUID = 134123;
	public Map<String, features> ways = new HashMap<>();
}

class features implements Serializable {
	public static final long serialVersionUID = 134123;
	public WayDraw draw;
	public Map<String,SubFeature> subfeatures = new HashMap<>();
}

class WayDraw implements Serializable {
	public static final long serialVersionUID = 134123;
	public boolean fill;
	public String fill_color;
	public boolean stroke;
	public String stroke_color;
	public double line_width;
	public double dash_size;
	public int zoom_level;
}

class SubFeature implements Serializable {
	public static final long serialVersionUID = 134123;
	public WayDraw draw;
	public List<Drawable> drawable = new ArrayList<>();
	public String name;
}