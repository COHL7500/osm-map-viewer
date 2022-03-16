package bfst22.vector;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

// Responsible for displaying model data.
public class View {
    public View(Model model, Stage primaryStage) throws IOException {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight() / 2));

        
        var loader = new FXMLLoader(View.class.getResource("ViewNew.fxml"));
        primaryStage.setScene(loader.load());

        primaryStage.setWidth(800);
        primaryStage.setMinWidth(400);
        primaryStage.setHeight(600);
        primaryStage.setMinHeight(300);
        
        primaryStage.show();

        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.setTitle("Danmarkskort - Gruppe #1");
    }
}
