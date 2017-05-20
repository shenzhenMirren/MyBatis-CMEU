package pers.cmeu.common;

import java.util.List;

public class DaoUtil {
	private DaoUtil() {
	};

	private static DaoUtil daoUtil = null;

	public static DaoUtil getInstance() {
		if (daoUtil == null) {
			synchronized (DaoUtil.class) {
				if (daoUtil == null) {
					daoUtil = new DaoUtil();
				}
			}
		}
		return daoUtil;
	}

	/**
	 * 获得dao层字符串
	 * @param packageName
	 * @param importSpaces
	 * @param daoName
	 * @param entityName
	 * @param idType
	 * @param anyAssist
	 * @return
	 */
	public String getDaoString(String packageName, List<String> importSpaces, String daoName, String entityName,
			String idType, boolean anyAssist,boolean anyHasColl) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + packageName + ";\r\n");
		buffer.append(getImport(importSpaces));
		buffer.append("public interface " + daoName + "{\r\n");
		buffer.append(getRowCount(entityName, anyAssist));
		buffer.append(getSelectEntity(entityName, anyAssist));
		if (anyHasColl) {
			buffer.append(getSelectEntityOfPaging(entityName, anyAssist));
		}
		buffer.append(getSelectEntityById(entityName, idType));
		buffer.append(getInsert(entityName));
		buffer.append(getInsertNonEmpty(entityName));
		buffer.append(getDelete(entityName, idType, anyAssist));
		buffer.append(getUpdate(entityName, anyAssist));
		buffer.append(getUpdateNonEmpty(entityName, anyAssist));
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * 获得导入空间的字符串
	 * 
	 * @param importSpaces
	 * @return
	 */
	private String getImport(List<String> importSpaces) {
		if (importSpaces == null || importSpaces.size() == 0) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : importSpaces) {
			result.append("import " + str + ";\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得数据总行数
	 * 
	 * @param daoName
	 * @param anyAssist
	 * @return
	 */
	private String getRowCount(String entityName, boolean anyAssist) {
		if (anyAssist) {
			return "    long get" + entityName + "RowCount(Assist assist);\r\n";
		} else {
			return "    long get" + entityName + "RowCount();\r\n";
		}
	}

	/**
	 * 获得查询所有语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getSelectEntity(String entityName, boolean anyAssist) {
		if (anyAssist) {
			return "    List<" + entityName + "> select" + entityName + "(Assist assist);\r\n";
		} else {
			return "    List<" + entityName + "> select" + entityName + "();\r\n";
		}
	}
	
	/**
	 * 获得需要分页的查询语句
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getSelectEntityOfPaging(String entityName, boolean anyAssist){
		if (anyAssist) {
			return "    List<" + entityName + "> select" + entityName + "OfPaging(Assist assist);\r\n";
		} else {
			return "    List<" + entityName + "> select" + entityName + "OfPaging();\r\n";
		}
	}

	/**
	 * 获得查询语句通过Id
	 * 
	 * @param entityName
	 * @param idType
	 * @return
	 */
	private String getSelectEntityById(String entityName, String idType) {
		if (idType == null) {
			return "";
		}
		return "    " + entityName + " select" + entityName + "ById(" + idType + " id);\r\n";
	}

	/**
	 * 获得插入语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsert(String entityName) {
		return "    int insert" + entityName + "(" + entityName + " value);\r\n";
	}

	/**
	 * 获得插入非空语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsertNonEmpty(String entityName) {
		return "    int insertNonEmpty" + entityName + "(" + entityName + " value);\r\n";
	}

	/**
	 * 创建删除语句
	 * 
	 * @param entityName
	 * @param idType
	 * @return
	 */
	private String getDelete(String entityName, String idType, boolean anyAssist) {
		if (idType == null) {
			return "";
		}
		if (anyAssist) {
			return "    int delete" + entityName + "ById(" + idType + " id);\r\n    int delete" + entityName
					+ "(Assist assist);\r\n";
		} else {
			return "    int delete" + entityName + "ById(" + idType + " id);\r\n";
		}
	}

	/**
	 * 获得更新语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getUpdate(String entityName, boolean anyAssist) {
		if (anyAssist) {
			return "    int update" + entityName + "ById(" + entityName + " enti);\r\n    int update" + entityName
					+ "(@Param(\"enti\") " + entityName + " value, @Param(\"assist\") Assist assist);\r\n";
		} else {
			return "    int update" + entityName + "ById(" + entityName + " enti);\r\n";
		}
	}

	/**
	 * 获得更新不为空的语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getUpdateNonEmpty(String entityName, boolean anyAssist) {
		if (anyAssist) {
			return "    int updateNonEmpty" + entityName + "ById(" + entityName + " enti);\r\n    int updateNonEmpty"
					+ entityName + "(@Param(\"enti\") " + entityName + " value, @Param(\"assist\") Assist assist);\r\n";
		} else {
			return "    int updateNonEmpty" + entityName + "ById(" + entityName + " enti);\r\n";
		}
	}

}
