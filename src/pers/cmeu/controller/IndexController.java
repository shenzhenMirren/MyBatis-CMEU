package pers.cmeu.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.common.DBUtil;
import pers.cmeu.common.FileFactory;
import pers.cmeu.common.StrUtil;
import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.HistoryConfig;
import pers.cmeu.models.SuperAttribute;
import pers.cmeu.view.AlertUtil;

public class IndexController extends BaseController {

	// 存储数据库指定数据库,修改属性时用
	private DatabaseConfig selectedDatabaseConfig;
	// 记录存储的表名,修改属性时用
	private String selectedTableName;
	// 标记属性是否修改false为没有修改
	private boolean changeInfo = false;
	// 需要创建所有类的集合;
	private List<SuperAttribute> superAttributes = new ArrayList<SuperAttribute>();
	// 确定信息是否需要从新加载信息
	private boolean falg = false;

	@FXML
	private Label lblConnection;
	@FXML
	private Label lblConfig;
	@FXML
	private TreeView<String> tvDataBase;
	@FXML
	private TextField txtProjectPath;
	@FXML
	private TextField txtRootDir;
	@FXML
	private TextField txtTableName;

	@FXML
	private TextField txtDaoPackage;
	@FXML
	private TextField txtDaoName;
	@FXML
	private TextField txtEntityPackage;
	@FXML
	private TextField txtEntityName;
	@FXML
	private TextField txtMapPackage;
	@FXML
	private TextField txtMapName;
	@FXML
	private TextField txtAssistPackage;
	@FXML
	private TextField txtAssistName;
	@FXML
	private TextField txtConfigPackage;
	@FXML
	private TextField txtConfigName;
	@FXML
	private TextField txtMyUtilPackage;
	@FXML
	private TextField txtMyUtilName;

	@FXML
	private CheckBox chkAssist;
	@FXML
	private CheckBox chkConfig;
	@FXML
	private CheckBox chkMyUtil;

	@FXML
	private Label lblAssistPackage;
	@FXML
	private Label lblAssistName;
	@FXML
	private Label lblConfigPackage;
	@FXML
	private Label lblConfigName;
	@FXML
	private Label lblMyUtilPackage;
	@FXML
	private Label lblMyUtilName;

	@FXML
	private Button btnSelectFile;
	@FXML
	private Button btnUpdateEntity;
	@FXML
	private Button btnRunCreate;
	@FXML
	private Button btnSaveConfig;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 初始化图标连接与配置信息
		ImageView lblConnImage = new ImageView("pers/resource/image/computer.png");
		lblConnImage.setFitHeight(40);
		lblConnImage.setFitWidth(40);
		lblConnection.setGraphic(lblConnImage);
		lblConnection.setOnMouseClicked(event -> {
			ConnectionController controller = (ConnectionController) loadFXMLPage("新建数据库连接", FXMLPage.CONNECTION,
					false);
			controller.setIndexController(this);
			controller.showDialogStage();

		});
		ImageView lblConfImage = new ImageView("pers/resource/image/config.png");
		lblConfImage.setFitHeight(40);
		lblConfImage.setFitWidth(40);
		lblConfig.setGraphic(lblConfImage);
		lblConfig.setOnMouseClicked(enent -> {
			HistoryConfigController controller = (HistoryConfigController) loadFXMLPage("配置信息管理",
					FXMLPage.HISTORY_CONFIG, false);
			controller.setIndexController(this);
			controller.showDialogStage();
		});

		// 加载右边数据库树与事件
		tvDataBase.setShowRoot(false);
		tvDataBase.setRoot(new TreeItem<>());
		Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
		tvDataBase.setCellFactory((TreeView<String> tv) -> {
			TreeCell<String> cell = defaultCellFactory.call(tv);
			cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				int level = tvDataBase.getTreeItemLevel(cell.getTreeItem());
				TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
				TreeItem<String> treeItem = treeCell.getTreeItem();
				if (level == 1) {
					final ContextMenu contextMenu = new ContextMenu();
					MenuItem item0 = new MenuItem("打开连接");
					item0.setOnAction(event1 -> {
						DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
						try {
							List<String> tables = DBUtil.getTableNames(selectedConfig);
							if (tables != null && tables.size() > 0) {
								ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
								children.clear();
								for (String tableName : tables) {
									TreeItem<String> newTreeItem = new TreeItem<>();
									ImageView imageView = new ImageView("pers/resource/image/table.png");
									imageView.setFitHeight(16);
									imageView.setFitWidth(16);
									newTreeItem.setGraphic(imageView);
									newTreeItem.setValue(tableName);
									children.add(newTreeItem);
								}
							}
						} catch (CommunicationsException e) {
							AlertUtil.showErrorAlert("连接超时");
						} catch (Exception e) {
							AlertUtil.showErrorAlert(e.getMessage());
						}
					});
					MenuItem item1 = new MenuItem("关闭连接");
					item1.setOnAction(event1 -> {
						treeItem.getChildren().clear();
					});
					MenuItem item2 = new MenuItem("删除连接");
					item2.setOnAction(event1 -> {
						if (!AlertUtil.showConfirmAlert("确定删除该连接吗")) {
							return;
						}
						DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
						try {
							ConfigUtil.deleteDatabaseConfig(selectedConfig.getConnName());
							this.loadTVDataBase();
						} catch (Exception e) {
							AlertUtil.showErrorAlert("删除数据库连接失败: " + e.getMessage());
						}
					});
					contextMenu.getItems().addAll(item0, item1, item2);
					cell.setContextMenu(contextMenu);
				}
				if (event.getClickCount() == 2) {
					treeItem.setExpanded(true);
					if (level == 1) {
						DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
						try {
							List<String> tables = DBUtil.getTableNames(selectedConfig);
							if (tables != null && tables.size() > 0) {
								ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
								children.clear();
								// 获得树节点
								for (String tableName : tables) {
									TreeItem<String> newTreeItem = new TreeItem<>();
									ImageView imageView = new ImageView("pers/resource/image/table.png");
									imageView.setFitHeight(18);
									imageView.setFitWidth(18);
									newTreeItem.setGraphic(imageView);
									newTreeItem.setValue(tableName);
									children.add(newTreeItem);
								}
							}
						} catch (CommunicationsException e) {
							AlertUtil.showErrorAlert("连接超时");
						} catch (Exception e) {
							AlertUtil.showErrorAlert(e.getMessage());
						}
					} else if (level == 2) {
						String tableName = treeCell.getTreeItem().getValue();
						selectedDatabaseConfig = (DatabaseConfig) treeItem.getParent().getGraphic().getUserData();
						selectedTableName = tableName;
						txtTableName.setText(tableName);
						txtEntityName.setText(StrUtil.unlineToPascal(tableName));
						txtDaoName.setText(StrUtil.unlineToPascal(tableName) + "Dao");
						txtMapName.setText(StrUtil.unlineToPascal(tableName) + "Mapper");
					}
				}
			});
			return cell;
		});
		// 加载左边数据库树
		loadTVDataBase();
		try {
			// 加载首页配置信息
			loadIndexConfigInfo("default");
		} catch (Exception e) {
			AlertUtil.showErrorAlert("加载配置失败!失败原因:\r\n" + e.getMessage());
		}

	}

	public String text() {
		return selectedTableName;
	}
	
	/**
	 * 加载数据库到树集
	 */
	public void loadTVDataBase() {
		TreeItem<String> rootTreeItem = tvDataBase.getRoot();
		rootTreeItem.getChildren().clear();
		List<DatabaseConfig> item = null;
		try {
			item = ConfigUtil.getDatabaseConfig();
			for (DatabaseConfig dbConfig : item) {
				TreeItem<String> treeItem = new TreeItem<String>();
				treeItem.setValue(dbConfig.getConnName());
				ImageView dbImage = new ImageView("pers/resource/image/database.png");
				dbImage.setFitHeight(20);
				dbImage.setFitWidth(20);
				dbImage.setUserData(dbConfig);
				treeItem.setGraphic(dbImage);
				rootTreeItem.getChildren().add(treeItem);
			}
		} catch (Exception e) {
			AlertUtil.showErrorAlert(e.getMessage());
		}
	}

	/**
	 * 选择文件
	 * 
	 * @param event
	 */
	public void selectFile(ActionEvent event) {

		DirectoryChooser directoryChooser = new DirectoryChooser();

		File file = directoryChooser.showDialog(super.getPrimaryStage());
		if (file != null) {
			txtProjectPath.setText(file.getPath());
		}
	}

	/**
	 * 修改实体属性
	 * 
	 * @param event
	 */
	public void updateEntity(ActionEvent event) {
		if (selectedTableName == null) {
			AlertUtil.showWarnAlert("请先选择数据库表!打开左侧数据库双击表名便可加载...");
			return;
		}

		// 打开属性窗并讲数据库与表名给属性窗
		AttributeSetController controller = (AttributeSetController) loadFXMLPage("属性设置", FXMLPage.ATTRIBUTE_SET,
				false);
		controller.setIndexController(this);
		controller.showDialogStage();
		controller.setSelectedDatabaseConfig(selectedDatabaseConfig);
		controller.setSelectedTableName(selectedTableName);
		if (falg == false) {
			controller.initTable(false);
		} else {
			controller.initTable(true);
		}

	}

	/**
	 * 保存配置文件
	 * 
	 * @param event
	 */
	public void saveConfig(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("保存当前配置");
		dialog.setContentText("请输入配置名称:\r\n(表名不在保存范围内必须通过数据库加载!!!)");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String name = result.get();
			try {
				HistoryConfig config = getHistoryConfig();
				config.setHistoryConfigName(name);
				ConfigUtil.saveHistoryConfig(config);
				AlertUtil.showInfoAlert("保存配置成功!");
			} catch (Exception e) {
				AlertUtil.showErrorAlert("保存配置失败!失败原因:\r\n" + e.getMessage());
			}
		}
	}

	/**
	 * 执行创建
	 * 
	 * @param event
	 */
	public void runCreate(ActionEvent event) {
		if (txtProjectPath.getText().trim().equals("") || txtRootDir.getText().trim().equals("")
				|| txtTableName.getText().trim().equals("") || txtEntityName.getText().trim().equals("")) {
			AlertUtil.showWarnAlert("除了包名其他的选项都为必填选项;\r\n实体类可以通过双击左边数据库表加载...");
			return;
		}

		// 初始化文件工厂
		FileFactory factory = new FileFactory(txtProjectPath.getText(), txtRootDir.getText(), chkAssist.isSelected(),
				chkConfig.isSelected(), chkMyUtil.isSelected());
		factory.init(selectedDatabaseConfig, txtEntityPackage.getText(), txtDaoPackage.getText(), txtDaoName.getText(),
				txtMapPackage.getText(), txtMapName.getText(), txtAssistPackage.getText(), txtAssistName.getText(),
				txtConfigPackage.getText(), txtConfigName.getText(), txtMyUtilPackage.getText(),
				txtMyUtilName.getText());

		if (changeInfo == false) {
			SuperAttribute attr = new SuperAttribute();
			attr.setClassName(txtEntityName.getText());
			attr.setTableName(txtTableName.getText());
			List<AttributeCVF> attributes = null;
			try {
				attributes = DBUtil.getTableColumns(selectedDatabaseConfig, selectedTableName);
				for (AttributeCVF temp : attributes) {
					temp.setPropertyName(StrUtil.unlineToCamel(temp.getPropertyName()));
				}
			} catch (Exception e1) {
				AlertUtil.showErrorAlert("获得属性失败!原因:\r\n" + e1.getMessage());
			}

			String key = null;
			try {
				key = DBUtil.getTablePrimaryKey(selectedDatabaseConfig, selectedTableName);
			} catch (Exception e) {
				AlertUtil.showErrorAlert("获得主键失败!原因:\r\n" + e.getMessage());
			}
			attr.setAttributes(attributes);
			attr.setPrimaryKey(key);
			superAttributes.add(attr);
		} else {
			if (this.superAttributes.get(0)!=null) {
				this.superAttributes.get(0).setClassName(txtEntityName.getText());
			}
		}

		//执行创建
		try {
			factory.create(superAttributes);
			AlertUtil.showInfoAlert("创建完成!");
			changeInfo=false;
		} catch (Exception e) {
			AlertUtil.showErrorAlert("创建失败!原因:\r\n" + e.getMessage());
		}

	}

	/**
	 * 是否创建Assist帮助工具
	 * 
	 * @param event
	 */
	public void onchkAssist(ActionEvent event) {
		showOrHideAssist(chkAssist.isSelected());
	}

	public void showOrHideAssist(boolean param) {
		if (param) {
			txtAssistPackage.disableProperty().set(false);
			txtAssistName.disableProperty().set(false);
			lblAssistPackage.disableProperty().set(false);
			lblAssistName.disableProperty().set(false);
		} else {
			txtAssistPackage.disableProperty().set(true);
			txtAssistName.disableProperty().set(true);
			lblAssistPackage.disableProperty().set(true);
			lblAssistName.disableProperty().set(true);
		}
	}

	/**
	 * 是否创建mybatis配置文件
	 * 
	 * @param event
	 */
	public void onchkMyConfig(ActionEvent event) {
		showOrHideMyConfig(chkConfig.isSelected());
	}

	/**
	 * 是否显示MyConfig相关输入框
	 * 
	 * @param param
	 */
	public void showOrHideMyConfig(boolean param) {
		if (param) {
			txtConfigPackage.disableProperty().set(false);
			txtConfigName.disableProperty().set(false);
			lblConfigPackage.disableProperty().set(false);
			lblConfigName.disableProperty().set(false);
		} else {
			txtConfigPackage.disableProperty().set(true);
			txtConfigName.disableProperty().set(true);
			lblConfigPackage.disableProperty().set(true);
			lblConfigName.disableProperty().set(true);
		}
	}

	/**
	 * 是否创建mybatis帮助工具
	 * 
	 * @param event
	 */
	public void onchkMyUtil(ActionEvent event) {
		showOrHideMyUtil(chkMyUtil.isSelected());
	}

	/**
	 * 是否显示MyUtil相关输入框
	 * 
	 * @param param
	 */
	public void showOrHideMyUtil(boolean param) {
		if (param) {
			txtMyUtilPackage.disableProperty().set(false);
			txtMyUtilName.disableProperty().set(false);
			lblMyUtilPackage.disableProperty().set(false);
			lblMyUtilName.disableProperty().set(false);
		} else {
			txtMyUtilPackage.disableProperty().set(true);
			txtMyUtilName.disableProperty().set(true);
			lblMyUtilPackage.disableProperty().set(true);
			lblMyUtilName.disableProperty().set(true);
		}

	}

	/**
	 * 获得配置信息
	 * 
	 * @return
	 */
	public HistoryConfig getHistoryConfig() {

		String projectPath = txtProjectPath.getText();
		String rootDir = txtRootDir.getText();
		String daoPackage = txtDaoPackage.getText();
		String daoName = txtDaoName.getText();
		String entityPackage = txtEntityPackage.getText();
		String entityName = txtEntityName.getText();
		String mapPackage = txtMapPackage.getText();
		String mapName = txtMapName.getText();
		String assistPackage = txtAssistPackage.getText();
		String assistName = txtAssistName.getText();
		String configPackage = txtConfigPackage.getText();
		String configName = txtConfigName.getText();
		String myUtilPackage = txtMyUtilPackage.getText();
		String myUtilName = txtMyUtilName.getText();
		boolean isAssist = chkAssist.isSelected();
		boolean isConfig = chkConfig.isSelected();
		boolean isMyUtil = chkMyUtil.isSelected();

		HistoryConfig result = new HistoryConfig(projectPath, rootDir, daoPackage, daoName, entityPackage, entityName,
				mapPackage, mapName, assistPackage, assistName, configPackage, configName, myUtilPackage, myUtilName,
				isAssist, isConfig, isMyUtil);

		return result;
	}

	/**
	 * 加载首页配置文件
	 * 
	 * @param name
	 * @throws Exception
	 */
	public void loadIndexConfigInfo(String name) throws Exception {
		HistoryConfig config = ConfigUtil.getHistoryConfigByName(name);
		if (config == null) {
			return;
		}
		txtProjectPath.setText(config.getProjectPath());
		txtRootDir.setText(config.getRootDir());
		txtDaoPackage.setText(config.getDaoPackage());
		txtDaoName.setText(config.getDaoName());
		txtEntityPackage.setText(config.getEntityPackage());
		txtEntityName.setText(config.getEntityName());
		txtMapPackage.setText(config.getMapPackage());
		txtMapName.setText(config.getMapName());
		txtAssistPackage.setText(config.getAssistPackage());
		txtAssistName.setText(config.getAssistName());
		txtConfigPackage.setText(config.getConfigPackage());
		txtConfigName.setText(config.getConfigName());
		txtMyUtilPackage.setText(config.getMyUtilPackage());
		txtMyUtilName.setText(config.getMyUtilName());
		chkAssist.setSelected(config.isAssist());
		showOrHideAssist(config.isAssist());
		chkConfig.setSelected(config.isConfig());
		showOrHideMyConfig(config.isConfig());
		chkMyUtil.setSelected(config.isMyUtil());
		showOrHideMyUtil(config.isMyUtil());

	}
	// -----------------------get/set-------------------------------

	public boolean isFalg() {
		return falg;
	}

	public void setFalg(boolean falg) {
		this.falg = falg;
	}

	public boolean isChangeInfo() {
		return changeInfo;
	}

	public void setChangeInfo(boolean changeInfo) {
		this.changeInfo = changeInfo;
	}

	public List<SuperAttribute> getSuperAttributes() {
		return superAttributes;
	}

	public void addSuperAttributes(SuperAttribute superAttribute) {
		this.superAttributes.add(superAttribute);
	}

	public void setSuperAttributes(List<SuperAttribute> superAttributes) {
		this.superAttributes = superAttributes;
	}

	public Label getLblConnection() {
		return lblConnection;
	}

	public void setLblConnection(Label lblConnection) {
		this.lblConnection = lblConnection;
	}

	public Label getLblConfig() {
		return lblConfig;
	}

	public void setLblConfig(Label lblConfig) {
		this.lblConfig = lblConfig;
	}

	public TreeView<String> getTvDataBase() {
		return tvDataBase;
	}

	public void setTvDataBase(TreeView<String> tvDataBase) {
		this.tvDataBase = tvDataBase;
	}

	public TextField getTxtProjectPath() {
		return txtProjectPath;
	}

	public void setTxtProjectPath(TextField txtProjectPath) {
		this.txtProjectPath = txtProjectPath;
	}

	public TextField getTxtRootDir() {
		return txtRootDir;
	}

	public void setTxtRootDir(TextField txtRootDir) {
		this.txtRootDir = txtRootDir;
	}

	public TextField getTxtTableName() {
		return txtTableName;
	}

	public void setTxtTableName(TextField txtTableName) {
		this.txtTableName = txtTableName;
	}

	public TextField getTxtDaoPackage() {
		return txtDaoPackage;
	}

	public void setTxtDaoPackage(TextField txtDaoPackage) {
		this.txtDaoPackage = txtDaoPackage;
	}

	public TextField getTxtDaoName() {
		return txtDaoName;
	}

	public void setTxtDaoName(TextField txtDaoName) {
		this.txtDaoName = txtDaoName;
	}

	public TextField getTxtEntityPackage() {
		return txtEntityPackage;
	}

	public void setTxtEntityPackage(TextField txtEntityPackage) {
		this.txtEntityPackage = txtEntityPackage;
	}

	public TextField getTxtEntityName() {
		return txtEntityName;
	}

	public void setTxtEntityName(TextField txtEntityName) {
		this.txtEntityName = txtEntityName;
	}

	public TextField getTxtMapPackage() {
		return txtMapPackage;
	}

	public void setTxtMapPackage(TextField txtMapPackage) {
		this.txtMapPackage = txtMapPackage;
	}

	public TextField getTxtMapName() {
		return txtMapName;
	}

	public void setTxtMapName(TextField txtMapName) {
		this.txtMapName = txtMapName;
	}

	public TextField getTxtAssistPackage() {
		return txtAssistPackage;
	}

	public void setTxtAssistPackage(TextField txtAssistPackage) {
		this.txtAssistPackage = txtAssistPackage;
	}

	public TextField getTxtAssistName() {
		return txtAssistName;
	}

	public void setTxtAssistName(TextField txtAssistName) {
		this.txtAssistName = txtAssistName;
	}

	public TextField getTxtConfigPackage() {
		return txtConfigPackage;
	}

	public void setTxtConfigPackage(TextField txtConfigPackage) {
		this.txtConfigPackage = txtConfigPackage;
	}

	public TextField getTxtConfigName() {
		return txtConfigName;
	}

	public void setTxtConfigName(TextField txtConfigName) {
		this.txtConfigName = txtConfigName;
	}

	public TextField getTxtMyUtilPackage() {
		return txtMyUtilPackage;
	}

	public void setTxtMyUtilPackage(TextField txtMyUtilPackage) {
		this.txtMyUtilPackage = txtMyUtilPackage;
	}

	public TextField getTxtMyUtilName() {
		return txtMyUtilName;
	}

	public void setTxtMyUtilName(TextField txtMyUtilName) {
		this.txtMyUtilName = txtMyUtilName;
	}

	public CheckBox getChkAssist() {
		return chkAssist;
	}

	public void setChkAssist(CheckBox chkAssist) {
		this.chkAssist = chkAssist;
	}

	public CheckBox getChkConfig() {
		return chkConfig;
	}

	public void setChkConfig(CheckBox chkConfig) {
		this.chkConfig = chkConfig;
	}

	public CheckBox getChkMyUtil() {
		return chkMyUtil;
	}

	public void setChkMyUtil(CheckBox chkMyUtil) {
		this.chkMyUtil = chkMyUtil;
	}

	public Label getLblAssistName() {
		return lblAssistName;
	}

	public void setLblAssistName(Label lblAssistName) {
		this.lblAssistName = lblAssistName;
	}

	public Label getLblConfigName() {
		return lblConfigName;
	}

	public void setLblConfigName(Label lblConfigName) {
		this.lblConfigName = lblConfigName;
	}

	public Label getLblMyUtilName() {
		return lblMyUtilName;
	}

	public void setLblMyUtilName(Label lblMyUtilName) {
		this.lblMyUtilName = lblMyUtilName;
	}

	public Label getLblAssistPackage() {
		return lblAssistPackage;
	}

	public void setLblAssistPackage(Label lblAssistPackage) {
		this.lblAssistPackage = lblAssistPackage;
	}

	public Label getLblConfigPackage() {
		return lblConfigPackage;
	}

	public void setLblConfigPackage(Label lblConfigPackage) {
		this.lblConfigPackage = lblConfigPackage;
	}

	public Label getLblMyUtilPackage() {
		return lblMyUtilPackage;
	}

	public void setLblMyUtilPackage(Label lblMyUtilPackage) {
		this.lblMyUtilPackage = lblMyUtilPackage;
	}

	public Button getBtnSelectFile() {
		return btnSelectFile;
	}

	public void setBtnSelectFile(Button btnSelectFile) {
		this.btnSelectFile = btnSelectFile;
	}

	public Button getBtnUpdateEntity() {
		return btnUpdateEntity;
	}

	public void setBtnUpdateEntity(Button btnUpdateEntity) {
		this.btnUpdateEntity = btnUpdateEntity;
	}

	public Button getBtnRunCreate() {
		return btnRunCreate;
	}

	public void setBtnRunCreate(Button btnRunCreate) {
		this.btnRunCreate = btnRunCreate;
	}

	public Button getBtnSaveConfig() {
		return btnSaveConfig;
	}

	public void setBtnSaveConfig(Button btnSaveConfig) {
		this.btnSaveConfig = btnSaveConfig;
	}

	public DatabaseConfig getSelectedDatabaseConfig() {
		return selectedDatabaseConfig;
	}

	public void setSelectedDatabaseConfig(DatabaseConfig selectedDatabaseConfig) {
		this.selectedDatabaseConfig = selectedDatabaseConfig;
	}

	public String getSelectedTableName() {
		return selectedTableName;
	}

	public void setSelectedTableName(String selectedTableName) {
		this.selectedTableName = selectedTableName;
	}

}
