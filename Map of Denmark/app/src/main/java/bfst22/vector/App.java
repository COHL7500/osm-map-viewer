package bfst22.vector;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;

// Responsible for initiating the application; Model is instantiated with our OSM file and finally View.
public class App extends Application {
    @Override public void start(Stage primaryStage) throws Exception {

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        primaryStage.setHeight(screenHeight / 1.5);
        primaryStage.setWidth(screenWidth / 1.5);

        Model model = new Model("data/small.osm.zip"); // Reads OSM file from data folder.
        new View(model, primaryStage); // primaryStage is our main window
    }
}