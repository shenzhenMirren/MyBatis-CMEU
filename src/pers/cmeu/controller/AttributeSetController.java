package pers.cmeu.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import pers.cmeu.common.DBUtil;
import pers.cmeu.common.StrUtil;
import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.SuperAttribute;
import pers.cmeu.view.AlertUtil;

public class AttributeSetController extends BaseController {
	private IndexController indexController;

	// 存储信息table里面的所有属性
	ObservableList<AttributeCVF> attributeCVF;
	// 存储数据库指定数据库,修改属性时用
	private DatabaseConfig selectedDatabaseConfig;
	// 记录存储的表名,修改属性时用
	private String selectedTableName;
	// 需要创建所有类的集合;
	private List<SuperAttribute> superAttributes = new ArrayList<SuperAttribute>();

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
	private TextField txtPrimaryKey;
	@FXML
	private TextField txtCustomType;
	@FXML
	private TextField txtCustomName;

	@FXML
	private Button btnSuccess;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnAddTable;
	@FXML
	private Button btnAddToTableView;

	@FXML
	private TableView<AttributeCVF> tblEntityProperty;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tblEntityProperty.setEditable(true);
		tblEntityProperty.setStyle("-fx-font-size:14px");
		tblEntityProperty.setPlaceholder(new Label("正在加载属性..."));

	}

	/**
	 * 由于控制与窗体关联的原因,无法在初始化窗口完成传参,所以需要在index控制器启动本窗体后再调用该方法初始化窗体
	 */
	public void initTable(boolean falg) {

		// 初始化主键名称
		initTablePrimaryKey();
		if (falg == false) {
			// 获得工厂数据
			attributeCVF = getAttributeCVFs(selectedDatabaseConfig, selectedTableName);
		} else {
			attributeCVF = getAttributeCVFsByIndex();
		}

		tdCheck.setCellFactory(CheckBoxTableCell.forTableColumn(tdCheck));
		tdCheck.setCellValueFactory(new PropertyValueFactory<>("check"));

		tdColumn.setCellValueFactory(new PropertyValueFactory<>("conlumn"));
		tdJDBCType.setCellValueFactory(new PropertyValueFactory<>("jdbcType"));
		tdJDBCType.setCellFactory(TextFieldTableCell.forTableColumn());
		tdJDBCType.setOnEditCommit(event -> {
			if ("".equals(event.getNewValue())) {
				event.getTableView().getItems().get(event.getTablePosition().getRow()).setJdbcType(null);
			} else {
				event.getTableView().getItems().get(event.getTablePosition().getRow()).setJdbcType(event.getNewValue());
			}
		});
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
		indexController.setFalg(true);

	}

	/**
	 * 初始化主键
	 */
	public void initTablePrimaryKey() {
		try {
			String key = DBUtil.getTablePrimaryKey(selectedDatabaseConfig, selectedTableName);
			if (key != null) {
				txtPrimaryKey.setText(key);
			} else {
				txtPrimaryKey.setPromptText("注意:你选择的表没有主键!");
			}
		} catch (Exception e) {
			AlertUtil.showErrorAlert("获得主键失败!失败原因:\r\n" + e.getMessage());
		}

	}

	/**
	 * 获得数据库列并初始化
	 * 
	 * @return
	 */
	public ObservableList<AttributeCVF> getAttributeCVFs(DatabaseConfig config, String tableName) {
		ObservableList<AttributeCVF> result = null;

		try {
			List<AttributeCVF> attributeCVFs = DBUtil.getTableColumns(config, tableName);
			result = FXCollections.observableList(attributeCVFs);
		} catch (Exception e) {
			AlertUtil.showErrorAlert("加载属性列失败!失败原因:\r\n" + e.getMessage());
		}

		return result;
	}

	/**
	 * 获取现有的数据
	 * 
	 * @return
	 */
	public ObservableList<AttributeCVF> getAttributeCVFsByIndex() {
		ObservableList<AttributeCVF> result = null;
		if (indexController.getSuperAttributes().size() > 0) {
			result = FXCollections.observableList(indexController.getSuperAttributes().get(0).getAttributes());
		} else {
			result = getAttributeCVFs(selectedDatabaseConfig, selectedTableName);
		}
		return result;
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
		if (attributeCVF == null) {
			return;
		}
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
		if (attributeCVF == null) {
			return;
		}
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
	 * 将属性添加到属性表
	 */
	public void addToTable(ActionEvent event) {
		AttributeCVF attribute = new AttributeCVF();
		attribute.setJavaType(txtCustomType.getText());
		attribute.setPropertyName(txtCustomName.getText());
		this.attributeCVF.add(attribute);
		tblEntityProperty.getItems().add(attribute);
	}

	/**
	 * 取消关闭该窗口
	 * 
	 * @param event
	 */
	public void cancel(ActionEvent event) {
		boolean result = AlertUtil.showConfirmAlert("取消的话你全部的设置都不生效,确定取消吗?");
		if (result) {
			// 表示不修改任何属性
			indexController.setChangeInfo(false);
			closeDialogStage();
		}

	}

	/**
	 * 确定
	 * 
	 * @param event
	 */
	public void success(ActionEvent event) {
		List<AttributeCVF> item = tblEntityProperty.getItems();

		// 获得表里的属性类名在index控制层获取获取
		SuperAttribute attr = new SuperAttribute();
		attr.setCamel(chkUnlineCamel.isSelected());
		attr.setSerializable(chkSerializable.isSelected());
		attr.setCreateJDBCType(chkCreateJDBCtype.isSelected());
		attr.setCreateGetSet(chkGetAndSet.isSelected());
		attr.setConstruct(chkConstruct.isSelected());
		attr.setConstructAll(chkConstructAll.isSelected());
		attr.setTableName(selectedTableName);
		attr.setPrimaryKey(txtPrimaryKey.getText());
		attr.setAttributes(tblEntityProperty.getItems());
		this.superAttributes.add(attr);
		for (AttributeCVF tblAttr : item) {
			if (tblAttr.getJavaType().getUserData() != null) {
				this.superAttributes.add((SuperAttribute) tblAttr.getJavaType().getUserData());
			}
		}
		// 将该集合转交给index
		indexController.setSuperAttributes(this.superAttributes);

		// 表示已经修改过属性
		indexController.setChangeInfo(true);
		closeDialogStage();
	}

	/**
	 * 打开添加子表
	 * 
	 * @param event
	 */
	public void openAddTable(ActionEvent event) {
		/*
		 * AddMoreAttributeController controller = (AddMoreAttributeController)
		 * loadFXMLPage("添加更多属性", FXMLPage.ADD_MORE_ATTRIBUTE, false);
		 * controller.setAttributeSetController(this);
		 * controller.showDialogStage();
		 * controller.setDatabaseConfig(selectedDatabaseConfig);
		 * controller.initListItem();
		 */

		initChil();
		initChilListItem();

		paneChil.setVisible(true);

	}

	// -----------------------chil 区域------------------------------
	// 存储信息table里面的所有属性
	ObservableList<AttributeCVF> chilAttributeCVF;

	@FXML
	private AnchorPane paneChil;
	@FXML
	private CheckBox chkUnlineCamelChil;
	@FXML
	private CheckBox chkSerializableChil;
	@FXML
	private CheckBox chkCreateJDBCtypeChil;
	@FXML
	private CheckBox chkGetAndSetChil;
	@FXML
	private CheckBox chkConstructChil;
	@FXML
	private CheckBox chkConstructAllChil;

	@FXML
	private TextField txtPrimaryKeyChil;
	@FXML
	private TextField txtCustomTypeChil;
	@FXML
	private TextField txtCustomNameChil;
	@FXML
	private TextField txtTableNameChil;
	@FXML
	private TextField txtClassNameChil;

	@FXML
	private Button btnSuccessChilChil;
	@FXML
	private Button btnAddToTableViewChil;
	@FXML
	private Button btnAddParentToTableChil;
	@FXML
	private TableView<AttributeCVF> tblEntityPropertyChil;
	@FXML
	private TableColumn<AttributeCVF, Boolean> tdCheckChil;
	@FXML
	private TableColumn<AttributeCVF, String> tdColumnChil;
	@FXML
	private TableColumn<AttributeCVF, String> tdJDBCTypeChil;
	@FXML
	private TableColumn<AttributeCVF, String> tdJAVATypeChil;
	@FXML
	private TableColumn<AttributeCVF, String> tdPropertyNameChil;

	@FXML
	private ListView<Label> lvTableListChil;

	/**
	 * 初始化子表
	 */
	public void initChil() {
		tblEntityPropertyChil.setEditable(true);
		tblEntityPropertyChil.setStyle("-fx-font-size:14px");
		tblEntityPropertyChil.setPlaceholder(new Label("←双击左边表名数据加载..."));

	}

	/**
	 * 加载子表左边list所有表名
	 */
	public void initChilListItem() {
		try {
			List<String> tableNames = DBUtil.getTableNames(selectedDatabaseConfig);

			for (String str : tableNames) {
				ImageView imageView = new ImageView("pers/resource/image/table.png");
				imageView.setFitHeight(18);
				imageView.setFitWidth(18);
				Label label = new Label(str);
				label.setGraphic(imageView);
				label.setUserData(str);
				label.setPrefWidth(lvTableListChil.getPrefWidth());
				label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getClickCount() == 2) {
						// 双击显示表名与主键,同时加载到tableview
						try {
							String tableName = label.getUserData().toString();
							txtTableNameChil.setText(tableName);
							txtClassNameChil.setText(StrUtil.unlineToPascal(tableName));
							String primaryKey = DBUtil.getTablePrimaryKey(selectedDatabaseConfig, tableName);
							if (primaryKey == null || "".equals(primaryKey)) {
								txtPrimaryKeyChil.setText(null);
								txtPrimaryKeyChil.setPromptText("注意:该表没有主键!");
							} else {
								txtPrimaryKeyChil.setText(primaryKey);
							}
							if (tableName != null) {
								// 初始化表属性
								initChilTable();
							}

						} catch (Exception e) {
							AlertUtil.showErrorAlert("加载失败!失败原因:\r\n" + e.getMessage());
						}

					}
				});
				lvTableListChil.getItems().add(label);
			}

		} catch (Exception e) {
			AlertUtil.showErrorAlert("获得子表失败!原因:" + e.getMessage());
		}
	}

	/**
	 * 初始化子表
	 */
	public void initChilTable() {
		chilAttributeCVF = getAttributeCVFs(selectedDatabaseConfig, txtTableNameChil.getText());
		tdCheckChil.setCellFactory(CheckBoxTableCell.forTableColumn(tdCheck));
		tdCheckChil.setCellValueFactory(new PropertyValueFactory<>("check"));

		tdColumnChil.setCellValueFactory(new PropertyValueFactory<>("conlumn"));
		tdJDBCTypeChil.setCellValueFactory(new PropertyValueFactory<>("jdbcType"));

		tdJAVATypeChil.setCellValueFactory(new PropertyValueFactory<>("javaType"));

		tdPropertyNameChil.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
		tdPropertyNameChil.setCellFactory(TextFieldTableCell.forTableColumn());
		tdPropertyNameChil.setOnEditCommit(event -> {
			event.getTableView().getItems().get(event.getTablePosition().getRow()).setPropertyName(event.getNewValue());
		});
		// 初始化子表
		if (chkUnlineCamelChil.isSelected()) {
			toCamelByChil();
		} else {
			notCamelByChil();
		}
	}

	/**
	 * 去下划线
	 */
	public void unlineCamelChil() {
		if (chkUnlineCamelChil.isSelected()) {
			toCamelByChil();
		} else {
			notCamelByChil();
		}
	}

	/**
	 * 设置属性为帕斯卡
	 */
	public void toCamelByChil() {
		if (chilAttributeCVF == null) {
			return;
		}
		tblEntityPropertyChil.getItems().clear();
		for (AttributeCVF attr : chilAttributeCVF) {
			attr.setPropertyName(StrUtil.unlineToCamel(attr.getPropertyName()));
			tblEntityPropertyChil.getItems().add(attr);
		}
	}

	/**
	 * 设置属性名跟列名相同
	 */
	public void notCamelByChil() {
		if (chilAttributeCVF == null) {
			return;
		}
		ObservableList<AttributeCVF> item = chilAttributeCVF;
		tblEntityPropertyChil.getItems().clear();
		for (AttributeCVF attr : item) {
			if (attr.getConlumn() == null || "".equals(attr.getConlumn())) {
				attr.setPropertyName(StrUtil.fristToLoCase(attr.getPropertyName()));
			} else {
				attr.setPropertyName(StrUtil.fristToLoCase(attr.getConlumn()));
			}
			tblEntityPropertyChil.getItems().add(attr);
		}

	}

	/**
	 * 添加到table
	 */
	public void addToTableChil() {
		AttributeCVF attribute = new AttributeCVF();
		attribute.setJavaType(txtCustomTypeChil.getText());
		attribute.setPropertyName(txtCustomNameChil.getText());
		this.chilAttributeCVF.add(attribute);
		tblEntityPropertyChil.getItems().add(attribute);
	}

	/**
	 * 将父类添加到子表中作为数学
	 */
	public void addParentToTableChil() {
		AttributeCVF attribute = new AttributeCVF();
		attribute.setJavaType(indexController.getTxtEntityName().getText());
		attribute.setPropertyName(StrUtil.unlineToCamel(indexController.getTxtEntityName().getText()));
		this.chilAttributeCVF.add(attribute);
		tblEntityPropertyChil.getItems().add(attribute);
	}

	/**
	 * 确定的事件 将子表添加到父的表中,并关闭显示子表
	 */
	public void chilSuccess() {
		if (txtTableNameChil.getText() == null || txtClassNameChil.getText() == null
				|| "".equals(txtTableNameChil.getText()) || "".equals(txtClassNameChil.getText())) {
			boolean result = AlertUtil.showConfirmAlert("您并没有添加任何子表...确定退出本页面吗?");
			if (result) {
				paneChil.setVisible(false);
			}
		} else {
			SuperAttribute attr = new SuperAttribute();
			attr.setCamel(chkUnlineCamelChil.isSelected());
			attr.setSerializable(chkSerializableChil.isSelected());
			attr.setCreateJDBCType(chkCreateJDBCtypeChil.isSelected());
			attr.setCreateGetSet(chkGetAndSetChil.isSelected());
			attr.setConstruct(chkConstructChil.isSelected());
			attr.setConstructAll(chkConstructAllChil.isSelected());
			attr.setTableName(txtTableNameChil.getText());
			attr.setClassName(txtClassNameChil.getText());
			attr.setPrimaryKey(txtPrimaryKeyChil.getText());
			List<AttributeCVF> cvfs = new ArrayList<AttributeCVF>();
			for (AttributeCVF attri : tblEntityPropertyChil.getItems()) {
				AttributeCVF cvf = new AttributeCVF();
				cvf.setCheck(attri.getCheck());
				cvf.setConlumn(attri.getConlumn());
				cvf.setJdbcType(attri.getJdbcType());
				cvf.setJavaType(attri.getJavaTypeValue());
				cvf.setPropertyName(attri.getPropertyName());
				cvfs.add(cvf);
			}

			attr.setAttributes(cvfs);

			// 添加到父类table
			AttributeCVF attribute = new AttributeCVF();
			attribute.setJavaType("java.util.List<" + txtClassNameChil.getText() + ">");
			// 将子表的属性保存在父类的表里
			attribute.getJavaType().setUserData(attr);
			attribute.setPropertyName(StrUtil.unlineToCamel(txtClassNameChil.getText()) + "s");
			this.attributeCVF.add(attribute);
			tblEntityProperty.getItems().add(attribute);

			paneChil.setVisible(false);
			lvTableListChil.getItems().clear();
			tblEntityPropertyChil.getItems().clear();
			txtTableNameChil.setText(null);
			txtClassNameChil.setText(null);
			txtPrimaryKeyChil.setText(null);
		}

	}

	/**
	 * 关闭子表
	 */
	public void chilCancel() {
		boolean result = AlertUtil.showConfirmAlert("取消的话你全部的设置都不生效,确定取消吗?");
		if (result) {
			paneChil.setVisible(false);
		}
	}

	// ------------------parent get/set---------------------

	public List<SuperAttribute> getSuperAttributes() {
		return superAttributes;
	}

	public void setSuperAttributes(List<SuperAttribute> superAttributes) {
		this.superAttributes = superAttributes;
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

	public ObservableList<AttributeCVF> getAttributeCVF() {
		return attributeCVF;
	}

	public void setAttributeCVF(ObservableList<AttributeCVF> attributeCVF) {
		this.attributeCVF = attributeCVF;
	}

	public IndexController getIndexController() {
		return indexController;
	}

	public void setIndexController(IndexController indexController) {
		this.indexController = indexController;
	}

	public CheckBox getChkUnlineCamel() {
		return chkUnlineCamel;
	}

	public void setChkUnlineCamel(CheckBox chkUnlineCamel) {
		this.chkUnlineCamel = chkUnlineCamel;
	}

	public CheckBox getChkSerializable() {
		return chkSerializable;
	}

	public void setChkSerializable(CheckBox chkSerializable) {
		this.chkSerializable = chkSerializable;
	}

	public CheckBox getChkCreateJDBCtype() {
		return chkCreateJDBCtype;
	}

	public void setChkCreateJDBCtype(CheckBox chkCreateJDBCtype) {
		this.chkCreateJDBCtype = chkCreateJDBCtype;
	}

	public CheckBox getChkGetAndSet() {
		return chkGetAndSet;
	}

	public void setChkGetAndSet(CheckBox chkGetAndSet) {
		this.chkGetAndSet = chkGetAndSet;
	}

	public CheckBox getChkConstruct() {
		return chkConstruct;
	}

	public void setChkConstruct(CheckBox chkConstruct) {
		this.chkConstruct = chkConstruct;
	}

	public CheckBox getChkConstructAll() {
		return chkConstructAll;
	}

	public void setChkConstructAll(CheckBox chkConstructAll) {
		this.chkConstructAll = chkConstructAll;
	}

	public Button getBtnSuccess() {
		return btnSuccess;
	}

	public void setBtnSuccess(Button btnSuccess) {
		this.btnSuccess = btnSuccess;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public void setBtnCancel(Button btnCancel) {
		this.btnCancel = btnCancel;
	}

	public TableView<AttributeCVF> getTblEntityProperty() {
		return tblEntityProperty;
	}

	public void setTblEntityProperty(TableView<AttributeCVF> tblEntityProperty) {
		this.tblEntityProperty = tblEntityProperty;
	}

	public TextField getTxtPrimaryKey() {
		return txtPrimaryKey;
	}

	public void setTxtPrimaryKey(TextField txtPrimaryKey) {
		this.txtPrimaryKey = txtPrimaryKey;
	}

	public Button getBtnAddTable() {
		return btnAddTable;
	}

	public void setBtnAddTable(Button btnAddTable) {
		this.btnAddTable = btnAddTable;
	}

	public Button getBtnAddToTableView() {
		return btnAddToTableView;
	}

	public void setBtnAddToTableView(Button btnAddToTableView) {
		this.btnAddToTableView = btnAddToTableView;
	}

	public TextField getTxtCustomType() {
		return txtCustomType;
	}

	public void setTxtCustomType(TextField txtCustomType) {
		this.txtCustomType = txtCustomType;
	}

	public TextField getTxtCustomName() {
		return txtCustomName;
	}

	public void setTxtCustomName(TextField txtCustomName) {
		this.txtCustomName = txtCustomName;
	}

	// ------------------chil get/set-----------------------

}
