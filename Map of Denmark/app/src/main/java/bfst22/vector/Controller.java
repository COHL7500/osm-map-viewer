package bfst22.vector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Stage stage;
    private Point2D lastMouse;
	private Model model;
	private boolean isMenuActive = false;
    private final List<String> loadedMaps = new ArrayList<>();
	
	@FXML private MapCanvas canvas;
    @FXML private VBox vBox;
    @FXML private BorderPane someBorderPane;
	@FXML private MenuItem unloadFileButton;
    @FXML private Menu recentMapsSubmenu;

    // Debug menu variables
    @FXML private VBox vbox_debug;
    @FXML private Label canvas_min;
    @FXML private Label canvas_max;
    @FXML private Label canvas_origin;
    @FXML private Label canvas_mouse;
    @FXML private Label canvas_zoom;
    @FXML private Label canvas_bounds_min;
    @FXML private Label canvas_bounds_max;
    @FXML private Label canvas_nodes;
    @FXML private Label canvas_ways;
    @FXML private Label canvas_relations;
    @FXML private Label canvas_filesize;
    @FXML private Label canvas_load_time;
    @FXML private Label canvas_repaint_time;
    @FXML private Label canvas_avg_repaint_time;

    // Runs upon start of program: Initializes our MapCanvas based on model.
    public void init(final Model model, final Stage primarystage) {
        this.someBorderPane.setLeft(null);
        this.model = model;
        this.stage = primarystage;
        this.canvas.init(model,this);
        this.addRecentLoadedMap(this.model.currFileName);
        this.canvas.centerPos();
        this.canvas.centerPos();
        this.canvas.pan(0,-50);
        this.canvas.update();

        this.someBorderPane.prefWidthProperty().bind(stage.widthProperty());
        this.someBorderPane.prefHeightProperty().bind(stage.heightProperty());
        this.someBorderPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            this.canvas.setWidth(newValue.doubleValue() - (this.isMenuActive ? 200 : 14));
            this.canvas.setWidth(this.canvas.getWidth() - (this.canvas.debugValMap.get("debugSideBar") ? 250 : 0));
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
        if (this.loadedMaps.size() > 10) this.loadedMaps.remove(this.loadedMaps.size()-1);
        this.recentMapsSubmenu.getItems().clear();

        for (int i = this.loadedMaps.size()-1; i > -1; i--) {
            String map = this.loadedMaps.get(i).replace("\\","/");
            MenuItem entry = new MenuItem((this.loadedMaps.size()-1-i) + ". " + map);
            entry.setUserData(map);
            entry.setOnAction(event -> {
                this.model.unloadOSM();
                try {
                    this.model.loadMapFile(entry.getUserData().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.canvas.centerPos();
                canvas.setDisable(false);
                this.unloadFileButton.setDisable(false);
            });

            this.recentMapsSubmenu.getItems().add(entry);
            this.recentMapsSubmenu.setDisable(false);
        }
    }

    public void updateDebugInfo(){
        if(this.model.isOSMLoaded && this.canvas.debugValMap.get("debugSideBar")){
            this.canvas_min.setText(String.format("%-27s%s", "min:", String.format("%.5f", this.canvas.minx) + ", " + String.format("%.5f", this.canvas.miny)));
            this.canvas_max.setText(String.format("%-26.5s%s", "max:", String.format("%.5f", this.canvas.maxx) + ", " + String.format("%.5f", this.canvas.maxy)));
            this.canvas_origin.setText(String.format("%-26s%s", "origin:", String.format("%.5f", this.canvas.originx) + ", " + String.format("%.5f", this.canvas.originy)));
            this.canvas_mouse.setText(String.format("%-24s%s", "mouse:", String.format("%.5f", this.canvas.mousex) + ", " + String.format("%.5f", this.canvas.mousey)));
            this.canvas_zoom.setText(String.format("%-25s%s", "zoom:", String.format("%.5f", this.canvas.zoom_current)));
            this.canvas_bounds_min.setText(String.format("%-21s%s", "bounds min:", String.format("%.5f", this.model.minlon) + ", " + String.format("%.5f", this.model.minlat)));
            this.canvas_bounds_max.setText(String.format("%-20s%s", "bounds max:", String.format("%.5f", this.model.maxlon) + ", " + String.format("%.5f", this.model.maxlat)));
            this.canvas_nodes.setText(String.format("%-25s%s", "nodes:", this.model.nodecount));
            this.canvas_ways.setText(String.format("%-26s%s", "ways:", this.model.waycount));
            this.canvas_relations.setText(String.format("%-25s%s", "relations:", this.model.relcount));
            this.canvas_filesize.setText(String.format("%-27s%.2f megabytes", "file size:", (float) this.model.filesize / 1000000));
            this.canvas_load_time.setText(String.format("%-24s%d ms", "load time:", this.model.loadTime/1000000));
            this.canvas_repaint_time.setText(String.format("%-23s%d ms", "repaint time:", this.canvas.repaintTime/1000000));
            this.canvas_avg_repaint_time.setText(String.format("%-20s%d ms", "avg repaint time:", this.canvas.avgRT/1000000));
        }
    }

    private String inputWindow(String title, String contentText){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setContentText(contentText);
        dialog.setResizable(false);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        return dialog.showAndWait().orElse(null);
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
        this.someBorderPane.setLeft((this.isMenuActive = !this.isMenuActive) ? vBox : null);
        this.canvas.setWidth(this.canvas.getWidth() - (this.isMenuActive ? 200 : -200));
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
            this.canvas.centerPos();
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

    // when the menubar 'Edit' section button 'Change Zoom Level' is clicked
    @FXML private void changeZoomLevelClicked(final ActionEvent e){
        String zlevel = this.inputWindow("Change Zoom Level","Syntax: 42000");
        if(zlevel != null){
            this.canvas.zoom(Integer.parseInt(zlevel)/this.canvas.zoom_current);
            this.canvas.update();
        }
    }

    // when the menubar 'Tools' section button 'Change Absolute Coordinates' is clicked
    @FXML private void changeAbsoluteCoordClicked(final ActionEvent e){
        String abscoords = this.inputWindow("Change Absolute Coordinates","Syntax: -12.345, 67.890");
        if(abscoords != null){
            String[] dialogvalue = abscoords.split(",");
            if(dialogvalue.length == 2) this.canvas.goToPosAbsolute(Double.parseDouble(dialogvalue[0]), Double.parseDouble(dialogvalue[1]));
        }
    }

    // when the menubar 'Tools' section button 'Change Relative Coordinates' is clicked
    @FXML private void changeRelativeCoordClicked(final ActionEvent e){
        String relcoords = this.inputWindow("Change Relative Coordinates","Syntax: -12.345, 67.890");
        if(relcoords != null){
            String[] dialogvalue = relcoords.split(",");
            if(dialogvalue.length == 2) this.canvas.goToPosRelative(Double.parseDouble(dialogvalue[0]), Double.parseDouble(dialogvalue[1]));
        }
    }

    // when the menubar 'Tools' section button 'Center Screen Position' is clicked
    @FXML private void centerScreenPosition(final ActionEvent e){
        this.canvas.centerPos();
        this.canvas.pan(0,-50);
    }

    // when the menubar 'Tools' section button 'Toggle Debug Sidebar' is clicked
    @FXML private void debugSidebarClicked(final ActionEvent e){
        this.canvas.debugPropertiesToggle("debugSideBar");
        this.someBorderPane.setRight(this.canvas.debugValMap.get("debugSideBar") ? vbox_debug : null);
        this.canvas.setWidth(this.canvas.getWidth() - (this.canvas.debugValMap.get("debugSideBar") ? 250 : -250));
        this.canvas.update();
    }

    // when the menubar 'Tools' section button 'Display Filled' is clicked
    @FXML private void debugDisplayFilledClicked(final ActionEvent e){
        this.canvas.debugValMap.replace("debugDisplayWireframe", false);
        this.canvas.update();
    }

    // when the menubar 'Tools' section button 'Display Wireframe' is clicked
    @FXML private void debugDisplayWireframeClicked(final ActionEvent e){
        this.canvas.debugValMap.replace("debugDisplayWireframe", true);
        this.canvas.update();
    }

    // when the menubar 'Tools' section button 'Enable Cursor Pointer' is clicked
    @FXML private void debugCursorClicked(final ActionEvent e) throws IOException {
        this.canvas.debugPropertiesToggle("debugCursor");
    }

    // when the menubar 'Tools' section button 'Enable Kd-Tree VisBox' is clicked
    @FXML private void debugVisBoxClicked(final ActionEvent e){
        this.canvas.debugPropertiesToggle("debugVisBox");
    }

    // when the menubar 'Tools' section button 'Enable Kd-Tree Splits' is clicked
    @FXML private void debugSplitsClicked(final ActionEvent e) throws IOException {
        this.canvas.debugPropertiesToggle("debugSplits");
    }

    // when the menubar 'Tools' section button 'Enable Free Movement' is clicked
    @FXML private void debugFreeMovementClicked(final ActionEvent e) throws IOException {
        this.canvas.debugPropertiesToggle("debugFreeMovement");
        if(!this.canvas.debugValMap.get("debugFreeMovement")) this.canvas.centerPos();
    }

    // when the menubar 'Tools' section button 'Disable Help Text' is clicked
    @FXML private void debugHelpTextClicked(final ActionEvent e) throws IOException {
        this.canvas.debugPropertiesToggle("debugDisableHelpText");
    }

    // when the menubar 'Tools' section button 'Disable Bounding Box' is clicked
    @FXML private void debugBoundingBoxClicked(final ActionEvent e) throws IOException {
        this.canvas.debugPropertiesToggle("debugBoundingBox");
    }

    // when the menubar 'Help' section button 'About...' is clicked
    @FXML private void aboutButtonClicked(final ActionEvent e){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("Map of Denmark\nIT-Copenhagen First-Year-Project\n2022 - Group #1");
        alert.setResizable(false);
        alert.show();
    }
}