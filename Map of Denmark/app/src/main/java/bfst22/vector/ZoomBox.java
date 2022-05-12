package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ZoomBox {
	private float[] zoomBoxStart;
	private boolean isActive;

	public ZoomBox() {
		this.zoomBoxStart = new float[]{0,0};
	}

	public void setState(boolean state){
		this.isActive = state;
	}

	public boolean isZooming() {
		return this.isActive;
	}

	public void press(float[] pos) {
		if (this.isActive) this.zoomBoxStart = pos;
	}

	public void drag(GraphicsContext gc, float[] pos, double zoom_current) {
		if (this.isActive) {
			gc.setLineWidth(1 / zoom_current);
			gc.setStroke(Color.ORANGE);
			gc.setLineDashes(0);

			gc.beginPath();
			gc.moveTo(this.zoomBoxStart[0], this.zoomBoxStart[1]);
			gc.lineTo(this.zoomBoxStart[0], pos[1]);
			gc.lineTo(pos[0], pos[1]);
			gc.lineTo(pos[0], this.zoomBoxStart[1]);
			gc.lineTo(this.zoomBoxStart[0], this.zoomBoxStart[1]);
			gc.stroke();
			gc.closePath();

			gc.fillOval((pos[0] + this.zoomBoxStart[0]) / 2, (pos[1] + this.zoomBoxStart[1]) / 2, 5 / zoom_current, 5 / zoom_current);
		}
	}

	public void release(MapCanvas canvas, float[] mousepos) {
		if (this.isActive) {
			canvas.zoomTo(2);
			float x = (mousepos[0] + this.zoomBoxStart[0])/2;
			float y = (mousepos[1] + this.zoomBoxStart[1])/2;
			canvas.goToPosAbsolute(new float[]{x,y});
		}
	}
}