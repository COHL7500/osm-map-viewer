package bfst22.vector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

// Responsible for displaying model data.
public class View {
    private final int width, height;
    private final String style;

	
    public View(Model model, Stage stage) throws Exception {
        this.width = 800;
        this.height = 600;
        String fxml = "View.fxml";
        this.style = "style.css";
        String map = "data/small.osm.zip";
        String title = "Danmarkskort - Gruppe #1";

        model.loadMapFile(map);

        FXMLLoader loader = new FXMLLoader(View.class.getResource(fxml));
        Scene scene = loader.load();

        this.setDisplayBound(stage);
        this.getCSS(scene);
        this.setPrimaryStageSize(stage);

        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();

        ((Controller) loader.getController()).init(model,stage);
    }

    // Getting the CSS file and implement it on the scene
    private void getCSS(Scene scene) throws NullPointerException {
        try {
            String css = View.class.getResource(this.style).toExternalForm();
            scene.getStylesheets().add(css);
        }
        catch (NullPointerException npe) {
            System.out.println("ERROR: CSS cannot load/find file:\n" + npe.getMessage());
        }

    }

    // Setting the stage size for the application
    private void setPrimaryStageSize(Stage primaryStage){
        primaryStage.setWidth(this.width);
        primaryStage.setMinWidth(this.width/2.0);
        primaryStage.setHeight(this.height);
        primaryStage.setMinHeight(this.height/2.0);
    }

    // Setting the window displaybound so the scene spawns within the screen
    private void setDisplayBound(Stage primaryStage){
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight() / 2));
    }
}