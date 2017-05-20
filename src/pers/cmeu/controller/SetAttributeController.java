package pers.cmeu.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pers.cmeu.common.DBUtil;
import pers.cmeu.common.StrUtil;
import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.ColumnItem;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.SuperAttribute;
import pers.cmeu.view.AlertUtil;

public class SetAttributeController implements Initializable {
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
	private Button btnSuccess;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnAddToTableView;
	@FXML
	private Button btnAddProperty;
	@FXML
	private Button btnAddItem;
	
	//主键策略
	@FXML
	private TextArea txtaSelectKey;
	@FXML
	private Label lblSelectKey;
	@FXML
	private CheckBox chkSelectKey;

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

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tblEntityProperty.setEditable(true);
		tblEntityProperty.setStyle("-fx-font-size:14px");
		tblEntityProperty.setPlaceholder(new Label("正在加载属性..."));
		init();
	}
	
	/**
	 *初始化
	 */
	public void init() {
		IndexController indexContro=(IndexController) StageManager.CONTROLLER.get("index");
		
		// 存储数据库指定数据库,修改属性时用
		DatabaseConfig selectedDatabaseConfig=indexContro.getSelectedDatabaseConfig();
		// 记录存储的表名,修改属性时用
		String selectedTableName=indexContro.getSelectedTableName();
		boolean falg=indexContro.isFalg();
		initTablePrimaryKey(selectedDatabaseConfig,selectedTableName);
		if (falg == false) {
			// 获得工厂数据
			attributeCVF = getAttributeCVFs(selectedDatabaseConfig, selectedTableName);
		} else {
			// 需要创建所有类的集合;
			List<SuperAttribute> superAttributes = indexContro.getSuperAttributes();
			if (superAttributes.size()>0) {
				attributeCVF =FXCollections.observableList(superAttributes.get(0).getAttributes());
			}else {
				attributeCVF = getAttributeCVFs(selectedDatabaseConfig, selectedTableName);
			}
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
		indexContro.setFalg(true);
		
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
	 * 初始化主键
	 */
	public void initTablePrimaryKey(DatabaseConfig selectedDatabaseConfig,String selectedTableName) {
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
	 * 生成主键策略
	 * @param event
	 */
	public void selectKey(ActionEvent event) {
		if (txtPrimaryKey.getText()==null||"".equals(txtPrimaryKey.getText())) {
			AlertUtil.showWarnAlert("你尚未选择表或者你所选择的表没有主键");
			chkSelectKey.selectedProperty().set(false);
			return;
		}
		
		String keyType="";
		for (AttributeCVF attr : tblEntityProperty.getItems()) {
			if (attr.getConlumn().equals(txtPrimaryKey.getText())) {
				keyType=attr.getJavaTypeValue();
				break;
			}
		}
		String dbType=((IndexController) StageManager.CONTROLLER.get("index")).getSelectedDatabaseConfig().getDbType();
		StringBuffer res=new StringBuffer();
		res.append("        <selectKey keyProperty=\""+txtPrimaryKey.getText()+"\" resultType=\""+keyType+"\" ");
		if ("MySQL".equals(dbType)) {
			res.append("order=\"AFTER\">\r\n            SELECT LAST_INSERT_ID() AS "+txtPrimaryKey.getText());
		}else if ("SqlServer".equals(dbType)) {
			res.append("order=\"AFTER\">\r\n            SELECT SCOPE_IDENTITY() AS "+txtPrimaryKey.getText());
		} else if ("PostgreSQL".equals(dbType)) {
			res.append("order=\"BEFORE\">\r\n            SELECT nextval() AS "+txtPrimaryKey.getText());
		}else {
			res.append("order=\"BEFORE\">\r\n            SELECT .Nextval FROM dual");
		}
		res.append("\r\n        </selectKey>");
		txtaSelectKey.setText(res.toString());
		lblSelectKey.setVisible(chkSelectKey.isSelected());
		txtaSelectKey.setVisible(chkSelectKey.isSelected());
	}
	
	/**
	 * 将属性添加到属性表
	 * @param event
	 */
	public void addToTable(ActionEvent event) {
		AttributeCVF attribute = new AttributeCVF();
		attribute.setJavaType(txtCustomType.getText());
		attribute.setPropertyName(txtCustomName.getText());
		this.attributeCVF.add(attribute);
		tblEntityProperty.getItems().add(attribute);
	}
	
	private boolean anyOpenPro=true;//用于作为判断打开添加属性(true)还是添加集合(false)
	private int needOrNotCreatePages=0;//用于判断是否需要创建分页
	/**
	 * 添加新表作为属性
	 */
	public void addProperty() {
		anyOpenPro=true;
		StageManager.CONTROLLER.put("attribute", this);
		Stage stage=new Stage();
		try {
			Parent root= FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource(FXMLPage.ADD_SON_ATTRIBUTE.getFxml()));
			stage.setTitle("添加新表");
			stage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(root));
			stage.show();
			StageManager.STAGE.put("addPropertyBySon", stage);
		} catch (IOException e) {
			AlertUtil.showErrorAlert("初始化添加属性失败:\r\n原因:"+e.getMessage());
		}
		
	}
	/**
	 * 添加新表作为集合
	 */
	public void addPropertyItem() {
		anyOpenPro=false;
		StageManager.CONTROLLER.put("attribute", this);
		Stage stage=new Stage();
		try {
			Parent root= FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource(FXMLPage.ADD_SON_ATTRIBUTE.getFxml()));
			stage.setTitle("添加新表");
			stage.getIcons().add(new Image("pers/resource/image/CMEUicon.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(root));
			stage.show();
			StageManager.STAGE.put("addPropertyBySon", stage);
		} catch (IOException e) {
			AlertUtil.showErrorAlert("初始化添加属性失败:\r\n原因:"+e.getMessage());
		}
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
			((IndexController)StageManager.CONTROLLER.get("index")).setChangeInfo(false);
			StageManager.STAGE.get("attribute").close();
			StageManager.STAGE.remove("attribute");
		}
	}
	public void success() {
		IndexController index=(IndexController)StageManager.CONTROLLER.get("index");
		//设置当前页面所选择的信息
		if (index.getThisSuperAttribute()==null) {
			index.setThisSuperAttribute(new SuperAttribute());
		}
		index.getThisSuperAttribute().setCamel(chkUnlineCamel.isSelected());
		index.getThisSuperAttribute().setSerializable(chkSerializable.isSelected());
		index.getThisSuperAttribute().setCreateJDBCType(chkCreateJDBCtype.isSelected());
		index.getThisSuperAttribute().setConstruct(chkConstruct.isSelected());
		index.getThisSuperAttribute().setConstructAll(chkConstructAll.isSelected());
		index.getThisSuperAttribute().setCreateGetSet(chkGetAndSet.isSelected());
		index.getThisSuperAttribute().setPrimaryKey(txtPrimaryKey.getText());
		if (needOrNotCreatePages!=0) {
			index.getThisSuperAttribute().setAnyHasColl(true);
		}
		if (chkSelectKey.isSelected()) {
			index.getThisSuperAttribute().setSelectKey(txtaSelectKey.getText());
		}
		index.getThisSuperAttribute().setAttributes(tblEntityProperty.getItems());
		List<ColumnItem> items=new ArrayList<ColumnItem>();
		for (AttributeCVF item : tblEntityProperty.getItems()) {
			if (item.getColumnItem()==null) {
				continue;
			}
			items.add(item.getColumnItem());
		}
		if (items.size()>0) {
			index.getThisSuperAttribute().setColumnItems(items);
		}
		
		index.setChangeInfo(true);
		StageManager.STAGE.get("attribute").close();
		StageManager.STAGE.remove("attribute");
	}
	//-----------------------get/set--------------------------------
	public String getPrimaryKey(){
		return txtPrimaryKey.getText();
	}

	public boolean isAnyOpenPro() {
		return anyOpenPro;
	}

	public void setAnyOpenPro(boolean anyOpenPro) {
		this.anyOpenPro = anyOpenPro;
	}

	public int getNeedOrNotCreatePages() {
		return needOrNotCreatePages;
	}

	public void setNeedOrNotCreatePages(int needOrNotCreatePages) {
		this.needOrNotCreatePages = needOrNotCreatePages;
	}
	
}
