package pers.cmeu.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import pers.cmeu.models.ClassConfig;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.HistoryConfig;

public class ConfigUtil {
	private static final String CONFIG_DIR = "config";
	private static final String CONFIG_FILE = "/ConfigDB.db";
	private static final String DRIVER = "org.sqlite.JDBC";
	private static final String DB_URL = "jdbc:sqlite:./config/ConfigDB.db";

	/**
	 * 获得数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		Class.forName(DRIVER);
		Connection conn = DriverManager.getConnection(DB_URL);
		return conn;
	}

	/**
	 * 查看是否存在配置文件数据库,如果不存在则创建
	 * 
	 * @throws IOException
	 */
	public static void existsConfigDB() throws IOException {
		File path = new File(CONFIG_DIR);

		if (!path.exists()) {
			path.mkdir();
		}
		File file = new File(CONFIG_DIR + CONFIG_FILE);
		if (!file.exists()) {
			InputStream fis = null;
			FileOutputStream fos = null;
			try {
				fis = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("pers/resource/config/ConfigDB.db");
				fos = new FileOutputStream(CONFIG_DIR + CONFIG_FILE);
				byte[] buffer = new byte[1024];
				int byteread = 0;
				while ((byteread = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, byteread);
				}
			} finally {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			}

		}
	}

	/**
	 * 保存数据库连接
	 * 
	 * @param connName
	 * @param dbConfig
	 * @throws Exception
	 */
	public static void saveDatabaseConfig(String connName, DatabaseConfig dbConfig) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			ResultSet rs1 = stat.executeQuery(String.format("select * from DBSet where name ='%s'", connName));
			if (rs1.next()) {
				throw new RuntimeException("该连接名称已经存在!请使用其它名字...");
			}
			String jsonStr = JSON.toJSONString(dbConfig);
			String sql = String.format("insert into DBSet values('%s', '%s')", connName, jsonStr);
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 更新数据库连接
	 * 
	 * @param dbConfig
	 * @throws Exception
	 */
	public static void updateDatabaseConfig(DatabaseConfig dbConfig) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String jsonStr = JSON.toJSONString(dbConfig);
			String sql = String.format("update DBSet set value='%s' where name='%s'", jsonStr, dbConfig.getConnName());
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 获得数据库连接
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<DatabaseConfig> getDatabaseConfig() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from DBSet");
			List<DatabaseConfig> result = new ArrayList<DatabaseConfig>();
			while (rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				DatabaseConfig databaseConfig = JSON.parseObject(value, DatabaseConfig.class);
				databaseConfig.setConnName(name);
				result.add(databaseConfig);
			}

			return result;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 删除数据库连接
	 * 
	 * @param name
	 * @throws Exception
	 */
	public static void deleteDatabaseConfig(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String sql = String.format("delete from DBSet where name='%s'", name);
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 保存配置文件信息
	 * 
	 * @param Config
	 * @throws Exception
	 */
	public static void saveHistoryConfig(HistoryConfig config) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			ResultSet rs1 = stat.executeQuery(
					String.format("select * from HistoryConfig where name ='%s'", config.getHistoryConfigName()));
			if (rs1.next()) {
				throw new RuntimeException("该配置名称已经存在!请使用其它名字...");
			}

			String jsonStr = JSON.toJSONString(config);
			String sql = String.format("insert into HistoryConfig values('%s', '%s')", config.getHistoryConfigName(),
					jsonStr);
			stat.executeUpdate(sql);
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 获得配置文件信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<HistoryConfig> getHistoryConfigs() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String sql = "select * from HistoryConfig ";
			rs = stat.executeQuery(sql);
			List<HistoryConfig> configs = new ArrayList<>();
			while (rs.next()) {
				String value = rs.getString("value");
				configs.add(JSON.parseObject(value, HistoryConfig.class));
			}
			return configs;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 通过名字找到配置信息
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static HistoryConfig getHistoryConfigByName(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String sql = String.format("select * from HistoryConfig where name ='%s'", name);
			rs = stat.executeQuery(sql);
			HistoryConfig config = null;
			while (rs.next()) {
				String value = rs.getString("value");
				config = JSON.parseObject(value, HistoryConfig.class);
			}
			return config;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 删除配置文件信息
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static int deleteHistoryConfigByName(String name) throws Exception {
		Connection conn = null;
		Statement stat = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String sql = String.format("DELETE FROM HistoryConfig where name='%s'", name);
			return stat.executeUpdate(sql);
		} finally {
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 保存实体类配置文件信息
	 * 
	 * @param Config
	 * @throws Exception
	 */
	public static int saveClassConfig(ClassConfig config) throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String jsonStr = JSON.toJSONString(config);
			String sql = String.format("replace into ClassConfig(name,value) values('classConfig', '%s')", jsonStr);
			int result = stat.executeUpdate(sql);
			return result;
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 获得实体类配置文件信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ClassConfig getClassConfig() throws Exception {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stat = conn.createStatement();
			String sql = String.format("select * from ClassConfig where name='classConfig'");
			ResultSet resultSet = stat.executeQuery(sql);
			while (resultSet.next()) {
				ClassConfig result = JSON.parseObject(resultSet.getString("value"), ClassConfig.class);
				return result;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
		return null;
	}

}
