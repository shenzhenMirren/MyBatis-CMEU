package pers.cmeu.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.common.CreateFileUtil;
import pers.cmeu.common.DBUtil;
import pers.cmeu.common.StrUtil;
import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.ClassConfig;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.HistoryConfig;
import pers.cmeu.models.SuperAttribute;
import pers.cmeu.view.AlertUtil;

public class IndexController extends BaseController {
	private Logger log = Logger.getLogger(this.getClass());
	// 存储数据库指定数据库,修改属性时用
	private DatabaseConfig selectedDatabaseConfig;
	private DatabaseConfig updateOfDatabaseConfig;
	// 记录存储的表名,修改属性时用
	private String selectedTableName;
	// 标记属性是否修改false为没有修改
	private boolean changeInfo = false;
	// 需要创建所有类的集合;
	private List<SuperAttribute> superAttributes = new ArrayList<SuperAttribute>();
	// 首页默认的属性集
	private SuperAttribute thisSuperAttribute;
	// 确定信息是否需要从新加载信息,需要等于true,不需要等于false
	private boolean falg = true;

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
	private TextField txtServicePackage;
	@FXML
	private TextField txtServiceName;
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
	private TextField txtUpdateMapper;

	@FXML
	private CheckBox chkAssist;
	@FXML
	private CheckBox chkConfig;
	@FXML
	private CheckBox chkMyUtil;
	@FXML
	private CheckBox chkService;
	@FXML
	private CheckBox chkSpringAnno;
	@FXML
	private CheckBox chkFristCreateMybatis;

	@FXML
	private Label lblServicePackage;
	@FXML
	private Label lblServiceName;
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
	private Label lblUpdateMapper;

	@FXML
	private Button btnSelectFile;
	@FXML
	private Button btnUpdateEntity;
	@FXML
	private Button btnRunCreate;
	@FXML
	private Button btnSaveConfig;
	@FXML
	private Button btnSelectMapperFile;

	@FXML
	private ComboBox<String> cboCodeFormat;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.debug("初始化首页...");
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
		cboCodeFormat.getItems().addAll("UTF-8", "GBK", "UTF-16", "UTF-32", "GB2312", "GB18030", "ISO-8859-1");
		cboCodeFormat.setValue("UTF-8");
		log.debug("初始化首页成功!");
		log.debug("加载左侧数据库树与事件....");
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
						log.debug("执行打开数据库连接....");
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
							log.error("打开连接失败!!!" + e);
						} catch (Exception e) {
							AlertUtil.showErrorAlert(e.getMessage());
							log.error("打开连接失败!!!" + e);
						}
					});
					MenuItem item1 = new MenuItem("关闭连接");
					item1.setOnAction(event1 -> {
						treeItem.getChildren().clear();
					});
					MenuItem item3 = new MenuItem("修改连接");
					item3.setOnAction(event1 -> {
						updateOfDatabaseConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
						if (updateOfDatabaseConfig != null) {
							log.debug("打开修改数据库连接窗口...");
							UpdateConnection controller = (UpdateConnection) loadFXMLPage("修改数据库连接",
									FXMLPage.UPDATE_CONNECTION, false);
							controller.setIndexController(this);
							controller.init();
							controller.showDialogStage();

						}
					});
					MenuItem item2 = new MenuItem("删除连接");
					item2.setOnAction(event1 -> {
						if (!AlertUtil.showConfirmAlert("确定删除该连接吗")) {
							return;
						}
						log.debug("执行删除数据库链接...");
						DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
						try {
							ConfigUtil.deleteDatabaseConfig(selectedConfig.getConnName());
							this.loadTVDataBase();
						} catch (Exception e) {
							AlertUtil.showErrorAlert("删除数据库连接失败: " + e.getMessage());
							log.error("删除数据库连接失败!!!" + e);
						}
					});
					contextMenu.getItems().addAll(item0, item1, item3, item2);
					cell.setContextMenu(contextMenu);
				}
				// 加载所有表
				if (event.getClickCount() == 2) {
					if (treeItem == null) {
						return;
					}
					treeItem.setExpanded(true);
					if (level == 1) {
						log.debug("加载所有表....");
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
							log.debug("加载所有表成功!");
						} catch (CommunicationsException e) {
							AlertUtil.showErrorAlert("连接超时");
							log.error("加载所有表失败!!!" + e);
						} catch (Exception e) {
							AlertUtil.showErrorAlert(e.getMessage());
							log.error("加载所有表失败!!!" + e);
						}
					} else if (level == 2) {
						log.debug("将表的数据加载到数据面板...");
						String tableName = treeCell.getTreeItem().getValue();
						selectedDatabaseConfig = (DatabaseConfig) treeItem.getParent().getGraphic().getUserData();
						selectedTableName = tableName;
						txtTableName.setText(tableName);
						txtEntityName.setText(StrUtil.unlineToPascal(tableName));
						txtDaoName.setText(StrUtil.unlineToPascal(tableName) + "Dao");
						txtMapName.setText(StrUtil.unlineToPascal(tableName) + "Mapper");
						txtServiceName.setText(StrUtil.unlineToPascal(tableName) + "Service");
						log.debug("将表的数据加载到数据面板成功!");
					}
				}
			});
			return cell;
		});
		// 加载左边数据库树

		try {
			loadTVDataBase();
			log.debug("加载所有数据库到左侧树集成功!");
		} catch (Exception e1) {
			AlertUtil.showErrorAlert(e1.getMessage());
			log.error("加载所有数据库到左侧树集失败!!!" + e1);
		}
		try {
			// 加载首页配置信息
			log.debug("执行查询默认配置信息并加载到首页...");
			loadIndexConfigInfo("default");
			log.debug("加载配置信息到首页成功!");
		} catch (Exception e) {
			AlertUtil.showErrorAlert("加载配置失败!失败原因:\r\n" + e.getMessage());
			log.error("加载配置信息失败!!!" + e);
		}

	}

	/**
	 * 加载数据库到树集
	 * 
	 * @throws Exception
	 */
	public void loadTVDataBase() throws Exception {
		TreeItem<String> rootTreeItem = tvDataBase.getRoot();
		rootTreeItem.getChildren().clear();
		List<DatabaseConfig> item = null;
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
	}

	/**
	 * 选择项目文件
	 * 
	 * @param event
	 */
	public void selectFile(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File file = directoryChooser.showDialog(super.getPrimaryStage());
		if (file != null) {
			txtProjectPath.setText(file.getPath());
			log.debug("选择文件项目目录:" + file.getPath());
		}
	}

	/**
	 * 选择项目MybatisConfig配置文件所在文件
	 * 
	 * @param event
	 */
	public void selectMybatisFile(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		File file = fileChooser.showOpenDialog(super.getPrimaryStage());
		if (file != null) {
			txtUpdateMapper.setText(file.getPath());
			log.debug("选择更新项目资源的配置文件:" + file.getPath());
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
		// 将本窗口保存添加到管理器
		if (StageManager.CONTROLLER == null) {
			StageManager.CONTROLLER = new HashMap<String, Object>();
		}
		StageManager.CONTROLLER.put("index", this);
		Stage stage = new Stage();
		try {
			log.debug("打开修改属性窗口...");
			Parent root = FXMLLoader
					.load(Thread.currentThread().getContextClassLoader().getResource(FXMLPage.SET_ATTRIBUTE.getFxml()));
			stage.setTitle("修改属性");
			stage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(getPrimaryStage());
			stage.setScene(new Scene(root));
			stage.show();
			// 将本窗口保存添加到管理器
			if (StageManager.STAGE == null) {
				StageManager.STAGE = new HashMap<String, Stage>();
			}
			StageManager.STAGE.put("attribute", stage);
			log.debug("打开修改属性窗口成功!");
		} catch (IOException e) {
			AlertUtil.showErrorAlert("初始化修改属性失败:\r\n原因:" + e.getMessage());
			log.error("初始化修改属性失败!!!" + e);
		}
	}

	/**
	 * 保存配置文件
	 * 
	 * @param event
	 */
	public void saveConfig(ActionEvent event) {
		log.debug("执行保存配置文件...");
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
				log.debug("保存配置成功!");
			} catch (Exception e) {
				AlertUtil.showErrorAlert("保存配置失败!失败原因:\r\n" + e.getMessage());
				log.error("保存配置失败!!!" + e);
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
			AlertUtil.showWarnAlert("项目所在目录以及类名为必填项;\r\n实体类可以通过双击左边树形数据库表加载...");
			return;
		}
		log.debug("准备开始执行创建所有文件...");
		btnRunCreate.setText("创建中...");

		if (changeInfo == false) {
			SuperAttribute attr = new SuperAttribute();
			attr.setClassName(txtEntityName.getText());
			attr.setTableName(txtTableName.getText());
			attr.setDaoName(txtDaoName.getText());
			attr.setMapperName(txtMapName.getText());
			attr.setServiceName(txtServiceName.getText());

			List<AttributeCVF> attributes = null;

			String key = null;
			try {
				log.debug("获取表的主键...");
				key = DBUtil.getTablePrimaryKey(selectedDatabaseConfig, selectedTableName);
			} catch (Exception e) {
				AlertUtil.showErrorAlert("获得主键失败!原因:\r\n" + e.getMessage());
				log.error("获取表主键失败!!!" + e);
			}

			try {
				log.debug("获取表的所有列...");
				attributes = DBUtil.getTableColumns(selectedDatabaseConfig, selectedTableName);
				for (AttributeCVF temp : attributes) {
					temp.setPropertyName(StrUtil.unlineToCamel(temp.getPropertyName()));
				}

			} catch (Exception e1) {
				AlertUtil.showErrorAlert("获得属性失败!原因:\r\n" + e1.getMessage());
				log.error("获得表的所有列失败!!!" + e1);
			}

			try {
				log.debug("初始化创建类配置信息...");
				// 从配置文件中获取配置信息并应用
				ClassConfig classConfig = ConfigUtil.getClassConfig();
				attr.setCreateGetSet(classConfig.isGetAndSet());
				attr.setConstruct(classConfig.isConstruct());
				attr.setConstructAll(classConfig.isConstructAll());
				attr.setCamel(classConfig.isUnlineCamel());
				attr.setSerializable(classConfig.isSeriz());
				attr.setCreateJDBCType(classConfig.isCreateJDBCType());
				log.debug("初始化创建类配置信息-->成功!");
			} catch (Exception e) {
				log.error("初始化创建类配置信息-->失败:" + e);
			}
			attr.setPrimaryKey(key);
			attr.setAttributes(attributes);
			superAttributes.add(attr);
		} else {
			thisSuperAttribute.setTableName(txtTableName.getText());
			thisSuperAttribute.setClassName(txtEntityName.getText());
			thisSuperAttribute.setDaoName(txtDaoName.getText());
			thisSuperAttribute.setMapperName(txtMapName.getText());
			if (chkService.isSelected()) {
				thisSuperAttribute.setServiceName(txtServiceName.getText());
			}
			superAttributes.add(thisSuperAttribute);
		}
		log.debug("初始化创建工具...");
		// 初始化文件工具
		CreateFileUtil fileUtil = CreateFileUtil.getInstance();
		fileUtil.init(selectedDatabaseConfig, superAttributes, cboCodeFormat.getValue(), txtProjectPath.getText(),
				txtRootDir.getText(), txtEntityPackage.getText(), txtDaoPackage.getText(), txtMapPackage.getText(),
				chkService.isSelected(), chkSpringAnno.isSelected(), txtServicePackage.getText(),
				txtUpdateMapper.getText(), chkAssist.isSelected(),
				txtAssistPackage.getText(), txtAssistName.getText(), chkConfig.isSelected(), txtConfigPackage.getText(),
				txtConfigName.getText(), chkMyUtil.isSelected(), txtMyUtilPackage.getText(), txtMyUtilName.getText());

		log.debug("开始执行创建所有文件...");
		// 执行创建
		try {
			fileUtil.createAll();
			AlertUtil.showInfoAlert("创建完成!");
			thisSuperAttribute = null;
			changeInfo = false;
			log.debug("创建所有文件成功!");
		} catch (Exception e) {
			e.printStackTrace();
			AlertUtil.showErrorAlert("创建失败!原因:\r\n" + e.getMessage());
			btnRunCreate.setText("创建失败");
			log.error("创建所有文件失败!!!" + e);
		} finally {
			btnRunCreate.setText("执行创建");
		}

	}

	/**
	 * 是否第一次创建
	 * 
	 * @param event
	 */
	public void onChkFristCreateMybatis(ActionEvent event) {
		shoueOrHideFrist(chkFristCreateMybatis.isSelected());
	}

	public void shoueOrHideFrist(boolean param) {
		if (param) {
			btnSelectMapperFile.disableProperty().set(true);
			txtUpdateMapper.disableProperty().set(true);
			lblUpdateMapper.disableProperty().set(true);
			chkConfig.setSelected(true);
		} else {
			btnSelectMapperFile.disableProperty().set(false);
			txtUpdateMapper.disableProperty().set(false);
			lblUpdateMapper.disableProperty().set(false);
			chkConfig.setSelected(false);
		}
	}

	/**
	 * 是否创建service层
	 * 
	 * @param event
	 */
	public void onchkService(ActionEvent event) {
		showOrHideService(chkService.isSelected());
	}

	public void showOrHideService(boolean param) {
		if (param) {
			txtServiceName.disableProperty().set(false);
			txtServicePackage.disableProperty().set(false);
			lblServicePackage.disableProperty().set(false);
			lblServiceName.disableProperty().set(false);
		} else {
			txtServiceName.disableProperty().set(true);
			txtServicePackage.disableProperty().set(true);
			lblServicePackage.disableProperty().set(true);
			lblServiceName.disableProperty().set(true);
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
			lblAssistPackage.disableProperty().set(false);
			lblAssistName.disableProperty().set(false);
		} else {
			txtAssistPackage.disableProperty().set(true);
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
			chkFristCreateMybatis.disableProperty().set(false);
			chkFristCreateMybatis.setSelected(true);
			shoueOrHideFrist(true);
		} else {
			txtConfigPackage.disableProperty().set(true);
			txtConfigName.disableProperty().set(true);
			lblConfigPackage.disableProperty().set(true);
			lblConfigName.disableProperty().set(true);
			chkFristCreateMybatis.disableProperty().set(true);
			chkFristCreateMybatis.setSelected(false);
			shoueOrHideFrist(false);
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
		String servicePackage = txtServicePackage.getText();
		String serviceName = txtServiceName.getText();
		String entityPackage = txtEntityPackage.getText();
		String entityName = txtEntityName.getText();
		String mapPackage = txtMapPackage.getText();
		String mapName = txtMapName.getText();
		String updateMapper = txtUpdateMapper.getText();
		String assistPackage = txtAssistPackage.getText();
		String assistName = txtAssistName.getText();
		String configPackage = txtConfigPackage.getText();
		String configName = txtConfigName.getText();
		String myUtilPackage = txtMyUtilPackage.getText();
		String myUtilName = txtMyUtilName.getText();
		boolean isService = chkService.isSelected();
		boolean isAssist = chkAssist.isSelected();
		boolean isConfig = chkConfig.isSelected();
		boolean isMyUtil = chkMyUtil.isSelected();
		boolean isSpringAnno = chkSpringAnno.isSelected();
		HistoryConfig result = new HistoryConfig(projectPath, rootDir, daoPackage, daoName, servicePackage, serviceName,
				entityPackage, entityName, mapPackage, mapName, updateMapper,
				assistPackage, assistName, configPackage, configName, myUtilPackage, myUtilName, isService,
				isSpringAnno, isAssist, isConfig, isMyUtil);
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
		txtServicePackage.setText(config.getServicePackage());
		txtServiceName.setText(config.getServiceName());
		txtEntityPackage.setText(config.getEntityPackage());
		txtEntityName.setText(config.getEntityName());
		txtMapPackage.setText(config.getMapPackage());
		txtMapName.setText(config.getMapName());
		txtUpdateMapper.setText(config.getUpdateMapper());
		txtAssistPackage.setText(config.getAssistPackage());
		txtAssistName.setText(config.getAssistName());
		txtConfigPackage.setText(config.getConfigPackage());
		txtConfigName.setText(config.getConfigName());
		txtMyUtilPackage.setText(config.getMyUtilPackage());
		txtMyUtilName.setText(config.getMyUtilName());
		chkService.setSelected(config.isService());
		showOrHideService(config.isService());
		chkSpringAnno.setSelected(config.isSpringAnno());
		chkAssist.setSelected(config.isAssist());
		showOrHideAssist(config.isAssist());
		chkConfig.setSelected(config.isConfig());
		showOrHideMyConfig(config.isConfig());
		chkMyUtil.setSelected(config.isMyUtil());
		showOrHideMyUtil(config.isMyUtil());

	}
	// -----------------------get/set-------------------------------

	public String getTableName() {
		return txtTableName.getText();
	}

	public SuperAttribute getThisSuperAttribute() {
		return thisSuperAttribute;
	}

	public void setThisSuperAttribute(SuperAttribute thisSuperAttribute) {
		this.thisSuperAttribute = thisSuperAttribute;
	}

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

	public DatabaseConfig getUpdateOfDatabaseConfig() {
		return updateOfDatabaseConfig;
	}

	public void setUpdateOfDatabaseConfig(DatabaseConfig updateOfDatabaseConfig) {
		this.updateOfDatabaseConfig = updateOfDatabaseConfig;
	}

}
