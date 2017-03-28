package pers.cmeu.controller;

import java.net.URL;
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
import pers.cmeu.common.DBUtil;
import pers.cmeu.common.StrUtil;
import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.SuperAttribute;
import pers.cmeu.view.AlertUtil;
/**
 * 因为舞台切换的问题,使用本类会影响到AttributeSet类,导致其无法通过代码关闭
 * 2017-3-19
 * @author duhua
 *
 */
public class AddMoreAttributeController extends BaseController {

	private AttributeSetController attributeSetController;
	// 存储数据库指定数据库,修改属性时用
	private DatabaseConfig DatabaseConfig;

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
	private TextField txtPrimaryKey;
	@FXML
	private TextField txtCustomType;
	@FXML
	private TextField txtCustomName;
	@FXML
	private TextField txtTableName;
	@FXML
	private TextField txtClassName;

	@FXML
	private Button btnSuccess;
	@FXML
	private Button btnCancel;
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

	@FXML
	private ListView<Label> lvTableList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tblEntityProperty.setEditable(true);
		tblEntityProperty.setStyle("-fx-font-size:14px");
		tblEntityProperty.setPlaceholder(new Label("双击左边表名数据加载..."));
	}

	/**
	 * 加载左边list所有表名
	 */
	public void initListItem() {
		try {
			List<String> tableNames = DBUtil.getTableNames(DatabaseConfig);

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
						try {
							String tableName = label.getUserData().toString();
							txtTableName.setText(tableName);
							txtClassName.setText(tableName);
							String primaryKey = DBUtil.getTablePrimaryKey(DatabaseConfig, tableName);
							if (primaryKey == null || "".equals(primaryKey)) {
								txtPrimaryKey.setText(null);
								txtPrimaryKey.setPromptText("注意:该表没有主键!");
							} else {
								txtPrimaryKey.setText(primaryKey);
							}
							if (tableName != null) {
								// 初始化表属性
								initTable();
							}

						} catch (Exception e) {
							AlertUtil.showErrorAlert("加载失败!失败原因:\r\n" + e.getMessage());
						}

					}
				});
				lvTableList.getItems().add(label);
			}

		} catch (Exception e) {
			AlertUtil.showErrorAlert("获得子表失败!原因:" + e.getMessage());
		}
	}

	/**
	 * 初始化右边的表
	 */
	public void initTable() {
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
	 */
	public ObservableList<AttributeCVF> getAttributeCVFs() {
		ObservableList<AttributeCVF> result = null;
		try {
			List<AttributeCVF> attributeCVFs = DBUtil.getTableColumns(DatabaseConfig, txtTableName.getText());
			result = FXCollections.observableList(attributeCVFs);
		} catch (Exception e) {
			AlertUtil.showErrorAlert("加载属性列失败!失败原因:\r\n" + e.getMessage());
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
	 */
	public void cancel(ActionEvent event) {
		boolean result = AlertUtil.showConfirmAlert("取消的话你全部的设置都不生效,确定取消吗?");
		if (result) {
			closeDialogStage();
		}

	}

	/**
	 * 添加子类确定
	 */
	public void success() {
		
		if (txtTableName.getText().equals("") || txtClassName.equals("")) {
			boolean result = AlertUtil.showConfirmAlert("您并没有添加任何子表...确定退出?");
			if (result) {
				
				closeDialogStageByMap(FXMLPage.ADD_MORE_ATTRIBUTE);
			}
		} else {
			SuperAttribute attr = new SuperAttribute();
			attr.setCamel(chkUnlineCamel.isSelected());
			attr.setSerializable(chkSerializable.isSelected());
			attr.setCreateJDBCType(chkCreateJDBCtype.isSelected());
			attr.setCreateGetSet(chkGetAndSet.isSelected());
			attr.setConstruct(chkConstruct.isSelected());
			attr.setConstructAll(chkConstructAll.isSelected());
			attr.setTableName(txtTableName.getText());
			attr.setClassName(txtClassName.getText());
			attr.setPrimaryKey(txtPrimaryKey.getText());
			attr.setAttributes(tblEntityProperty.getItems());
			attributeSetController.getSuperAttributes().add(attr);
			closeDialogStageByMap(FXMLPage.ADD_MORE_ATTRIBUTE);
		}
	
	}

	// -----------------get/set-----------------------

	public DatabaseConfig getDatabaseConfig() {
		return DatabaseConfig;
	}

	public void setDatabaseConfig(DatabaseConfig databaseConfig) {
		DatabaseConfig = databaseConfig;
	}

	public AttributeSetController getAttributeSetController() {
		return attributeSetController;
	}

	public ListView<Label> getLvTableList() {
		return lvTableList;
	}

	public void setLvTableList(ListView<Label> lvTableList) {
		this.lvTableList = lvTableList;
	}

	public void setAttributeSetController(AttributeSetController attributeSetController) {
		this.attributeSetController = attributeSetController;
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

	public ObservableList<AttributeCVF> getAttributeCVF() {
		return attributeCVF;
	}

	public void setAttributeCVF(ObservableList<AttributeCVF> attributeCVF) {
		this.attributeCVF = attributeCVF;
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

	public Button getBtnAddToTableView() {
		return btnAddToTableView;
	}

	public void setBtnAddToTableView(Button btnAddToTableView) {
		this.btnAddToTableView = btnAddToTableView;
	}

	public TextField getTxtTableName() {
		return txtTableName;
	}

	public void setTxtTableName(TextField txtTableName) {
		this.txtTableName = txtTableName;
	}

	public TableColumn<AttributeCVF, Boolean> getTdCheck() {
		return tdCheck;
	}

	public void setTdCheck(TableColumn<AttributeCVF, Boolean> tdCheck) {
		this.tdCheck = tdCheck;
	}

	public TableColumn<AttributeCVF, String> getTdColumn() {
		return tdColumn;
	}

	public void setTdColumn(TableColumn<AttributeCVF, String> tdColumn) {
		this.tdColumn = tdColumn;
	}

	public TableColumn<AttributeCVF, String> getTdJDBCType() {
		return tdJDBCType;
	}

	public void setTdJDBCType(TableColumn<AttributeCVF, String> tdJDBCType) {
		this.tdJDBCType = tdJDBCType;
	}

	public TableColumn<AttributeCVF, String> getTdJAVAType() {
		return tdJAVAType;
	}

	public void setTdJAVAType(TableColumn<AttributeCVF, String> tdJAVAType) {
		this.tdJAVAType = tdJAVAType;
	}

	public TableColumn<AttributeCVF, String> getTdPropertyName() {
		return tdPropertyName;
	}

	public void setTdPropertyName(TableColumn<AttributeCVF, String> tdPropertyName) {
		this.tdPropertyName = tdPropertyName;
	}

}
