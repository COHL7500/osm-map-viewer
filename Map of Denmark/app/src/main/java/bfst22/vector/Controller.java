package bfst22.vector;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

// Responsible for controlling/updating the current view and manipulating dataflow of model.

public class Controller {

    private Point2D lastMouse;

    @FXML
    private MapCanvas canvas;


    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(Model model) {
        canvas.init(model);
    }

    // Handles an event of scrolling and increases/decreases the zoom level of the map.
    @FXML
    private void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();

        // Default canvas zoom: 1.10
        canvas.zoom(Math.pow(1.003, factor), e.getX(), e.getY()); // Change value to control sensitivity.
    }

    // Handles panning in the program
    @FXML
    private void onMouseDragged(MouseEvent e) {
        var dx = e.getX() - lastMouse.getX();
        var dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        lastMouse = new Point2D(e.getX(), e.getY());
    }

    // updates the variable lastMouse upon pressing (necessary for onMouseDragged)
    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
    }
}
