package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Stage stage;
    private Point2D lastMouse;
	private Model model;
	private boolean leftPaneVisibility = false;
    private final List<String> loadedMaps = new ArrayList<>();
	
	@FXML private MapCanvas canvas;
	@FXML private Button menuActivation;
    @FXML private VBox vBox;
    @FXML private Pane somePane;
    @FXML private BorderPane someBorderPane;
	@FXML private MenuItem unloadFileButton;
    @FXML private Menu recentMapsSubmenu;
    @FXML private ToggleGroup mapdisplay;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model, final Stage primarystage) {
		this.someBorderPane.setLeft(null);
        this.model = model;
        this.stage = primarystage;
        this.canvas.init(model);
        this.addRecentLoadedMap(this.model.currFileName);
        this.centerPos();
        this.centerPos();
        this.canvas.pan(0,-50);
        this.canvas.update();

        this.someBorderPane.prefWidthProperty().bind(stage.widthProperty());
        this.someBorderPane.prefHeightProperty().bind(stage.heightProperty());
        this.someBorderPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            this.canvas.setWidth(newValue.doubleValue() - (this.leftPaneVisibility ? 200 : 14));
            this.canvas.update();
        });
        this.someBorderPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            this.canvas.setHeight(newValue.doubleValue() - 130);
            this.canvas.update();
        });
    }

    private void addRecentLoadedMap(String filename){
        this.loadedMaps.remove(filename);
        this.loadedMaps.add(filename);
        if (loadedMaps.size() > 10) this.loadedMaps.remove(this.loadedMaps.size()-1);
        this.recentMapsSubmenu.getItems().clear();

        for (int i = this.loadedMaps.size()-1; i > -1; i--) {
            String map = this.loadedMaps.get(i).replace("\\","/");
            //String[] nameSplit = map.replace("\\", "/").split("/");
            MenuItem entry = new MenuItem((this.loadedMaps.size()-1-i) + ". " + map);
            entry.setUserData(map);
            entry.setOnAction(event -> {
                model.unloadOSM();
                try {
                    model.loadMapFile(entry.getUserData().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.centerPos();
                canvas.setDisable(false);
                this.unloadFileButton.setDisable(false);
            });

            this.recentMapsSubmenu.getItems().add(entry);
            this.recentMapsSubmenu.setDisable(false);
        }
    }

    private void goToPosAbsolute(final double x, final double y){
        double dx = (x - this.canvas.originx) * canvas.zoom_current;
        double dy = (y - this.canvas.originy) * canvas.zoom_current;
        this.canvas.pan(-dx,-dy);
    }

    private void goToPosRelative(final double x, final double y){
        this.canvas.pan(x*this.canvas.zoom_current,y*this.canvas.zoom_current);
    }

    private void centerPos(){
        double dx = (this.model.maxlon + this.model.minlon)/2;
        double dy = (this.model.maxlat + this.model.minlat)/2;
        this.goToPosAbsolute(dx,dy);
    }

    /* ---------- Mouse Methods ---------- */
    // handles an event of scrolling and increases/decreases the zoom level of the map.
    @FXML private void onScroll(final ScrollEvent e) {
        this.canvas.zoom(Math.pow(1.003, e.getDeltaY())); // Change value to control sensitivity.
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
        
    @FXML private void onMenuButtonPress(ActionEvent e){
        this.leftPaneVisibility = !this.leftPaneVisibility;
        this.someBorderPane.setLeft(this.leftPaneVisibility ? vBox : null);
        this.canvas.setWidth(this.someBorderPane.getWidth() - (this.leftPaneVisibility ? 200 : 0));
        this.canvas.update();
    }

    // updates the mouse position on the screen upon moving
    @FXML private void onMouseMoved(final MouseEvent e){
        this.canvas.setMousePos(new Point2D(e.getX(), e.getY()));
    }

    /* ---------- GUI Methods ---------- */
    // when the menubar 'File' section button 'Load Default map' is clicked
    @FXML private void onDefaultLoadClicked(final ActionEvent e) throws Exception {
        this.addRecentLoadedMap("data/small.osm.zip");
        this.model.unloadOSM();
        this.model.loadMapFile("data/small.osm.zip");
        this.canvas.setDisable(false);
        this.unloadFileButton.setDisable(false);
    }

    // when the menubar 'File' section button 'Import Custom map' is clicked
    @FXML private void onBrowseOSMClicked(final ActionEvent e) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose OSM File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map File", "*.osm","(*.zip)","*.obj"));
        File file = fileChooser.showOpenDialog(this.stage);

        if(file != null) {
            this.addRecentLoadedMap(file.getAbsolutePath());
            this.model.unloadOSM();
            this.model.loadMapFile(file.getAbsolutePath());
            this.centerPos();
            this.canvas.pan(0,-50);
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

    // when the menubar 'Edit' section button 'Zoom In' is clicked
    @FXML private void zoomInClicked(final ActionEvent e){
        this.canvas.zoom(1.2);
        this.canvas.pan(1,1);
    }

    // when the menubar 'Edit' section button 'Zoom Out' is clicked
    @FXML private void zoomOutClicked(final ActionEvent e){
        this.canvas.zoom(0.8);
        this.canvas.pan(1,1);
    }

    // when the menubar 'Tools' section button 'Change Absolute Coordinates' is clicked
    @FXML private void changeAbsoluteCoordClicked(final ActionEvent e){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Absolute Coordinates");
        dialog.setContentText("Syntax: -12.345, 67.890");
        dialog.setResizable(false);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> value = dialog.showAndWait();

        if(value.isPresent()){
            String[] dialogvalue = value.get().split(",");
            if(dialogvalue.length == 2) this.goToPosAbsolute(Double.parseDouble(dialogvalue[0]),Double.parseDouble(dialogvalue[1]));
        }
    }

    // when the menubar 'Tools' section button 'Change Relative Coordinates' is clicked
    @FXML private void changeRelativeCoordClicked(final ActionEvent e){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Relative Coordinates");
        dialog.setContentText("Syntax: -12.345, 67.890");
        dialog.setResizable(false);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        Optional<String> value = dialog.showAndWait();

        if(value.isPresent()){
            String[] dialogvalue = value.get().split(",");
            if(dialogvalue.length == 2) this.goToPosRelative(Double.parseDouble(dialogvalue[0]),Double.parseDouble(dialogvalue[1]));
        }
    }

    // when the menubar 'Tools' section button 'Center Screen Position' is clicked
    @FXML private void centerScreenPosition(final ActionEvent e){
        this.centerPos();
        this.canvas.pan(0,-50);
    }

    // when the menubar 'Tools' section button 'Display Filled' is clicked
    @FXML private void debugDisplayFilledClicked(final ActionEvent e){
        this.canvas.debugDisplayWireframe = false;
        this.canvas.update();
    }

    // when the menubar 'Tools' section button 'Display Wireframe' is clicked
    @FXML private void debugDisplayWireframeClicked(final ActionEvent e){
        this.canvas.debugDisplayWireframe = true;
        this.canvas.update();
    }

    // when the menubar 'Tools' section button 'Enable Cursor Pointer' is clicked
    @FXML private void debugCursorClicked(final ActionEvent e){
        this.canvas.debugCursor = !this.canvas.debugCursor;
    }

    // when the menubar 'Tools' section button 'Enable Kd-Tree VisBox' is clicked
    @FXML private void debugVisBoxClicked(final ActionEvent e){
        this.canvas.debugVisBox = !this.canvas.debugVisBox;
    }

    // when the menubar 'Tools' section button 'Enable Kd-Tree Splits' is clicked
    @FXML private void debugSplitsClicked(final ActionEvent e){
        this.canvas.debugSplits = !this.canvas.debugSplits;
    }

    // when the menubar 'Tools' section button 'Enable Free Movement' is clicked
    @FXML private void debugFreeMovementClicked(final ActionEvent e){
        this.canvas.debugFreeMovement = !this.canvas.debugFreeMovement;
        if(!this.canvas.debugFreeMovement) this.centerPos();
    }

    // when the menubar 'Tools' section button 'Disable Help Text' is clicked
    @FXML private void debugHelpTextClicked(final ActionEvent e){
        this.canvas.debugDisableHelpText = !this.canvas.debugDisableHelpText;
    }

    // when the menubar 'Tools' section button 'Disable Debug Box' is clicked
    @FXML private void debugInfoTextClicked(final ActionEvent e){
        this.canvas.debugInfo = !this.canvas.debugInfo;
    }

    // when the menubar 'Tools' section button 'Disable Bounding Box' is clicked
    @FXML private void debugBoundingBoxClicked(final ActionEvent e){
        this.canvas.debugBoundingBox = !this.canvas.debugBoundingBox;
    }

    // when the menubar 'Help' section button 'About...' is clicked
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