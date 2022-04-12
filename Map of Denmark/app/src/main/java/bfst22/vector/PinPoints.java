package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class PinPoints {
	private List<Pin> pins;
	private ListView<HBox> pinPointList;

	public PinPoints(){
		this.pins = new ArrayList<>();
	}

	public void init(final ListView<HBox> pinPointList){
		this.pinPointList = pinPointList;
	}

	public void newWindow(final MapCanvas canvas){
		this.displayWindow("Add Pin Point",null,canvas);
	}

	private void displayWindow(final String header, final Pin point, final MapCanvas canvas){
		Dialog<ButtonType> dialog = new Dialog<>();

		Label labelTitle = new Label("Title:");
		Label labelDesc = new Label("Description:");
		TextField title = new TextField(point != null ? point.title : "");
		TextArea desc = new TextArea(point != null ? point.description : "");
		ButtonType button1 = new ButtonType("OK", ButtonBar.ButtonData.YES);
		ButtonType button2 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType button3 = new ButtonType("Delete", ButtonBar.ButtonData.NO);
		VBox vboxDialog = new VBox(labelTitle,title,labelDesc,desc);
		vboxDialog.setSpacing(5);

		dialog.getDialogPane().setContent(vboxDialog);
		dialog.getDialogPane().setPrefSize(100,250);
		dialog.getDialogPane().getButtonTypes().addAll(button1,button2,button3);
		dialog.getDialogPane().lookupButton(button3).setDisable(point == null);
		dialog.setTitle(header);

		dialog.showAndWait().ifPresent(response -> {
			switch (response.getButtonData()){
				case YES -> { // OK
					if(point == null){
						HBox pinEntry = new HBox();
						Label labelEntry = new Label(title.getText().length() > 20 ? title.getText().substring(0,20) + "..." : title.getText());
						Pane paneEntry = new Pane();
						Button buttonEntry1 = new Button("Goto");
						Button buttonEntry2 = new Button("Edit");
						pinEntry.getChildren().addAll(labelEntry,paneEntry,buttonEntry1,buttonEntry2);
						pinEntry.setSpacing(5);
						pinEntry.setAlignment(Pos.CENTER_LEFT);
						HBox.setHgrow(paneEntry, Priority.ALWAYS);

						Pin newPoint = new Pin(pinEntry, (float) canvas.mousePos.getX(), (float) canvas.mousePos.getY(), 15, true, title.getText(), desc.getText());
						this.pins.add(newPoint);

						buttonEntry1.setOnMousePressed(f -> canvas.goToPosAbsolute(new Point2D(newPoint.lat,newPoint.lon)));
						buttonEntry2.setOnMousePressed(g -> this.displayWindow("Edit Pin Point", newPoint, canvas));
						this.pinPointList.getItems().add(pinEntry);

					} else {
						((Label) point.listEntry.getChildren().get(0)).setText(title.getText());
						this.pins.get(this.pins.indexOf(point)).setContent(title.getText(),desc.getText());
					}
				} case NO -> { // DELETE
					this.pinPointList.getItems().remove(point.listEntry);
					this.pins.remove(point);
				}
			}
		});
	}

	public void doubleClick(MapCanvas canvas){
		for(Pin point : this.pins){
			if(point.inRadius(canvas.mousePos,canvas.zoom_current)){
				this.displayWindow("Edit Pin Point",point,canvas);
				return;
			}
		}
	}

	public boolean drag(Point2D mousePos, double zoom, boolean state){
		for(MoveableObj obj : this.pins){
			if(obj.inRadius(mousePos,zoom)){
				obj.move(mousePos,zoom,state);
				return true;
			}
		}
		return false;
	}

	public void draw(GraphicsContext gc, double zoom, Point2D mousePos){
		for(Pin point : this.pins) point.draw(gc,zoom,mousePos);
	}
}
