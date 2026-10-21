import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentHubFxApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Загружаем FXML, который создадим на следующем шаге
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Электронный дневник");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}