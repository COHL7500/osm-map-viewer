package bfst22.vector;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Arrays;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Point2D lastMouse;

    @FXML
    private MapCanvas canvas;
    @FXML
    private MenuButton dropdown;
    @FXML
    private Slider slider;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model) {
        this.canvas.init(model);
    }

    // Handles an event of scrolling and increases/decreases the zoom level of the
    // map.
    @FXML
    private void onScroll(ScrollEvent e) {
        var factor = e.getDeltaY();

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

    // updates the variable lastMouse upon pressing (necessary for onMouseDragged)
    @FXML private void onMousePressed(final MouseEvent e) {
        this.lastMouse = new Point2D(e.getX(), e.getY());
    }
}