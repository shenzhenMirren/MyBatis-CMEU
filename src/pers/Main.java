package pers;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.controller.IndexController;

public class Main extends Application {
	private static Logger log=Logger.getLogger(Main.class.getName());
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
    	PropertyConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("pers/resource/config/log4j.properties"));
    	try {
    		log.debug("运行MyBatis-CMEU...");
			launch(args);
			log.debug("关闭MyBatis-CMEU!!!");
		} catch (Exception e) {
			log.error(e);
		}
    }
}