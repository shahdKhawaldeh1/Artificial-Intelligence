package projectAI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/*
Responsible for the interface (its size, name, and interface file)
*/

public class mainMap extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //file name withjavafx
        Parent main = FXMLLoader.load(getClass().getResource("mainMap.fxml"));
        primaryStage.setScene(new Scene(main, 580, 580));//size
        primaryStage.setTitle("Search Algorithms :)");//name
        primaryStage.setResizable(false);
        primaryStage.show();//show
    }

    public static void main(String[] args) {
        launch(args);
    }
}
