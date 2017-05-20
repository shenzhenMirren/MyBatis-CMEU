package pers.cmeu.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.DBType;
import pers.cmeu.models.DatabaseConfig;

public class DBUtil {
	private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

	/**
	 * 获得数据库连接
	 * 
	 * @param config
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
		DBType dbType = DBType.valueOf(config.getDbType());
		Class.forName(dbType.getDriverClass());
		String url = getConnectionURL(config);
		return DriverManager.getConnection(url, config.getUserName(), config.getUserPwd());
	}

	/**
	 * 获得数据库连接URL
	 * 
	 * @param dbConfig
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String getConnectionURL(DatabaseConfig dbConfig) throws ClassNotFoundException {
		DBType dbType = DBType.valueOf(dbConfig.getDbType());
		String connectionRUL = String.format(dbType.getConnectionUrlPattern(), dbConfig.getConnURL(),
				dbConfig.getListenPort(), dbConfig.getDbName(), dbConfig.getEncoding());
		return connectionRUL;
	}

	/**
	 * 获得数据库的表名
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static List<String> getTableNames(DatabaseConfig config) throws Exception {
		Connection conn = getConnection(config);
		List<String> tables = new ArrayList<>();
		ResultSet rs;
		if (config.getDbType().equalsIgnoreCase("sqlserver")) {
			// 如果是sqlserver数据库通过查询获得所有表跟视图
			String sql = "select name from sysobjects  where xtype='u' or xtype='v' ";
			rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				tables.add(rs.getString("name"));
			}

		} else {
			// 如果非sqlserver类型的数据库通过JDBC获得所有表跟视图
			DatabaseMetaData md = conn.getMetaData();
			String[] types = { "TABLE", "VIEW" };
			if (config.getDbType().equalsIgnoreCase("PostgreSQL")) {
				rs = md.getTables(null, null, null, types);
			}else {
				rs = md.getTables(null, config.getUserName().toUpperCase(), null, types);
			}
			while (rs.next()) {
				tables.add(rs.getString(3));
			}
		}

		return tables;
	}

	/**
	 * 获得所有列同时生成Attribute表模型
	 * 
	 * @param config
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static List<AttributeCVF> getTableColumns(DatabaseConfig config, String tableName) throws Exception {
		Connection conn = getConnection(config);
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getColumns(null, null, tableName, null);
		List<AttributeCVF> result = new ArrayList<>();
		while (rs.next()) {
			AttributeCVF attribute = new AttributeCVF();
			attribute.setConlumn(rs.getString("COLUMN_NAME"));
			attribute.setJavaType(JavaType.jdbcTypeToJavaType(rs.getString("TYPE_NAME")));
			attribute.setJdbcType(JDBCType.valiJDBCType(rs.getString("TYPE_NAME").toUpperCase()));
			result.add(attribute);
		}
		// 将主键放在第一位
		String key = null;
		key = getTablePrimaryKey(config, tableName);
		if (key != null) {
			boolean anyKeyInFrist = false;
			if (result.size() > 0) {
				if (result.get(0).getConlumn() != null) {
					if (result.get(0).getConlumn().equals(key)) {
						anyKeyInFrist = true;
					}
				}
			}
			if (!anyKeyInFrist) {
				int keyIndex=0;
				for (int i = 0; i < result.size(); i++) {
					if (result.get(i).getConlumn()!=null) {
						if (result.get(i).getConlumn().equals(key)) {
							keyIndex=i;
							break;
						}
					}
				}
				result.add(0, result.remove(keyIndex));
			}
		}

		return result;
	}

	/**
	 * 获得主键名称
	 * 
	 * @param config
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static String getTablePrimaryKey(DatabaseConfig config, String tableName) throws Exception {
		Connection conn = getConnection(config);
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getPrimaryKeys(null, null, tableName);
		while (rs.next()) {
			return rs.getString("COLUMN_NAME");
		}
		return null;
	}

}
