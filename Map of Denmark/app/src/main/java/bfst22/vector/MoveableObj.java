package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class MoveableObj extends PolyPoint {
	private final double radius;
	private boolean moveable;
	protected boolean inRadius, isDragging;

	public MoveableObj(final long id, final float lat, final float lon, final double radius, final boolean moveable){
		super(id,lat,lon);
		this.radius = radius;
		this.moveable = moveable;
		this.inRadius = false;
		this.isDragging = false;
	}

	public boolean inRadius(final Point2D mousePos, final double zoom){
		if(!this.isDragging) this.inRadius = Math.sqrt(Math.pow(mousePos.getX() - this.lat,2) + Math.pow(mousePos.getY() - this.lon,2)) < (this.radius/zoom);
		return this.inRadius;
	}

	public boolean isMovable(){
		return this.moveable;
	}

	public void setMovableState(boolean state){
		this.moveable = state;
	}

	public void move(final Point2D newPos, final double zoom, final boolean state){
		this.isDragging = state;
		if(this.inRadius && this.moveable && state){
			super.lat = (float) newPos.getX();
			super.lon = (float) newPos.getY();
		}
	}

	public abstract void draw(final GraphicsContext gc, final double zoom, final Point2D mousePos);
}