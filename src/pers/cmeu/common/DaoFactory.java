package pers.cmeu.common;

import java.util.List;

public abstract class DaoFactory {

	/**
	 * 获得导入空间的字符串
	 * 
	 * @param packages
	 * @return
	 */
	public String getImport(List<String> packages) {
		if (packages == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : packages) {
			result.append("import " + str + ";\r\n");
		}
		return result.toString();
	}

	/**
	 * 	 获得通过主键查询语句字符串
	 *  [0]类类型/类名字[1]主键类型
	 * @param nameAndType
	 * @return
	 */
	public String getSelectById(String[][] nameAndType) {
		if (nameAndType == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < nameAndType.length; i++) {
			if (nameAndType[i][1]==null) {
				continue;
			}
			result.append("    "+
					nameAndType[i][0] + " select" + nameAndType[i][0] + "ById(" + nameAndType[i][1] + " id);\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得通过id删除语句的字符串
	 * 
	 * @param nameAndType
	 * @return
	 */
	public String getDeleteById(String[][] nameAndType) {
		if (nameAndType == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < nameAndType.length; i++) {
			if (nameAndType[i][1]==null) {
				continue;
			}
			result.append("    int delete" + nameAndType[i][0] + "ById(" + nameAndType[i][1] + " id);\r\n");
		}
		return result.toString();
	}
	/**
	 * 获得插入全部语句的字符串
	 * 
	 * @param ename
	 * @return
	 */
	public String getInsert(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int insert" + str + "(" + str + " value);\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得插入非空语句的字符串
	 * 
	 * @param ename
	 * @return
	 */
	public String getInsertNonEmpty(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int insertNonEmpty" + str + "(" + str + " value);\r\n");
		}
		return result.toString();
	}
	/**
	 * 获得通过id更新字符串
	 * @param ename
	 * @return
	 */
	public String getUpdateById(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int update" + str + "ById(" + str + " value);\r\n");
		}
		return result.toString();
	}
	/**
	 * 获得通过id更新非空字符串
	 * @param ename
	 * @return
	 */
	public String getUpdateNonEmptyById(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int updateNonEmpty" + str + "ById(" + str + " value);\r\n");
		}
		return result.toString();
	}
	
	//-----------------abstract-------------------------
	/**
	 * 获得没有assist的获得总行数的字符串
	 * 
	 * @param ename
	 * @return
	 */
	public abstract String getRowCount(String[] ename);

	/**
	 * 获得查询语句的字符串
	 * 
	 * @param ename
	 * @return
	 */
	public abstract String getSelect(String[] ename);


	/**
	 * 获得删除语句字符串
	 * 
	 * @param ename
	 * @return
	 */
	public abstract String getDelete(String[] ename);
	

	
	/**
	 * 获得更新语句的字符串
	 * 
	 * @param ename
	 * @return
	 */
	public abstract String getUpdate(String[] ename);

	/**
	 * 获得更新非空语句的字符串
	 * @param ename
	 * @return
	 */
	public abstract String getUpdateNonEmpty(String[] ename);

}
