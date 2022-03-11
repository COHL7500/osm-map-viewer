package bfst22.vector;

import javafx.application.Application;
import javafx.stage.Stage;

// Responsible for initiating the application; Model is instantiated with our OSM file and finally View.

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // var filename = getParameters().getRaw().get(0);
        var model = new Model("data/small.osm.zip"); // Reads OSM file from data folder.
        new View(model, primaryStage); // primaryStage is our main window
    }
}
