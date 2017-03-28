package pers.cmeu.controller;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pers.cmeu.view.AlertUtil;

public abstract class BaseController implements Initializable {
   
    private Stage primaryStage;
    private Stage dialogStage;

    private static Map<FXMLPage, SoftReference<? extends BaseController>> cacheNodeMap = new HashMap<>();

    public BaseController loadFXMLPage(String title, FXMLPage fxmlPage, boolean cache) {
        SoftReference<? extends BaseController> parentNodeRef = cacheNodeMap.get(fxmlPage);
        if (cache && parentNodeRef != null) {
            return parentNodeRef.get();
        }
        URL skeletonResource = Thread.currentThread().getContextClassLoader().getResource(fxmlPage.getFxml());
        
        FXMLLoader loader = new FXMLLoader(skeletonResource);
       
        Parent loginNode;
        try {
            loginNode = loader.load();
            BaseController controller = loader.getController();
            dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(getPrimaryStage());
            dialogStage.setScene(new Scene(loginNode));
            dialogStage.setMaximized(false);
            dialogStage.setResizable(false);
            dialogStage.show();
            controller.setDialogStage(dialogStage);
            SoftReference<BaseController> softReference = new SoftReference<>(controller);
            cacheNodeMap.put(fxmlPage, softReference);
            return controller;
        } catch (IOException e) {
            AlertUtil.showErrorAlert(e.getMessage());
        }
        return null;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void showDialogStage() {
        if (dialogStage != null) {
            dialogStage.show();
        }
    }

    public void closeDialogStage() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
    public void closeDialogStageByMap(FXMLPage fxmlPage) {
		if (cacheNodeMap.get(fxmlPage).get().getDialogStage()!=null) {
			cacheNodeMap.get(fxmlPage).get().getDialogStage().close();
		}
	}

}
