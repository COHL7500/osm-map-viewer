package bfst22.vector;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.MissingResourceException;
import java.util.Objects;

// Responsible for displaying model data.
public class View {
    public View(Model model, Stage stage) throws Exception {
        this.setDisplayBound(stage);

    @FXML
    private BorderPane someBorderPane;

    // Main method for displaying the program
    public View(Model model, Stage primaryStage) throws IOException {

        setDisplayBound(primaryStage);

        var loader = new FXMLLoader(View.class.getResource("View.fxml"));

        Scene scene = loader.load();
        scene.getStylesheets().clear();

        getCSS(scene);
        setPrimaryStageSize(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.setTitle("Map Of Denmark");
    }

    // Getting the CSS file and implement it on the scene
    public void getCSS(Scene scene){
        String css = View.class.getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    // Setting the stage size for the application
    public void setPrimaryStageSize(Stage primaryStage){
        primaryStage.setWidth(800);
        primaryStage.setMinWidth(650);
        primaryStage.setHeight(600);
        primaryStage.setMinHeight(550);
    }

    // Setting the window displaybound so the scene spawns within the screen
    private void setDisplayBound(Stage primaryStage){
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight() / 2));
    }
}