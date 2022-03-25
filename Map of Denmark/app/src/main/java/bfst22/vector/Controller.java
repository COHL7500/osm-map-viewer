package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Point2D lastMouse;

    @FXML
    private MapCanvas canvas;
    @FXML
    private Button menuActivation;
    @FXML
    private VBox vBox;
    @FXML
    private Pane somePane;
    @FXML
    private BorderPane someBorderPane;

    private boolean leftPaneVisibility = false;;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model) {
        someBorderPane.getLeft().setVisible(leftPaneVisibility);
        someBorderPane.setLeft(null);
        this.canvas.init(model);
    }

    // Handles an event of scrolling and increases/decreases the zoom level of the map.
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

    // Updates the variable lastMouse upon pressing (necessary for onMouseDragged)
    @FXML private void onMousePressed(final MouseEvent e) {
        this.lastMouse = new Point2D(e.getX(), e.getY());
    }
    
    // Switch between disable and enable for leftPane wether it is visible or not
    @FXML
    private void onMenuButtonPress(ActionEvent e){  
        if (leftPaneVisibility == false){
            leftPaneVisibility = true;
            someBorderPane.setLeft(vBox);
            someBorderPane.getLeft().setVisible(leftPaneVisibility);
        } else if (leftPaneVisibility == true){
            leftPaneVisibility = false;
            someBorderPane.getLeft().setVisible(leftPaneVisibility);
            someBorderPane.setLeft(null);
        }
    }
}
