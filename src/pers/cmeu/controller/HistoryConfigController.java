package pers.cmeu.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.models.ClassConfig;
import pers.cmeu.models.HistoryConfig;
import pers.cmeu.models.HistoryConfigCVF;
import pers.cmeu.view.AlertUtil;

public class HistoryConfigController extends BaseController {
	private Logger log = Logger.getLogger(HistoryConfigController.class.getName());
	private IndexController indexController;

	@FXML
	private TableView<HistoryConfigCVF> tblConfigInfo;

	@FXML
	private CheckBox chkGetAndSet;
	@FXML
	private CheckBox chkConstruct;
	@FXML
	private CheckBox chkConstructAll;
	@FXML
	private CheckBox chkUnlineCamel;
	@FXML
	private CheckBox chkSerializable;
	@FXML
	private CheckBox chkCreateJDBCtype;

	@FXML
	private Button btnSaveClassConfig;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.debug("初始化配置信息窗口....");
		tblConfigInfo.setPlaceholder(new Label("尚未添加任何配置信息;可以通过首页保存配置新增"));
		log.debug("初始化配置信息窗口完成!");
		initTable();
	}

	/**
	 * 初始化配置table
	 */
	public void initTable() {
		log.debug("初始化配置信息表格...");
		ObservableList<HistoryConfigCVF> data = null;
		try {
			data = getHistoryConfig();
		} catch (Exception e) {
			tblConfigInfo.setPlaceholder(new Label("加载配置文件失败!失败原因:\r\n" + e.getMessage()));
			log.error("初始化配置信息表格出现异常!!!" + e);
		}

		TableColumn<HistoryConfigCVF, String> tdInfo = new TableColumn<HistoryConfigCVF, String>("配置信息文件名");
		TableColumn<HistoryConfigCVF, String> tdOperation = new TableColumn<HistoryConfigCVF, String>("操作");

		tdInfo.setPrefWidth(320);
		tdInfo.setCellValueFactory(new PropertyValueFactory<>("name"));

		tdOperation.setPrefWidth(198);
		tdOperation.setCellValueFactory(new PropertyValueFactory<>("hbox"));

		tblConfigInfo.getColumns().add(tdInfo);
		tblConfigInfo.getColumns().add(tdOperation);

		tblConfigInfo.setItems(data);
		log.debug("初始化配置信息完成!");

		try {
			log.debug("初始化创建类配置信息...");
			// 从配置文件中获取配置信息并应用
			ClassConfig classConfig = ConfigUtil.getClassConfig();
			chkGetAndSet.setSelected(classConfig.isGetAndSet());
			chkConstruct.setSelected(classConfig.isConstruct());
			chkConstructAll.setSelected(classConfig.isConstructAll());
			chkUnlineCamel.setSelected(classConfig.isUnlineCamel());
			chkSerializable.setSelected(classConfig.isSeriz());
			chkCreateJDBCtype.setSelected(classConfig.isCreateJDBCType());
			log.debug("初始化创建类配置信息-->成功!");
		} catch (Exception e) {
			log.error("初始化创建类配置信息-->失败:" + e);
		}

	}

	/**
	 * 保存实体类配置信息
	 * 
	 * @param event
	 */
	public void saveClassConfig(ActionEvent event) {
		log.info("执行实体类配置...");
		boolean getAndSet = chkGetAndSet.isSelected();
		boolean construct = chkConstruct.isSelected();
		boolean constructAll = chkConstructAll.isSelected();
		boolean unlineCamel = chkUnlineCamel.isSelected();
		boolean serializable = chkSerializable.isSelected();
		boolean createJDBCType = chkCreateJDBCtype.isSelected();
		ClassConfig classConfig = new ClassConfig(getAndSet, construct, constructAll, unlineCamel, serializable,
				createJDBCType);
		try {
			int result = ConfigUtil.saveClassConfig(classConfig);
			if (result != 0) {
				AlertUtil.showInfoAlert("保存成功!");
			}
			log.info("执行实体类配置-->成功:受影响:" + result);
		} catch (Exception e) {
			AlertUtil.showErrorAlert(e.toString());
			log.error("执行实体类配置-->失败:" + e);
		}

	}

	/**
	 * 获得配置文件Table 特别注意,条件添加的关系,加载与删除配置需要在这里面操作
	 * 
	 * @return
	 * @throws Exception
	 */
	public ObservableList<HistoryConfigCVF> getHistoryConfig() throws Exception {
		ObservableList<HistoryConfigCVF> result = FXCollections.observableArrayList();
		List<HistoryConfig> item = ConfigUtil.getHistoryConfigs();
		// 遍历配置文件并加载到工厂里面,同时给操作配置文件的加载与删除

		for (HistoryConfig tmp : item) {
			String configName = tmp.getHistoryConfigName();
			HBox box = new HBox();
			box.setSpacing(15);
			Button button = new Button("加载配置");
			button.setUserData(tmp.getHistoryConfigName());
			button.setOnAction(Event -> {
				try {
					log.debug("执行将配置信息加载到首页...");
					indexController.loadIndexConfigInfo(button.getUserData().toString());
					closeDialogStage();
					log.debug("将配置信息加载到首页成功!");
				} catch (Exception e) {
					AlertUtil.showErrorAlert("加载配置失败!失败原因:\r\n" + e.getMessage());
					log.error("将配置信息加载到首页失败!!!" + e);
				}
			});
			Button button1 = new Button("删除配置");
			button1.setUserData(tmp.getHistoryConfigName());
			button1.setOnAction(Event -> {
				if (AlertUtil.showConfirmAlert("确定删除吗?")) {
					try {
						log.debug("执行删除配置信息...");
						ConfigUtil.deleteHistoryConfigByName(button1.getUserData().toString());
						for (int i = 0; i < tblConfigInfo.getItems().size(); i++) {
							if (tblConfigInfo.getItems().get(i).getName().equals(button1.getUserData().toString())) {
								tblConfigInfo.getItems().remove(i);
								break;
							}
						}
						log.debug("执行删除配置完成!");
					} catch (Exception e) {
						AlertUtil.showErrorAlert("删除失败!失败原因:\r\n" + e.getMessage());
						log.error("执行删除配置失败!!!" + e);
					}
				}
			});
			box.getChildren().addAll(button, button1);

			HistoryConfigCVF cvf = new HistoryConfigCVF(configName, box);
			result.add(cvf);
		}

		return result;
	}

	// -------------------get/set------------------------------

	public IndexController getIndexController() {
		return indexController;
	}

	public void setIndexController(IndexController indexController) {
		this.indexController = indexController;
	}

	public TableView<HistoryConfigCVF> getTblConfigInfo() {
		return tblConfigInfo;
	}

	public void setTblConfigInfo(TableView<HistoryConfigCVF> tblConfigInfo) {
		this.tblConfigInfo = tblConfigInfo;
	}

}
