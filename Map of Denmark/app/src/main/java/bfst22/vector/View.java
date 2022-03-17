package bfst22.vector;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

// Responsible for displaying model data.

public class View {
    public View(Model model, Stage primaryStage) throws IOException {
        primaryStage.show();
        var loader = new FXMLLoader(View.class.getResource("ViewNew.fxml"));
        primaryStage.setScene(loader.load());
        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.setTitle("Danmarkskort - Gruppe #1");
    }
}
