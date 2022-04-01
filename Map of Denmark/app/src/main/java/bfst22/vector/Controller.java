package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Stage stage;
    private Point2D lastMouse;
    private Model model;
    @FXML private MapCanvas canvas;
    @FXML private MenuItem unloadFileButton;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model, final Stage stage) {
        this.model = model;
        this.stage = stage;
    }

    // Handles an event of scrolling and increases/decreases the zoom level of the map.
    // Default canvas zoom: 1.10
    @FXML private void onScroll(final ScrollEvent e) {
        this.canvas.zoom(Math.pow(1.003, e.getDeltaY()), e.getX(), e.getY()); // Change value to control sensitivity.
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

    // updates the mouse position on the screen upon moving
    @FXML private void onMouseMoved(final MouseEvent e){
        this.canvas.setMousePos(new Point2D(e.getX(), e.getY()));
    }

    @FXML private void onBrowseOSMClicked(final ActionEvent e) throws IOException, XMLStreamException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose OSM File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("OSM File", "*.osm"));

        File file = fileChooser.showOpenDialog(this.stage);

        if(file != null) {
            this.model.loadOSM(new FileInputStream(file.getAbsolutePath()));
            this.canvas.init(model);
            this.canvas.setDisable(false);
            this.unloadFileButton.setDisable(false);
        }
    }

    @FXML private void unloadFileButtonClicked(final ActionEvent e){
        this.canvas.clearScreen();
        //this.canvas.setDisable(true);
        //this.canvas = new MapCanvas();
        //this.unloadFileButton.setDisable(true);
    }

    @FXML private void exitButtonClicked(final ActionEvent e){
        System.exit(0);
    }

    @FXML private void debugCursorClicked(final ActionEvent e){
        this.canvas.debugCursor = !this.canvas.debugCursor;
    }

    @FXML private void debugVisBoxClicked(final ActionEvent e){
        this.canvas.debugVisBox = !this.canvas.debugVisBox;
    }

    @FXML private void debugHelpTextClicked(final ActionEvent e){
        this.canvas.debugDisableHelpText = !this.canvas.debugDisableHelpText;
    }
}