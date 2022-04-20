package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ZoomBox {
	private Point2D zoomBoxStart;
	private boolean active;

	public ZoomBox() {
		this.zoomBoxStart = new Point2D(0, 0);
	}

	public void setState(boolean state){
		this.active = state;
	}

	public boolean isZooming() {
		return this.active;
	}

	public void press(Point2D pos) {
		if (this.active) this.zoomBoxStart = pos;
	}

	public void drag(GraphicsContext gc, Point2D pos, double zoom_current) {
		if (this.active) {
			gc.setLineWidth(1 / zoom_current);
			gc.setStroke(Color.ORANGE);
			gc.setLineDashes(0);
			gc.beginPath();
			gc.moveTo(this.zoomBoxStart.getX(), this.zoomBoxStart.getY());
			gc.lineTo(this.zoomBoxStart.getX(), pos.getY());
			gc.lineTo(pos.getX(), pos.getY());
			gc.lineTo(pos.getX(), this.zoomBoxStart.getY());
			gc.lineTo(this.zoomBoxStart.getX(), this.zoomBoxStart.getY());
			gc.stroke();
			gc.closePath();
			gc.fillOval((pos.getX() + this.zoomBoxStart.getX()) / 2, (pos.getY() + this.zoomBoxStart.getY()) / 2, 5 / zoom_current, 5 / zoom_current);
		}
	}

	public void release(MapCanvas canvas, Point2D mousepos) {
		if (this.active) {
			canvas.zoomTo(2);

			double x = (mousepos.getX() + this.zoomBoxStart.getX()) / 2;
			double y = (mousepos.getY() + this.zoomBoxStart.getY()) / 2;
			canvas.goToPosAbsolute(new Point2D(x, y));
		}
	}
}