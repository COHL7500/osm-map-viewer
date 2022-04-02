package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Stage stage;
    private Point2D lastMouse;
    private Model model;

    @FXML private MapCanvas canvas;
    @FXML private MenuItem unloadFileButton;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model, final Stage primarystage) {
        this.model = model;
        this.stage = primarystage;
        this.canvas.init(model);
    }

    /* ---------- Mouse Methods ---------- */
    // handles an event of scrolling and increases/decreases the zoom level of the map.
    @FXML private void onScroll(final ScrollEvent e) {
        this.canvas.zoom(Math.pow(1.003, e.getDeltaY()), e.getX(), e.getY()); // Change value to control sensitivity.
        this.canvas.pan(1,1);
    }

    // handles panning in the program
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

    /* ---------- GUI Methods ---------- */
    // when the menubar 'File' section button 'Load Default map' is clicked
    @FXML private void onDefaultLoadClicked(final ActionEvent e) throws Exception {
        this.model.unloadOSM();
        this.model.loadMapFile("data/small.osm.zip");
        this.canvas.setDisable(false);
        this.unloadFileButton.setDisable(false);
    }

    // when the menubar 'File' section button 'Import Custom map' is clicked
    @FXML private void onBrowseOSMClicked(final ActionEvent e) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose OSM File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map File", "*.osm,*.zip,*.obj"));
        File file = fileChooser.showOpenDialog(this.stage);

        if(file != null) {
            this.model.unloadOSM();
            this.model.loadMapFile(file.getAbsolutePath());
            this.canvas.setDisable(false);
            this.unloadFileButton.setDisable(false);
        }
    }

    // when the menubar 'File' section button 'Unload map' is clicked
    @FXML private void unloadFileButtonClicked(final ActionEvent e){
        this.model.unloadOSM();
        this.canvas.setDisable(true);
        this.canvas.clearScreen();
        this.unloadFileButton.setDisable(true);
    }

    // when the menubar 'File' section button 'Exit' is clicked
    @FXML private void exitButtonClicked(final ActionEvent e){
        System.exit(0);
    }

    // when the menubar 'Dev Tools' section button 'Enable Cursor Pointer' is clicked
    @FXML private void debugCursorClicked(final ActionEvent e){
        this.canvas.debugCursor = !this.canvas.debugCursor;
    }

    // when the menubar 'Dev Tools' section button 'Enable Kd-Tree VisBox' is clicked
    @FXML private void debugVisBoxClicked(final ActionEvent e){
        this.canvas.debugVisBox = !this.canvas.debugVisBox;
    }

    // when the menubar 'Dev Tools' section button 'Enable Kd-Tree Splits' is clicked
    @FXML private void debugSplitsClicked(final ActionEvent e){
        this.canvas.debugSplits = !this.canvas.debugSplits;
    }

    // when the menubar 'Dev Tools' section button 'Disable Help Text' is clicked
    @FXML private void debugHelpTextClicked(final ActionEvent e){
        this.canvas.debugDisableHelpText = !this.canvas.debugDisableHelpText;
    }

    // when the menubar 'Dev Tools' section button 'Disable Debug Box' is clicked
    @FXML private void debugInfoTextClicked(final ActionEvent e){
        this.canvas.debugInfo = !this.canvas.debugInfo;
    }

    // when the menubar 'Help' section button 'About' is clicked
    @FXML private void aboutButtonClicked(final ActionEvent e){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("Map of Denmark\nIT-Copenhagen First-Year-Project\n2022");
        alert.setResizable(false);
        alert.show();
    }
}