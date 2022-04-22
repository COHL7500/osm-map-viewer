package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class Painter {
	private int drawMode;
	private boolean fill;
	private Color paintColor;
	private Point2D mousePos;
	private double strokeSize;
	private String selectedFont;
	private double zoom_current;
	private int fontSize;
	private final List<PaintObj> elements;
	private final List<PolyPoint> tempElements;

	public Painter(){
		this.fill = false;
		this.drawMode = -1;
		this.strokeSize = 10e-6;
		this.fontSize = 12;
		this.paintColor = Color.BLACK;
		this.selectedFont = "Arial";
		this.zoom_current = 1;
		this.elements = new ArrayList<>();
		this.tempElements = new ArrayList<>();
	}

	public boolean isDrawing(){
		return (this.drawMode != -1);
	}

	public void setDrawMode(int mode){
		this.drawMode = mode;
	}

	public void setColour(Color color){
		this.paintColor = color;
	}

	public void setStroke(double size){
		this.strokeSize = size*10e-6;
	}

	public void setFont(String font){
		this.selectedFont = font;
	}

	public void setFontSize(int size){
		System.out.println("Font size change: " + size);
		this.fontSize = size;
	}

	public void toggleFill(){
		this.fill = !this.fill;
	}

	public void keyPress(String key){
		if(this.drawMode == 3) ((PolyText) this.elements.get(this.elements.size()-1)).addText(key);
	}

	public void press(Point2D mousePos){
		this.mousePos = mousePos;
		switch(this.drawMode){
			case 0 -> this.tempElements.add(new PolyPoint(0,(float) this.mousePos.getX(),(float) this.mousePos.getY()));
			case 1 -> this.elements.add(new PolyCircle(mousePos,new Point2D(0,0),this.paintColor,this.strokeSize,this.fill));
			case 2 -> this.elements.add(new PolyRect(mousePos,new Point2D(0,0),this.paintColor,this.strokeSize,this.fill));
			case 3 -> this.elements.add(new PolyText(mousePos,this.paintColor,this.selectedFont,this.fontSize,this.strokeSize,this.fill));
		}
	}

	public void drag(Point2D mousePos){
		this.mousePos = mousePos;
		switch (this.drawMode){
			case 0 -> this.tempElements.add(new PolyPoint(0,(float) this.mousePos.getX(),(float) this.mousePos.getY()));
			case 1 -> ((PolyCircle) this.elements.get(this.elements.size()-1)).setEnd(mousePos);
			case 2 -> ((PolyRect) this.elements.get(this.elements.size()-1)).setEnd(mousePos);
			case 4 -> this.deleteWithin();
		}
	}

	public void release(){
		if(this.drawMode == 0) this.elements.add(new PolyPaint(this.paintColor,tempElements,this.strokeSize,this.fill));
		this.tempElements.clear();
	}

	public void stroke(GraphicsContext gc, Point2D mousePos, double zoom_current){
		this.zoom_current = zoom_current;
		gc.setLineDashes(0);

		if(!this.tempElements.isEmpty()){
			gc.setLineWidth(this.strokeSize);
			gc.setStroke(this.paintColor);
			gc.setFill(this.paintColor);

			gc.beginPath();
			gc.moveTo(this.tempElements.get(0).lat,this.tempElements.get(0).lon);

			for(PolyPoint paint : this.tempElements) gc.lineTo(paint.lat,paint.lon);

			if(this.fill) gc.fill(); else gc.stroke();
			gc.closePath();
		}

		if(this.drawMode == 4){
			gc.setStroke(Color.RED);
			gc.setLineDashes(5/zoom_current);
			gc.setLineWidth(2/zoom_current);
			gc.strokeOval(mousePos.getX()-(50/zoom_current),mousePos.getY()-(50/zoom_current),100/zoom_current,100/zoom_current);
			gc.setLineDashes(0);
		}

		for(PaintObj paint : this.elements) paint.draw(gc);
	}

	private void deleteWithin(){
		this.elements.removeIf(obj -> {
			double r = 50/zoom_current;
			double x = mousePos.getX()-obj.getPos().getX();
			double y = mousePos.getY()-obj.getPos().getY();
			double d = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
			return d <= r;
		});
	}

	private interface PaintObj {
		Point2D getPos();
		void draw(GraphicsContext gc);
	}

	private static class PolyText extends Text implements PaintObj {
		private final Color colour;
		private final double strokeSize;
		private final boolean filled;
		private final String font;
		private final int size;

		public PolyText(Point2D pos, Color colour, String font, int size, double strokeSize, boolean filled){
			this.setX(pos.getX());
			this.setY(pos.getY());
			this.colour = colour;
			this.strokeSize = strokeSize;
			this.filled = filled;
			this.font = font;
			this.size = size;
		}

		public void addText(String chars){
			super.setText(super.getText() + chars);
		}

		public Point2D getPos(){
			return new Point2D(super.getX(),super.getY());
		}

		@Override public void draw(GraphicsContext gc){
			gc.setFont(new Font(this.font,this.size*10e-5));
			gc.setLineWidth(this.strokeSize);
			gc.setStroke(this.colour);
			gc.setFill(this.colour);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.setTextBaseline(VPos.CENTER);
			if(this.filled) gc.fillText(super.getText(), super.getX(), super.getY());
			else gc.strokeText(super.getText(), super.getX(), super.getY());
			gc.setTextAlign(TextAlignment.LEFT);
			gc.setTextBaseline(VPos.BASELINE);
		}
	}

	private static class PolyPaint extends PolyLine implements PaintObj {
		private final Color colour;
		private final double strokeSize;
		private final boolean filled;
		private final Point2D pos;

		public PolyPaint(final Color colour, final List<PolyPoint> nodes, double strokeSize, boolean filled){
			super(nodes);
			this.colour = colour;
			this.strokeSize = strokeSize;
			this.filled = filled;
			this.pos = this.calcPos();
		}

		private Point2D calcPos(){
			double midtx = 0, midty = 0;
			for(int i = 0; i < super.coords.length; i+=3){
				midtx += super.coords[i+1];
				midty += super.coords[i+2];
			}
			return new Point2D(midtx/(super.coords.length/3),midty/(super.coords.length/3));
		}

		public Point2D getPos(){
			return this.pos;
		}

		@Override public void draw(GraphicsContext gc){
			gc.setLineWidth(this.strokeSize);
			gc.setStroke(this.colour);
			gc.setFill(this.colour);
			if(this.filled) super.fill(gc);
			else super.draw(gc);
		}
	}

	private abstract static class PolyBox implements PaintObj {
		private Point2D pos, curr, size;
		private final Color colour;
		private final boolean filled;
		private final double strokeSize;

		public PolyBox(Point2D pos, Point2D size, Color colour, double strokeSize, boolean filled){
			this.pos = pos;
			this.curr = new Point2D(0,0);
			this.size = size;
			this.filled = filled;
			this.colour = colour;
			this.strokeSize = strokeSize;
		}

		public Point2D getPos(){
			return new Point2D(this.curr.getX()+this.size.getX()/2, this.curr.getY()+this.size.getY()/2);
		}

		public void setEnd(Point2D pos){
			this.curr = new Point2D(Math.min(this.pos.getX(),pos.getX()), Math.min(this.pos.getY(),pos.getY()));
			this.size = new Point2D(Math.abs(this.pos.getX()-pos.getX()), Math.abs(this.pos.getY()-pos.getY()));
		}
	}

	private static class PolyCircle extends PolyBox {
		public PolyCircle(Point2D pos, Point2D size, Color colour, double strokeSize, boolean filled){
			super(pos,size,colour,strokeSize,filled);
		}

		@Override public void draw(GraphicsContext gc){
			gc.setLineWidth(super.strokeSize);
			gc.setStroke(super.colour);
			gc.setFill(super.colour);
			if(super.filled) gc.fillOval(super.curr.getX(), super.curr.getY(), super.size.getX(), super.size.getY());
			else gc.strokeOval(super.curr.getX(), super.curr.getY(), super.size.getX(), super.size.getY());
		}
	}

	private static class PolyRect extends PolyBox {
		public PolyRect(Point2D pos, Point2D size, Color colour, double strokeSize, boolean filled){
			super(pos,size,colour,strokeSize,filled);
		}

		@Override public void draw(GraphicsContext gc){
			gc.setLineWidth(super.strokeSize);
			gc.setStroke(super.colour);
			gc.setFill(super.colour);
			if(super.filled) gc.fillRect(super.curr.getX(), super.curr.getY(), super.size.getX(), super.size.getY());
			else gc.strokeRect(super.curr.getX(), super.curr.getY(), super.size.getX(), super.size.getY());
		}
	}
}
