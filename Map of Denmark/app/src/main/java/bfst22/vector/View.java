package bfst22.vector;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

// Responsible for displaying model data.
public class View {
    public View(Model model, Stage primaryStage) throws IOException {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        double width = 800, height = 600;

        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight() / 2));

        var loader = new FXMLLoader(View.class.getResource("ViewNew.fxml"));

        Scene scene = loader.load();

        primaryStage.setScene(scene);

        scene.getStylesheets().add("Map Of Denmark/app/src/main/resources/bfst22/vector/css/visual.css");

        primaryStage.setWidth(width);
        primaryStage.setMinWidth(width / 2);
        primaryStage.setHeight(height);
        primaryStage.setMinHeight(height / 2);

        primaryStage.show();

        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.setTitle("Map Of Denmark");
    }
}
