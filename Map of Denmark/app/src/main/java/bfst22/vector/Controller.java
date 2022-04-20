package bfst22.vector;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Responsible for controlling/updating the current view and manipulating dataflow of model.
public class Controller {
    private Stage stage;
	private Model model;
    private List<String> loadedMaps;
    private ContextMenu canvasCM;

    private Search search;
	
	@FXML private MapCanvas canvas;
    @FXML private VBox pinPointSidebar;
    @FXML private ScrollPane vBox_scrollpane;
    @FXML private HBox paintBox;
    @FXML private Pane somePane;
    @FXML private ToolBar paintBar;
    @FXML private BorderPane someBorderPane;
	@FXML private MenuItem unloadFileButton;
    @FXML private Menu recentMapsSubmenu;
    @FXML private ToggleGroup mapdisplay, brushModeGroup;
    @FXML private ColorPicker paintColourPicker;
    @FXML private Spinner<Double> paintStrokeSize;
    @FXML private Spinner<Integer> paintFontSize;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private ToggleButton zoomBoxButton;
    @FXML private ToggleButton zoomMagnifyingGlass;
    @FXML private ToggleButton pinpointButton;
    @FXML private ComboBox<String> fontBox;
    @FXML private TreeView<String> featuresTreeView;
    @FXML private ListView<HBox> pinPointList;

    // Debug menu variables
    @FXML private ScrollPane vbox_debug_scrollpane;
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

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------------ General Methods ------------------------------------------------ *
     * ----------------------------------------------------------------------------------------------------------------- */
    // Runs upon start of program: Initializes our MapCanvas based on model.
    public Controller(final Model model, final Stage primarystage) {
        primarystage.setScene(new Scene(Controller.smartFXMLLoader(this,"View.fxml")));
        primarystage.setWidth(this.someBorderPane.getPrefWidth());
        primarystage.setHeight(this.someBorderPane.getPrefHeight());

        this.model = model;
        this.stage = primarystage;
        this.loadedMaps = new ArrayList<>();
        this.canvasCM = new ContextMenu();

        this.search = new Search(model);

        this.someBorderPane.setLeft(null);
        this.someBorderPane.setRight(null);
        this.canvas.init(model);
        this.canvas.pinpoints.init(pinPointList);
        this.addRecentLoadedMap(this.model.currFileName);
        this.canvas.centerMap();
        this.generateTreeView();
        this.generateContextMenu();

        this.someBorderPane.prefWidthProperty().bind(stage.widthProperty());
        this.someBorderPane.prefHeightProperty().bind(stage.heightProperty());
        this.someBorderPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            this.canvas.setWidth(newValue.doubleValue() - (this.someBorderPane.getLeft() != null ? 265 : 0));
            this.canvas.setWidth(this.canvas.getWidth() - (this.someBorderPane.getRight() != null ? 265 : 0));
            this.canvas.update();
            this.canvas.checkInBounds();
        });
        this.someBorderPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            this.canvas.setHeight(newValue.doubleValue()-80);
            this.canvas.update();
            this.canvas.checkInBounds();
        });
        this.paintBar.managedProperty().bind(this.paintBar.visibleProperty());
        this.someBorderPane.setOnKeyPressed(e -> {
            this.canvas.painter.keyPress(e.getText());
            this.canvas.update();
        });
        this.fontBox.getItems().addAll(Font.getFamilies());
        this.fontBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.canvas.painter.setFont(newValue));
    }

    private void generateContextMenu(){
        MenuItem addPoint = new MenuItem("Add Pin Point Here");
        addPoint.setGraphic(new FontIcon("fas-map-pin:12"));
        addPoint.setOnAction(item -> this.canvas.pinpoints.newWindow(this.canvas));
        this.canvasCM.getItems().add(addPoint);
        this.canvas.setOnContextMenuRequested(e -> this.canvasCM.show(this.canvas, e.getScreenX(), e.getScreenY()));
    }

    public static Parent smartFXMLLoader(Object con, String filename) {
        try {
            FXMLLoader loader = new FXMLLoader(con.getClass().getResource(filename));
            loader.setController(con);
            return loader.load();
        } catch(IOException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void updateDebugInfo(){
        if(!this.model.isLoaded() || this.model.isLoaded() && this.someBorderPane.getRight() != null){
            this.canvas_min.setText(String.format("%-27s%s", "min:", String.format("%.5f", this.canvas.minPos.getX()) + ", " + String.format("%.5f", this.canvas.minPos.getY())));
            this.canvas_max.setText(String.format("%-26.5s%s", "max:", String.format("%.5f", this.canvas.maxPos.getX()) + ", " + String.format("%.5f", this.canvas.maxPos.getY())));
            this.canvas_origin.setText(String.format("%-26s%s", "origin:", String.format("%.5f", this.canvas.originPos.getX()) + ", " + String.format("%.5f", this.canvas.originPos.getY())));
            this.canvas_mouse.setText(String.format("%-24s%s", "mouse:", String.format("%.5f", this.canvas.mousePos.getX()) + ", " + String.format("%.5f", this.canvas.mousePos.getY())));
            this.canvas_zoom.setText(String.format("%-25s%s", "zoom:", String.format("%.5f", this.canvas.zoom_current)));
            this.canvas_bounds_min.setText(String.format("%-21s%s", "bounds min:", String.format("%.5f", this.model.minBoundsPos.getY()) + ", " + String.format("%.5f", this.model.minBoundsPos.getX())));
            this.canvas_bounds_max.setText(String.format("%-20s%s", "bounds max:", String.format("%.5f", this.model.maxBoundsPos.getY()) + ", " + String.format("%.5f", this.model.maxBoundsPos.getX())));
            this.canvas_nodes.setText(String.format("%-25s%s", "nodes:", this.model.nodecount));
            this.canvas_ways.setText(String.format("%-26s%s", "ways:", this.model.waycount));
            this.canvas_relations.setText(String.format("%-25s%s", "relations:", this.model.relcount));
            this.canvas_filesize.setText(String.format("%-27s%d bytes", "file size:", this.model.filesize));
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

    private void generateTreeView(){
        CheckBoxTreeItem<String> root = new CheckBoxTreeItem<>("Map Elements");
        root.setExpanded(true);
        root.selectedProperty().addListener(this::treeboxselected);
        this.featuresTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        this.featuresTreeView.setRoot(root);

        for(Map.Entry<String,keyFeature> feature : this.model.yamlObj.ways.entrySet()){
            CheckBoxTreeItem<String> featureString = new CheckBoxTreeItem<>(feature.getKey());
            featureString.selectedProperty().addListener(this::treeboxselected);
            root.getChildren().add(featureString);
            for(Map.Entry<String,valueFeature> subfeature : feature.getValue().valuefeatures.entrySet()){
                CheckBoxTreeItem<String> subfeatureString = new CheckBoxTreeItem<>(subfeature.getKey());
                featureString.getChildren().add(subfeatureString);
                subfeatureString.selectedProperty().addListener(this::treeboxselected);
                subfeatureString.setSelected(true);
            }
        }
    }

    private void treeboxselected(Observable box){
        TreeItem<String> root = this.featuresTreeView.getRoot();
        MapFeature yaml = this.model.yamlObj;

        root.getChildren().forEach(keyFeature -> {
            keyFeature keyobj = yaml.ways.get(keyFeature.getValue());
            keyFeature.getChildren().forEach(valueFeature -> {
                valueFeature valueobj = keyobj.valuefeatures.get(valueFeature.getValue());
                valueobj.draw.display = ((CheckBoxTreeItem<String>) valueFeature).isSelected();
            });
            keyobj.draw.display = ((CheckBoxTreeItem<String>) keyFeature).isSelected();
        });
        yaml.draw.display = ((CheckBoxTreeItem<String>) root).isSelected();
        this.canvas.update();
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ---------------------------------------------- Map Loading Methods ---------------------------------------------- *
     * ----------------------------------------------------------------------------------------------------------------- */
    private void loadMap(String filename) throws XMLStreamException, IOException, ClassNotFoundException {
        this.addRecentLoadedMap(filename);
        this.model.unload();
        this.model.load(filename);
        this.canvas.reset();
        this.canvas.zoomTo(42000);
        this.canvas.centerPos();
        this.canvas.panTo(new Point2D(0,-50));
        this.canvas.setDisable(false);
        this.unloadFileButton.setDisable(false);
        this.updateDebugInfo();
    }

    private void unloadMap(){
        this.model.unload();
        this.canvas.reset();
        this.canvas.setDisable(true);
        this.canvas.clearScreen();
        this.unloadFileButton.setDisable(true);
        this.updateDebugInfo();
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
                this.model.unload();
                this.canvas.reset();
                try {
                    this.model.load(entry.getUserData().toString());
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

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------------ Menubar Methods ------------------------------------------------ *
     * ----------------------------------------------------------------------------------------------------------------- */
    @FXML private void onMenuButtonPress(ActionEvent e){
        this.someBorderPane.setLeft(this.someBorderPane.getLeft() == null ? vBox_scrollpane : null);
        this.canvas.setWidth(this.canvas.getWidth() - (this.someBorderPane.getLeft() != null ? 265 : -265)); // Find a way to make this non-hardcoded
        this.canvas.update();
    }

    @FXML private void onZoomBoxButtonPressed(ActionEvent e){
        this.canvas.zoombox.setState(this.zoomBoxButton.isSelected());
    }

    @FXML private void onZoomMagnifyingGlassButtonPressed(ActionEvent e){
        this.canvas.zoomMagnifyingGlass = !this.canvas.zoomMagnifyingGlass;
    }

    @FXML private void onSearchButtonPressed(ActionEvent e){
        search();
    }

    @FXML private void onKeyPressed(KeyEvent k){
        if (k.getCode().equals(KeyCode.ENTER)) {
            search();
        }
        else autoComplete();
    }

    @FXML private void onPaintFillCheckboxPressed(ActionEvent e){
        this.canvas.painter.toggleFill();
    }

    @FXML private void onPaintButtonPressed(ActionEvent e){
        if(this.brushModeGroup.getSelectedToggle() == null) this.canvas.painter.setDrawMode(-1);
        else this.canvas.painter.setDrawMode(Integer.parseInt((String) this.brushModeGroup.getSelectedToggle().getUserData()));
    }

    @FXML private void onPaintColorButtonPressed(ActionEvent e){
        this.canvas.painter.setColour(this.paintColourPicker.getValue());
    }

    @FXML private void spinnerPaintStrokeSizeButtonPressed(KeyEvent e){
        this.canvas.painter.setStroke(paintStrokeSize.getValue());
    }

    @FXML private void spinnerPaintFontSizeButtonPressed(KeyEvent e){
        this.canvas.painter.setFontSize(this.paintFontSize.getValue());
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------------- Mouse Methods ------------------------------------------------- *
     * ----------------------------------------------------------------------------------------------------------------- */
    // handles an event of scrolling and increases/decreases the zoom level of the map.
    @FXML private void onScroll(final ScrollEvent e) {
        this.canvas.scrolled(e.getDeltaY());
        this.updateDebugInfo();
    }

    // handles panning in the program
    @FXML private void onMouseDragged(final MouseEvent e) {
        this.canvas.dragged(e,new Point2D(e.getX(), e.getY()));
        this.updateDebugInfo();
    }

    // updates the variable lastMouse upon pressing (necessary for onMouseDragged)
    @FXML private void onMousePressed(final MouseEvent e) {
        this.canvasCM.hide();
        if(e.getClickCount() == 2) this.canvas.pinpoints.doubleClick(this.canvas);
        this.canvas.pressed(e);
        this.updateDebugInfo();
    }

    // updates upon releasing
    @FXML private void onMouseReleased(final MouseEvent e) {
        this.canvas.released(e);
        this.updateDebugInfo();
    }

    // updates the mouse position on the screen upon moving
    @FXML private void onMouseMoved(final MouseEvent e){
        this.canvas.moved(new Point2D(e.getX(), e.getY()));
        this.updateDebugInfo();
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * --------------------------------------------- Menu Dropdown Methods --------------------------------------------- *
     * ----------------------------------------------------------------------------------------------------------------- */
    // when the menubar 'File' section button 'Load Default map' is clicked
    @FXML private void onDefaultLoadClicked(final ActionEvent e) throws Exception {
        this.loadMap("data/small.osm.zip");
    }

    // when the menubar 'File' section button 'Import Custom map' is clicked
    @FXML private void onBrowseOSMClicked(final ActionEvent e) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose OSM File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map File", "*.osm","*.zip","*.obj"));
        File file = fileChooser.showOpenDialog(this.stage);

        if(file != null) this.loadMap(file.getAbsolutePath());
    }

    // when the menubar 'File' section button 'Unload map' is clicked
    @FXML private void unloadFileButtonClicked(final ActionEvent e){
        this.unloadMap();
    }

    // when the menubar 'File' section button 'Exit' is clicked
    @FXML private void exitButtonClicked(final ActionEvent e){
        System.exit(0);
    }

    // when the menubar 'Edit' section button 'Zoom In' is clicked
    @FXML private void zoomInClicked(final ActionEvent e){
        this.canvas.zoomTo(1.2);
    }

    // when the menubar 'Edit' section button 'Zoom Out' is clicked
    @FXML private void zoomOutClicked(final ActionEvent e){
        this.canvas.zoomTo(0.8);
    }

    // when the menubar 'Edit' section button 'Change Zoom Level' is clicked
    @FXML private void changeZoomLevelClicked(final ActionEvent e){
        String zlevel = this.inputWindow("Change Zoom Level","Syntax: 42000");
        if(zlevel != null && !zlevel.isEmpty()) this.canvas.zoomTo(Integer.parseInt(zlevel)/this.canvas.zoom_current);
    }

    // when the menubar 'View' section button 'Paint Bar' is clicked
    @FXML private void paintBarButtonClicked(final ActionEvent e){
        this.paintBar.setVisible(!this.paintBar.isVisible());
        this.canvas.setHeight(this.canvas.getHeight() - (this.paintBar.isVisible() ? 30 : -30));
        this.canvas.update();
    }

    // when the menubar 'View' section button 'Toggle Debug Sidebar' is clicked
    @FXML private void debugSidebarClicked(final ActionEvent e){
		this.canvas.debugPropertiesToggle("debugSideBar");
        this.someBorderPane.setRight(this.someBorderPane.getRight() == null ? vbox_debug_scrollpane : null);
        this.canvas.setWidth(this.canvas.getWidth() - (this.someBorderPane.getRight() != null ? 265 : -265)); // Find a way to make this non-hardcoded
        this.canvas.update();
        this.updateDebugInfo();
    }

    // when the menubar 'Tools' section button 'Change Absolute Coordinates' is clicked
    @FXML private void changeAbsoluteCoordClicked(final ActionEvent e){
        String abscoords = this.inputWindow("Change Absolute Coordinates","Syntax: -12.345, 67.890");
        if(abscoords != null){
            String[] dialogvalue = abscoords.split(",");
            if(dialogvalue.length == 2) this.canvas.goToPosAbsolute(new Point2D(Double.parseDouble(dialogvalue[0]), Double.parseDouble(dialogvalue[1])));
        }
    }

    // when the menubar 'Tools' section button 'Change Relative Coordinates' is clicked
    @FXML private void changeRelativeCoordClicked(final ActionEvent e){
        String relcoords = this.inputWindow("Change Relative Coordinates","Syntax: -12.345, 67.890");
        if(relcoords != null){
            String[] dialogvalue = relcoords.split(",");
            if(dialogvalue.length == 2) this.canvas.goToPosRelative(new Point2D(Double.parseDouble(dialogvalue[0]), Double.parseDouble(dialogvalue[1])));
        }
    }

    // when the menubar 'Tools' section button 'Center Screen Position' is clicked
    @FXML private void centerScreenPosition(final ActionEvent e){
        this.canvas.centerPos();
        this.canvas.panTo(new Point2D(0,-50));
    }

    // when the menubar 'Tools' section button 'Display Filled' is clicked
    @FXML private void debugDisplayFilledClicked(final ActionEvent e){
        this.canvas.debugValMap.replace("debugDisplayWireframe", false);
    }

    // when the menubar 'Tools' section button 'Display Wireframe' is clicked
    @FXML private void debugDisplayWireframeClicked(final ActionEvent e){
        this.canvas.debugValMap.replace("debugDisplayWireframe", true);
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
    @FXML private void debugFreeMovementClicked(final ActionEvent e){
		this.canvas.debugPropertiesToggle("debugFreeMovement");
        this.canvas.checkInBounds();
    }

    // when the menubar 'Tools' section button 'Disable Help Text' is clicked
    @FXML private void debugHelpTextClicked(final ActionEvent e){
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

    // Helper method for search
    public void search() {
        try {
            System.out.println(search.addressSearch(searchField.getText()).toString());
        } catch (NullPointerException n) {
            System.out.println("No result");
        }
        searchField.setText("");
    }

    public void autoComplete() {
        for (Address address : search.searchSuggestions(searchField.getText()))
        System.out.println(address.toString());
    }
}