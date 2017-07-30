package pers.cmeu.common;

import java.util.List;

public class DaoUtil {
	private DaoUtil() {
	};
	public static DaoUtil getInstance() {
		return new DaoUtil();
	}

	/**
	 * 获得dao层字符串
	 * 
	 * @param packageName
	 * @param importSpaces
	 * @param daoName
	 * @param entityName
	 * @param idType
	 * @param anyAssist
	 * @return
	 */
	public String getDaoString(String packageName, List<String> importSpaces, String daoName, String entityName,
			String idType, boolean anyAssist, boolean anyHasColl) {
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
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		if (anyAssist) {
			countStr.append("	 * 获得" + entityName + "数据的总行数,可以通过辅助工具Assist进行条件查询,如果没有条件则传入null\r\n");
			countStr.append("	 * @param assist\r\n");
		} else {
			countStr.append("	 * 获得" + entityName + "数据的总行数\r\n");
		}
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");
		if (anyAssist) {
			countStr.append("    long get" + entityName + "RowCount(Assist assist);\r\n");
		} else {
			countStr.append("    long get" + entityName + "RowCount();\r\n");
		}
		return countStr.toString();
	}

	/**
	 * 获得查询所有语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getSelectEntity(String entityName, boolean anyAssist) {
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		if (anyAssist) {
			countStr.append("	 * 获得" + entityName + "数据集合,可以通过辅助工具Assist进行条件查询,如果没有条件则传入null\r\n");
			countStr.append("	 * @param assist\r\n");
		} else {
			countStr.append("	 * 获得" + entityName + "数据集合\r\n");
		}
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");
		if (anyAssist) {
			countStr.append("    List<" + entityName + "> select" + entityName + "(Assist assist);\r\n");
		} else {
			countStr.append("    List<" + entityName + "> select" + entityName + "();\r\n");
		}
		return countStr.toString();
	}

	/**
	 * 获得需要分页的查询语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getSelectEntityOfPaging(String entityName, boolean anyAssist) {
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		if (anyAssist) {
			countStr.append("	 * 获得" + entityName
					+ "数据集合,该方法为多表关联时保证分页的数据不缺失不重复,可以正常得到所有数据,如果非多表分页的情况建议使用不带ofPaging的方法,可以通过辅助工具Assist进行查询,如果没有条件则传入null\r\n");
			countStr.append("	 * @param assist\r\n");
		} else {
			countStr.append(
					"	 * 获得" + entityName + "数据集合,该方法为多表关联时保证分页的数据不缺失不重复,可以正常得到所有数据,如果非多表分页的情况建议使用不带ofPaging的方法\r\n");
		}
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");
		if (anyAssist) {
			countStr.append("    List<" + entityName + "> select" + entityName + "OfPaging(Assist assist);\r\n");
		} else {
			countStr.append("    List<" + entityName + "> select" + entityName + "OfPaging();\r\n");
		}
		return countStr.toString();
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
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		countStr.append("	 * 通过" + entityName + "的id获得" + entityName + "对象\r\n");
		countStr.append("	 * @param id\r\n");
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");
		countStr.append("    " + entityName + " select" + entityName + "ById(" + idType + " id);\r\n");
		return countStr.toString();
	}

	/**
	 * 获得插入语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsert(String entityName) {
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		countStr.append("	 * 插入" + entityName + "到数据库,包括null值\r\n");
		countStr.append("	 * @param value\r\n");
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");
		countStr.append("    int insert" + entityName + "(" + entityName + " value);\r\n");
		return countStr.toString();
	}

	/**
	 * 获得插入非空语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsertNonEmpty(String entityName) {
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		countStr.append("	 * 插入" + entityName + "中属性值不为null的数据到数据库\r\n");
		countStr.append("	 * @param value\r\n");
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");
		countStr.append("    int insertNonEmpty" + entityName + "(" + entityName + " value);\r\n");
		return countStr.toString();
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
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		countStr.append("	 * 通过" + entityName + "的id删除" + entityName + "\r\n");
		countStr.append("	 * @param id\r\n");
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");

		if (anyAssist) {
			countStr.append("    int delete" + entityName + "ById(" + idType + " id);\r\n");
			countStr.append("	/**\r\n");
			countStr.append("	 * 通过辅助工具Assist的条件删除" + entityName + "\r\n");
			countStr.append("	 * @param assist\r\n");
			countStr.append("	 * @return\r\n");
			countStr.append("	 */\r\n");
			countStr.append("    int delete" + entityName + "(Assist assist);\r\n");
		} else {
			countStr.append("    int delete" + entityName + "ById(" + idType + " id);\r\n");
		}

		return countStr.toString();
	}

	/**
	 * 获得更新语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getUpdate(String entityName, boolean anyAssist) {
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		countStr.append("	 * 通过" + entityName + "的id更新" + entityName + "中的数据,包括null值\r\n");
		countStr.append("	 * @param enti\r\n");
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");

		if (anyAssist) {
			countStr.append("    int update" + entityName + "ById(" + entityName + " enti);\r\n ");
			countStr.append("	/**\r\n");
			countStr.append("	 * 通过辅助工具Assist的条件更新" + entityName + "中的数据,包括null值\r\n");
			countStr.append("	 * @param value\r\n");
			countStr.append("	 * @param assist\r\n");
			countStr.append("	 * @return\r\n");
			countStr.append("	 */\r\n");
			countStr.append("    int update" + entityName + "(@Param(\"enti\") " + entityName
					+ " value, @Param(\"assist\") Assist assist);\r\n");
		} else {
			countStr.append("    int update" + entityName + "ById(" + entityName + " enti);\r\n");
		}

		return countStr.toString();
	}

	/**
	 * 获得更新不为空的语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getUpdateNonEmpty(String entityName, boolean anyAssist) {
		StringBuffer countStr = new StringBuffer();
		countStr.append("	/**\r\n");
		countStr.append("	 * 通过" + entityName + "的id更新" + entityName + "中属性不为null的数据\r\n");
		countStr.append("	 * @param enti\r\n");
		countStr.append("	 * @return\r\n");
		countStr.append("	 */\r\n");

		if (anyAssist) {
			countStr.append("    int updateNonEmpty" + entityName + "ById(" + entityName + " enti);\r\n ");
			countStr.append("	/**\r\n");
			countStr.append("	 * 通过辅助工具Assist的条件更新" + entityName + "中属性不为null的数据\r\n");
			countStr.append("	 * @param value\r\n");
			countStr.append("	 * @param assist\r\n");
			countStr.append("	 * @return\r\n");
			countStr.append("	 */\r\n");
			countStr.append("    int updateNonEmpty" + entityName + "(@Param(\"enti\") " + entityName
					+ " value, @Param(\"assist\") Assist assist);\r\n");
		} else {
			countStr.append("    int updateNonEmpty" + entityName + "ById(" + entityName + " enti);\r\n");
		}

		return countStr.toString();

	}

}
