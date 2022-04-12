package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Pin extends MoveableObj {
	public HBox listEntry;
	public String title, description;

	public Pin(HBox listEntry, final float lat, final float lon, final double radius, final boolean moveable, final String title, final String description){
		super(-1,lat,lon,radius,moveable);
		this.title = title;
		this.description = description;
		this.listEntry = listEntry;
	}

	public void setContent(final String title, final String description){
		this.title = title;
		this.description = description;
	}

	@Override public void draw(final GraphicsContext gc, final double zoom, final Point2D mousePos){
		gc.setFill(Color.BLACK);
		if(super.inRadius(mousePos,zoom)) gc.setFill(Color.YELLOW);
		gc.fillRect(super.lat-15/zoom,super.lon-15/zoom,30/zoom,30/zoom);
		//gc.fillArc(super.lat-10/zoom,super.lon-10/zoom,20/zoom,10/zoom,0,90, ArcType.OPEN);
	}
}
