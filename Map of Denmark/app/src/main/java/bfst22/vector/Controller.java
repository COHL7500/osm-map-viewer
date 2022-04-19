package bfst22.vector;

import javax.swing.text.Position;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Slider;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Point2D lastMouse;

    @FXML
    private MapCanvas canvas;
    @FXML
    private Button menuActivation;
    @FXML
    private VBox navigation, setting, appearance;
    @FXML
    private Pane somePane;
    @FXML
    private BorderPane someBorderPane;
    @FXML
    private Slider zoomSlider;

    double x = 0.0, y = 0.0, delta = 53.6;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model) {
        someBorderPane.setLeft(null);
        this.canvas.init(model);
    }

    // Handles an event of scrolling and increases/decreases the zoom level of the map.
    @FXML
    private void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();
        delta = factor;
        x = e.getX();
        y = e.getY();

        // Default canvas zoom: 1.10
        canvas.zoom(Math.pow(1.003, factor), e.getX(), e.getY()); // Change value to control sensitivity.
    }

    // Handles panning in the program
    @FXML private void onMouseDragged(final MouseEvent e) {
        double dx = e.getX() - lastMouse.getX();
        double dy = e.getY() - lastMouse.getY();
        this.canvas.pan(dx, dy);
        this.lastMouse = new Point2D(e.getX(), e.getY());
    }

    // Updates the variable lastMouse upon pressing (necessary for onMouseDragged)
    @FXML private void onMousePressed(final MouseEvent e) {
        this.lastMouse = new Point2D(e.getX(), e.getY());
    }
    
    // Side pane with navigation settings
    @FXML
    private void onNavButtonPress(ActionEvent e){  
        if (someBorderPane.getLeft() == null){
            someBorderPane.setLeft(navigation);
        } else if (someBorderPane.getLeft() == navigation){
            someBorderPane.setLeft(null);
        } else if (someBorderPane.getLeft() != null && someBorderPane.getLeft() != navigation) {
            someBorderPane.setLeft(navigation);
        }
    }

    // Side pane with settings
    @FXML
    private void onSettingButtonPress(ActionEvent e){
        if (someBorderPane.getLeft() == null){
            someBorderPane.setLeft(setting);
        } else if (someBorderPane.getLeft() == setting){
            someBorderPane.setLeft(null);
        } else if (someBorderPane.getLeft() != null && someBorderPane.getLeft() != setting) {
            someBorderPane.setLeft(setting);
        }
    }

    // Side pane with appearance settings
    @FXML
    private void onAppearanceButtonPress(ActionEvent e){
        if (someBorderPane.getLeft() == null){
            someBorderPane.setLeft(appearance);
        } else if (someBorderPane.getLeft() == appearance){
            someBorderPane.setLeft(null);
        } else if (someBorderPane.getLeft() != null && someBorderPane.getLeft() != appearance) {
            someBorderPane.setLeft(appearance);
        }
    }

    // Zooming in function
    @FXML
    private void zoomPlus(ActionEvent e){
        if (delta < 0) {
            delta = Math.abs(delta);
        }
        canvas.zoom(Math.pow(1.003, delta), canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    // Zooming out function
    @FXML
    private void zoomMinus(ActionEvent e){
        if (delta > 0) {
            delta = -delta;
        }
        canvas.zoom(Math.pow(1.003, delta), canvas.getWidth() / 2, canvas.getHeight() / 2);
    }
}
