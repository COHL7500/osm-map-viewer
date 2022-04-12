package bfst22.vector;

import javafx.geometry.Point2D;
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

	public void newWindow(final Point2D mousePos){
		this.displayWindow("Add Pin Point",null,mousePos);
	}

	private void displayWindow(final String header, final Pin point, final Point2D mousePos){
		Dialog<ButtonType> dialog = new Dialog<>();

		TextField title = new TextField(point != null ? point.title : "");
		TextArea desc = new TextArea(point != null ? point.description : "");
		ButtonType button1 = new ButtonType("OK", ButtonBar.ButtonData.YES);
		ButtonType button2 = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType button3 = new ButtonType("Delete", ButtonBar.ButtonData.NO);
		title.setPromptText("Title");
		desc.setPromptText("Description");

		dialog.getDialogPane().setContent(new VBox(title,desc));
		dialog.getDialogPane().setPrefSize(135,300);
		dialog.getDialogPane().getButtonTypes().addAll(button1,button2,button3);
		dialog.getDialogPane().lookupButton(button3).setDisable(point == null);
		dialog.setTitle(header);

		dialog.showAndWait().ifPresent(response -> {
			switch (response.getButtonData()){
				case YES -> { // OK
					if(point == null){
						HBox pinEntry = new HBox();
						pinEntry.getChildren().add(new Label(title.getText()));
						pinEntry.getChildren().add(new Pane());
						pinEntry.getChildren().add(new Button("Edit"));

						Pin newPoint = new Pin(pinEntry, (float) mousePos.getX(), (float) mousePos.getY(), 15, true, title.getText(), desc.getText());
						this.pins.add(newPoint);

						HBox.setHgrow(pinEntry.getChildren().get(1), Priority.ALWAYS);
						pinEntry.getChildren().get(2).setOnMousePressed(f -> this.displayWindow("Edit Pin Point", newPoint, mousePos));
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

	public void doubleClick(Point2D mousePos, double zoom){
		for(Pin point : this.pins){
			if(point.inRadius(mousePos,zoom)){
				this.displayWindow("Edit Pin Point",point,mousePos);
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
