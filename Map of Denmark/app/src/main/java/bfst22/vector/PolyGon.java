package bfst22.vector;

import java.io.Serializable;

public class PolyGon extends PolyLine implements Drawable, Serializable, SerialVersionIdentifiable {
	boolean clockwise;

	public PolyGon(final PolyLine node, boolean clockwise){
		super(node.ids, node.coords);
		this.clockwise = clockwise;
	}
}