package bfst22.vector;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.stage.Screen;
import javafx.stage.Stage;

// Responsible for displaying model data.
public class View {
    public View(Model model, Stage primaryStage) throws IOException {
        primaryStage.show();
        var loader = new FXMLLoader(View.class.getResource("View.fxml"));
        primaryStage.setWidth(Screen.getPrimary().getBounds().getWidth()/1.5);
        primaryStage.setHeight(Screen.getPrimary().getBounds().getHeight()/1.5);
        primaryStage.setScene(loader.load());
        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.setTitle("Danmarkskort - Gruppe #1");
    }
}
