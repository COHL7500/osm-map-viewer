package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.Arrays;

// Responsible for controlling/updating the current view and manipulating dataflow of model.

public class Controller {

    private Point2D lastMouse;

    @FXML private MapCanvas canvas;
    @FXML private MenuButton dropdown;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(Model model) {
        Arrays.stream(WayType.values()).forEach(way -> {
            CheckMenuItem checkbox = new CheckMenuItem(way.toString().toLowerCase());
            dropdown.getItems().add(checkbox);
            checkbox.setSelected(true);
            checkbox.setOnAction(e -> {
                WayType way1 = WayType.valueOf(((CheckMenuItem) e.getSource()).getText().toUpperCase());
                boolean checked = ((CheckMenuItem) e.getSource()).isSelected();
                canvas.setDisplayStatus(way1, checked);
                canvas.repaint();
            });
        });
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
