package pers.cmeu.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.common.DBUtil;
import pers.cmeu.common.StrUtil;
import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.ClassConfig;
import pers.cmeu.models.ColumnItem;
import pers.cmeu.models.SuperAttribute;
import pers.cmeu.view.AlertUtil;

public class AddSonAttributeController extends BaseController {
	private Logger log = Logger.getLogger(AddSonAttributeController.class.getName());
	// 存储信息table里面的所有属性
	ObservableList<AttributeCVF> attributeCVF;

	@FXML
	private CheckBox chkUnlineCamel;
	@FXML
	private CheckBox chkSerializable;
	@FXML
	private CheckBox chkCreateJDBCtype;
	@FXML
	private CheckBox chkGetAndSet;
	@FXML
	private CheckBox chkConstruct;
	@FXML
	private CheckBox chkConstructAll;
	@FXML
	private CheckBox chkCreateEntity;
	@FXML
	private CheckBox chkCreateDao;
	@FXML
	private CheckBox chkCreateMap;
	@FXML
	private CheckBox chkCreateService;
	@FXML
	private CheckBox chkCreateServiceImpl;
	@FXML
	private CheckBox chkCreateAll;

	@FXML
	private TextField txtPrimaryKey;
	@FXML
	private TextField txtCustomType;
	@FXML
	private TextField txtCustomName;
	@FXML
	private TextField txtTableName;
	@FXML
	private TextField txtTableAlias;
	@FXML
	private TextField txtClassName;
	@FXML
	private TextField txtDaoName;
	@FXML
	private TextField txtMapperName;
	@FXML
	private TextField txtServiceName;
	@FXML
	private TextField txtServiceImplName;
	@FXML
	private TextField txtJoinTableName;
	@FXML
	private TextField txtJoinColumnName;

	@FXML
	private Button btnSuccess;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnAddToTableView;
	@FXML
	private Button btnAddItem;
	@FXML
	private Button btnAddProperty;
	@FXML
	public TableView<AttributeCVF> tblEntityProperty;
	@FXML
	private TableColumn<AttributeCVF, Boolean> tdCheck;
	@FXML
	private TableColumn<AttributeCVF, String> tdColumn;
	@FXML
	private TableColumn<AttributeCVF, String> tdJDBCType;
	@FXML
	private TableColumn<AttributeCVF, String> tdJAVAType;
	@FXML
	private TableColumn<AttributeCVF, String> tdPropertyName;

	// 主键策略
	@FXML
	private TextArea txtaSelectKey;
	@FXML
	private Label lblSelectKey;
	@FXML
	private CheckBox chkSelectKey;

	@FXML
	private ListView<Label> lvTableList;

	@FXML
	private ToggleGroup joinType;
	@FXML
	private RadioButton radioInner;
	@FXML
	private RadioButton radioLeft;
	@FXML
	private RadioButton radioRight;
	@FXML
	private RadioButton radioWhere;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.debug("初始化第二级新表属性窗口...");
		tblEntityProperty.setEditable(true);
		tblEntityProperty.setStyle("-fx-font-size:14px");
		tblEntityProperty.setPlaceholder(new Label("双击左边表名数据加载..."));
		initListItem();
		radioInner.setUserData("inner");
		radioLeft.setUserData("left");
		radioRight.setUserData("right");
		radioWhere.setUserData("where");
		log.debug("初始化第二级新表属性窗口成功!");
	}

	/**
	 * 加载左边list所有表名
	 */
	public void initListItem() {
		try {
			log.debug("执行加载左侧所有表...");
			List<String> tableNames = DBUtil.getTableNames(
					((IndexController) StageManager.CONTROLLER.get("index")).getSelectedDatabaseConfig());

			for (String str : tableNames) {
				ImageView imageView = new ImageView("pers/resource/image/table.png");
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				Label label = new Label(str);
				label.setGraphic(imageView);
				label.setUserData(str);
				label.setPrefWidth(lvTableList.getPrefWidth());
				label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getClickCount() == 2) {
						// 双击显示表名与主键,同时加载到tableview
						log.debug("执行将选择中的表加载到属性表格中...");
						try {
							String tableName = label.getUserData().toString();
							txtTableName.setText(tableName);
							txtClassName.setText(StrUtil.unlineToPascal(tableName));
							txtDaoName.setText(StrUtil.unlineToPascal(tableName) + "Dao");
							txtMapperName.setText(StrUtil.unlineToPascal(tableName) + "Mapper");
							txtServiceName.setText(StrUtil.unlineToPascal(tableName) + "Service");
							txtServiceImplName.setText(StrUtil.unlineToPascal(tableName) + "ServiceImpl");
							String primaryKey = DBUtil
									.getTablePrimaryKey(((IndexController) StageManager.CONTROLLER.get("index"))
											.getSelectedDatabaseConfig(), tableName);
							if (primaryKey == null || "".equals(primaryKey)) {
								txtPrimaryKey.setText(null);
								txtPrimaryKey.setPromptText("注意:该表没有主键!");
							} else {
								txtPrimaryKey.setText(primaryKey);
								String tmpTb = ((IndexController) StageManager.CONTROLLER.get("index")).getTableName();
								String tmp = ((SetAttributeController) StageManager.CONTROLLER.get("attribute"))
										.getPrimaryKey();
								if (tmpTb != null) {
									txtJoinTableName.setText(tmpTb);
								}
								if (tmp != null) {
									txtJoinColumnName.setText(tmp);
								}
							}
							if (tableName != null) {
								// 初始化表属性
								log.debug("初始化属性表格...");
								initTable();
								log.debug("初始化属性表格成功!");
							}
							log.debug("将选择中的表加载到属性表格中成功!");
						} catch (Exception e) {
							AlertUtil.showErrorAlert("加载失败!失败原因:\r\n" + e.getMessage());
							log.error("将选择中的表加载到属性表格中失败!!!" + e);
						}

					}
				});
				lvTableList.getItems().add(label);
			}

			log.debug("加载左侧所有表成功!");
		} catch (Exception e) {
			AlertUtil.showErrorAlert("获得子表失败!原因:" + e.getMessage());
			log.debug("加载左侧所有表失败!!!" + e);
		}
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
	 * 初始化右边的表
	 * 
	 * @throws Exception
	 */
	public void initTable() throws Exception {
		// 获得工厂数据

		attributeCVF = getAttributeCVFs();

		tdCheck.setCellFactory(CheckBoxTableCell.forTableColumn(tdCheck));
		tdCheck.setCellValueFactory(new PropertyValueFactory<>("check"));

		tdColumn.setCellValueFactory(new PropertyValueFactory<>("conlumn"));
		tdJDBCType.setCellValueFactory(new PropertyValueFactory<>("jdbcType"));

		tdJAVAType.setCellValueFactory(new PropertyValueFactory<>("javaType"));

		tdPropertyName.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
		tdPropertyName.setCellFactory(TextFieldTableCell.forTableColumn());
		tdPropertyName.setOnEditCommit(event -> {
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setPropertyName(event.getNewValue());
		});

		// 是否将字符驼峰命名;
		if (chkUnlineCamel.isSelected()) {
			toCamel();
		} else {
			notCamel();
		}
	}

	/**
	 * 获得数据库列并初始化
	 * 
	 * @return
	 * @throws Exception
	 */
	public ObservableList<AttributeCVF> getAttributeCVFs() throws Exception {
		List<AttributeCVF> attributeCVFs = DBUtil.getTableColumns(
				((IndexController) StageManager.CONTROLLER.get("index")).getSelectedDatabaseConfig(),
				txtTableName.getText());
		return FXCollections.observableList(attributeCVFs);
	}

	/**
	 * 是否将java属性设置为驼峰命名
	 * 
	 * @param event
	 */
	public void unlineCamel(ActionEvent event) {
		if (chkUnlineCamel.isSelected()) {
			toCamel();
		} else {
			notCamel();
		}
	}

	/**
	 * 设置属性为帕斯卡
	 */
	public void toCamel() {
		tblEntityProperty.getItems().clear();
		for (AttributeCVF attr : attributeCVF) {
			attr.setPropertyName(StrUtil.unlineToCamel(attr.getPropertyName()));
			tblEntityProperty.getItems().add(attr);
		}
	}

	/**
	 * 设置属性名跟列名相同
	 */
	public void notCamel() {
		ObservableList<AttributeCVF> item = attributeCVF;
		tblEntityProperty.getItems().clear();
		for (AttributeCVF attr : item) {
			if (attr.getConlumn() == null || "".equals(attr.getConlumn())) {
				attr.setPropertyName(StrUtil.fristToLoCase(attr.getPropertyName()));
			} else {
				attr.setPropertyName(StrUtil.fristToLoCase(attr.getConlumn()));
			}
			tblEntityProperty.getItems().add(attr);
		}

	}

	/**
	 * 是否全部创建
	 * 
	 * @param event
	 */
	public void anyCreateAll(ActionEvent event) {
		if (!chkCreateEntity.isSelected() || !chkCreateDao.isSelected() || !chkCreateMap.isSelected()
				|| !chkCreateService.isSelected() || !chkCreateServiceImpl.isSelected()) {
			chkCreateAll.setSelected(true);
		}
		boolean value = chkCreateAll.isSelected();
		chkCreateEntity.setSelected(value);
		chkCreateDao.setSelected(value);
		chkCreateMap.setSelected(value);
		chkCreateService.setSelected(value);
		chkCreateServiceImpl.setSelected(value);
	}

	/**
	 * 将属性添加到属性表
	 */
	public void addToTable(ActionEvent event) {
		log.debug("执行添加自定义属性...");
		AttributeCVF attribute = new AttributeCVF();
		attribute.setJavaType(txtCustomType.getText());
		attribute.setPropertyName(txtCustomName.getText());
		this.attributeCVF.add(attribute);
		tblEntityProperty.getItems().add(attribute);
		log.debug("添加自定义属性成功!");
	}

	/**
	 * 生成主键策略
	 * 
	 * @param event
	 */
	public void selectKey(ActionEvent event) {
		if (txtPrimaryKey.getText() == null || "".equals(txtPrimaryKey.getText())) {
			AlertUtil.showWarnAlert("你尚未选择表或者你所选择的表没有主键");
			chkSelectKey.selectedProperty().set(false);
			return;
		}
		if (chkSelectKey.isSelected()) {
			log.debug("执行添加主键策略...");
		} else {
			log.debug("取消添加主键策略...");
		}
		String keyType = "";
		String keyProperty = "";
		for (AttributeCVF attr : tblEntityProperty.getItems()) {
			if (attr.getConlumn().equals(txtPrimaryKey.getText())) {
				keyType = attr.getJavaTypeValue();
				keyProperty = attr.getPropertyName();
				break;
			}
		}
		String dbType = ((IndexController) StageManager.CONTROLLER.get("index")).getSelectedDatabaseConfig()
				.getDbType();
		StringBuffer res = new StringBuffer();
		res.append("        <selectKey keyProperty=\"" + keyProperty + "\" resultType=\"" + keyType + "\" ");
		if ("MySQL".equals(dbType)) {
			res.append("order=\"AFTER\">\r\n            SELECT LAST_INSERT_ID() AS " + keyProperty);
		} else if ("SqlServer".equals(dbType)) {
			res.append("order=\"AFTER\">\r\n            SELECT SCOPE_IDENTITY() AS " + keyProperty);
		} else if ("PostgreSQL".equals(dbType)) {
			res.append("order=\"BEFORE\">\r\n            SELECT nextval() AS " + keyProperty);
		} else {
			res.append("order=\"BEFORE\">\r\n            SELECT .Nextval FROM dual");
		}
		res.append("\r\n        </selectKey>");
		txtaSelectKey.setText(res.toString());
		lblSelectKey.setVisible(chkSelectKey.isSelected());
		txtaSelectKey.setVisible(chkSelectKey.isSelected());
		if (chkSelectKey.isSelected()) {
			log.debug("添加主键策略成功!");
		} else {
			log.debug("取消添加主键策略成功!");
		}
	}

	private boolean anyOpenPro = true;// 用于作为判断打开添加属性(true)还是添加集合(false)

	/**
	 * 添加新表作为属性
	 */
	public void addProperty() {
		if (attributeCVF == null) {
			AlertUtil.showWarnAlert("当前窗体并未加载任何信息,请先操作当前窗体加载表,设置类");
			return;
		}
		anyOpenPro = true;
		StageManager.CONTROLLER.put("addPropertyBySon", this);
		Stage stage = new Stage();
		try {
			log.debug("打开添加第三级添加新表作为属性窗口...");
			Parent root = FXMLLoader.load(
					Thread.currentThread().getContextClassLoader().getResource(FXMLPage.ADD_GRAND_ATTRIBUTE.getFxml()));
			stage.setTitle("添加新表");
			stage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(root));
			stage.show();
			StageManager.STAGE.put("addPropertyByGrand", stage);
		} catch (IOException e) {
			AlertUtil.showErrorAlert("初始化添加属性失败:\r\n原因:" + e.getMessage());
			log.error("打开添加第三级添加新表作为属性窗口失败!!!" + e);
		}

	}

	/**
	 * 添加新表作为集合
	 */
	public void addPropertyItem() {
		if (attributeCVF == null) {
			AlertUtil.showWarnAlert("当前窗体并未加载任何信息,请先操作当前窗体加载表,设置类");
			return;
		}
		anyOpenPro = false;
		StageManager.CONTROLLER.put("addPropertyBySon", this);
		Stage stage = new Stage();
		try {
			log.debug("打开添加第三级添加新表作为集合窗口...");
			Parent root = FXMLLoader.load(
					Thread.currentThread().getContextClassLoader().getResource(FXMLPage.ADD_GRAND_ATTRIBUTE.getFxml()));
			stage.setTitle("添加新表");
			stage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(root));
			stage.show();
			StageManager.STAGE.put("addPropertyByGrand", stage);
		} catch (IOException e) {
			AlertUtil.showErrorAlert("初始化添加属性失败:\r\n原因:" + e.getMessage());
			log.error("打开添加第二级添加新表作为集合窗口失败!!!" + e);
		}
	}

	/**
	 * 取消
	 * 
	 * @param event
	 */
	public void cancel(ActionEvent event) {
		StageManager.STAGE.get("addPropertyBySon").close();
		StageManager.STAGE.remove("addPropertyBySon");
		log.debug("取消添加第二级关系...");
	}

	/**
	 * 确定
	 * 
	 * @param event
	 */
	public void success(ActionEvent event) {
		if (txtTableName != null && !("".equals(txtTableName.getText()))) {
			SetAttributeController addAttr = (SetAttributeController) StageManager.CONTROLLER.get("attribute");
			if (chkCreateEntity.isSelected()) {
				// 将信息存进首页等待创建
				IndexController index = (IndexController) StageManager.CONTROLLER.get("index");
				SuperAttribute attr = new SuperAttribute();
				attr.setClassName(txtClassName.getText());
				if (chkCreateDao.isSelected()) {
					attr.setDaoName(txtDaoName.getText());
				}
				if (chkCreateMap.isSelected()) {
					attr.setMapperName(txtMapperName.getText());
				}
				if (chkCreateService.isSelected()) {
					attr.setServiceName(txtServiceName.getText());
				}
				if (chkCreateServiceImpl.isSelected()) {
					attr.setServiceImplName(txtServiceImplName.getText());
				}
				if (chkSelectKey.isSelected()) {
					attr.setSelectKey(txtaSelectKey.getText());
				}
				if (txtTableAlias != null && !(txtTableAlias.getText().isEmpty())) {
					attr.setTableAlias(txtTableAlias.getText());
				}
				attr.setJoinType(joinType.getSelectedToggle().getUserData().toString());
				attr.setJoinColumn(txtJoinColumnName.getText());
				attr.setTableName(txtTableName.getText());
				attr.setPrimaryKey(txtPrimaryKey.getText());
				attr.setCamel(chkUnlineCamel.isSelected());
				attr.setSerializable(chkSerializable.isSelected());
				attr.setCreateJDBCType(chkCreateJDBCtype.isSelected());
				attr.setCreateGetSet(chkGetAndSet.isSelected());
				attr.setConstruct(chkConstruct.isSelected());
				attr.setConstructAll(chkConstructAll.isSelected());
				attr.setAttributes(tblEntityProperty.getItems());
				if (!addAttr.isAnyOpenPro()) {
					attr.setAnyHasColl(true);
				}
				List<ColumnItem> items = new ArrayList<ColumnItem>();
				for (AttributeCVF item : tblEntityProperty.getItems()) {
					if (item.getColumnItem() == null) {
						continue;
					}
					if (item.getCheck()) {
						items.add(item.getColumnItem());
					}
				}
				if (items.size() > 0) {
					attr.setColumnItems(items);
				}
				index.addSuperAttributes(attr);
			}
			// 将属性添加到上一级
			AttributeCVF attribute = new AttributeCVF();
			String tmpType = txtClassName.getText();
			if (!addAttr.isAnyOpenPro()) {
				tmpType = "java.util.List<" + tmpType + ">";
			}
			attribute.setJavaType(tmpType);
			attribute.setPropertyName(StrUtil.fristToLoCase(txtClassName.getText()));
			// 添加列集给mapper
			ColumnItem item = new ColumnItem();
			item.setAttributeCVFs(this.tblEntityProperty.getItems());
			item.setClassName(txtClassName.getText());
			item.setInPropertyName(StrUtil.fristToLoCase(txtClassName.getText()));
			item.setAnyAssociation(addAttr.isAnyOpenPro());
			item.setTableName(txtTableName.getText());
			item.setTableAlias(txtTableAlias.getText());
			item.setPrimaryKey(txtPrimaryKey.getText());
			item.setJoinTableName(txtJoinTableName.getText());
			item.setJoinColumn(txtJoinColumnName.getText());
			item.setJoinType(joinType.getSelectedToggle().getUserData().toString());
			List<ColumnItem> tempList = new ArrayList<ColumnItem>();
			// 遍历取出孙代column集合
			for (AttributeCVF cvf : tblEntityProperty.getItems()) {
				if (cvf.getColumnItem() != null) {
					tempList.add(cvf.getColumnItem());
				}
			}
			if (tempList.size() > 0) {
				item.setGrandItem(tempList);
			}
			attribute.setColumnItem(item);
			addAttr.setNeedOrNotCreatePages(addAttr.getNeedOrNotCreatePages() + 1);
			addAttr.attributeCVF.add(attribute);
			addAttr.tblEntityProperty.getItems().add(attribute);
		}
		StageManager.STAGE.get("addPropertyBySon").close();
		StageManager.STAGE.remove("addPropertyBySon");
		log.debug("确定添加第二级关系...");
	}

	// -----------------------get/set--------------------------------

	public String getPrimaryKey() {
		return txtPrimaryKey.getText();
	}

	public boolean isAnyOpenPro() {
		return anyOpenPro;
	}

	public String getTableName() {
		return txtTableName.getText();
	}

	public void setAnyOpenPro(boolean anyOpenPro) {
		this.anyOpenPro = anyOpenPro;
	}
}
