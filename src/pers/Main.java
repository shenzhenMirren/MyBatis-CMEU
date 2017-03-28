package pers;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.controller.IndexController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	ConfigUtil.existsConfigDB();
        URL url = Thread.currentThread().getContextClassLoader().getResource("pers/resource/FXML/Index.fxml");
       
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(true);
        primaryStage.setTitle("MyBatis-Config-Mapper-Entity-Util");
        primaryStage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
        IndexController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}