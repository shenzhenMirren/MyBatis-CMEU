package pers.cmeu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pers.cmeu.common.ConfigUtil;
import pers.cmeu.common.DBUtil;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.view.AlertUtil;

public class UpdateConnection extends BaseController  {
	private Logger log = Logger.getLogger(UpdateConnection.class.getName());
	private IndexController indexController;
	@FXML
	private TextField txtConnName;
	@FXML
	private TextField txtConnURL;
	@FXML
	private TextField txtListenPort;
	@FXML
	private TextField txtDBName;
	@FXML
	private TextField txtUserName;
	@FXML
	private TextField txtUserPwd;
	@FXML
	private ComboBox<String> cboDBType;
	@FXML
	private ComboBox<String> cboDBCoding;
	@FXML
	private Button btnTestConn;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnSave;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	public void init() {
		log.debug("初始化修改数据库连接窗口...");
		DatabaseConfig config = indexController.getUpdateOfDatabaseConfig();
		// 初始化下拉列表
		cboDBType.getItems().addAll("Oracle", "MySQL", "SqlServer", "PostgreSQL");
		cboDBType.setValue(config.getDbType());
		cboDBCoding.getItems().addAll("utf8", "gb2312", "gbk");
		cboDBCoding.setValue(config.getEncoding());
		txtConnName.setText(config.getConnName());
		txtConnURL.setText(config.getConnURL());
		txtDBName.setText(config.getDbName());
		txtListenPort.setText(config.getListenPort());
		txtUserName.setText(config.getUserName());
		txtUserPwd.setText(config.getUserPwd());
		log.debug("初始化数据库连接窗口成功!");
	}
	
	/**
	 * 确定修改
	 * @param event
	 */
	public void btnUpdate(ActionEvent event) {
		log.debug("执行修改数据库连接...");
		DatabaseConfig dbConfig = getDatabaseConfig();
		if (dbConfig == null) {
			log.debug("连接数据库的数据为null,取消保存操作!!!");
			return;
		}
		try {
			ConfigUtil.updateDatabaseConfig(dbConfig);
			AlertUtil.showAndWaitInfoAlert("修改数据库连接成功!");
			indexController.loadTVDataBase();
			log.debug("修改数据库连接成功!");
			getDialogStage().close();
		} catch (Exception e) {
			AlertUtil.showErrorAlert(e.getMessage());
			log.error("修改数据库连接失败!!!"+e);
		}
	}
	/**
	 * 取消修改
	 * @param event
	 */
	public void onCancel(ActionEvent event) {
		getDialogStage().close();
	}
	
	/**
	 * 测试连接
	 * @param event
	 */
	public void testConnection(ActionEvent event){	
		log.debug("执行测试数据库连接...");		
		DatabaseConfig config = getDatabaseConfig();
		if (config == null) {
			log.debug("连接数据库的数据为null,取消测试操作!!!");
			return;
		}
		try {
			DBUtil.getConnection(config);
			AlertUtil.showInfoAlert("连接成功!");
			log.debug("数据库测试连接成功!");
		} catch (Exception e) {
			AlertUtil.showWarnAlert("连接失败!原因:"+e.getMessage());
			log.error("数据库连接测试失败!!!"+e);
		}
	}
	
	/**
	 * 获得连接的所有字段
	 * @return
	 */
	public DatabaseConfig getDatabaseConfig() {
		 String connName=txtConnName.getText().trim();
		 String connURL=txtConnURL.getText().trim();
		 String listenPort=txtListenPort.getText().trim();
		 String dbName=txtDBName.getText().trim();
		 String userName=txtUserName.getText().trim();
		 String userPwd=txtUserPwd.getText().trim();
		 String dbType=cboDBType.getValue();
		 String encoding=cboDBCoding.getValue();
		 boolean isEmpty=validata(connName,connURL,listenPort,dbName,userName,dbType,encoding);
		 if (isEmpty) {
			DatabaseConfig config=new DatabaseConfig(connName, connURL, listenPort, dbName, userName, userPwd, dbType, encoding);
			return config;
		 }else {
			 AlertUtil.showWarnAlert("除了密码以外所有属性都为必需填与选择");
			 return null;
		}
		
	}
	
	/**
	 * 验证所有属性是否已经填写
	 * @param str
	 * @return
	 */
	public boolean validata(String...str) {
		for (String string : str) {
			if (string==null||"".equals(string)) {
				return false;
			}
		}
		return true;
	}

	public IndexController getIndexController() {
		return indexController;
	}

	public void setIndexController(IndexController indexController) {
		this.indexController = indexController;
	}
	
	
}
