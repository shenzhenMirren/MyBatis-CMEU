package pers.cmeu.common;

import java.util.List;

public class ServiceImplUtil {
	private ServiceImplUtil() {
	}

	private static ServiceImplUtil serviceImplUtil = null;

	public static ServiceImplUtil getInstance() {
		if (serviceImplUtil == null) {
			synchronized (ServiceImplUtil.class) {
				if (serviceImplUtil == null) {
					serviceImplUtil = new ServiceImplUtil();
				}
			}
		}
		return serviceImplUtil;
	}

	public String getServiceImplString(String packageName,List<String> importSpaces, String daoName, String serviceName,
			String serviceImplName, String entityName, String idType, boolean anyAssist,boolean anyHasColl) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + packageName + ";\r\n");
		buffer.append(getImport(importSpaces));
		buffer.append("public class " + serviceImplName + " implements " + serviceName + "{\r\n");
		buffer.append("    private " + daoName + " " + StrUtil.fristToLoCase(daoName) + ";\r\n");
		buffer.append(getRowCount(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		buffer.append(getSelectEntity(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		if (anyHasColl) {
			buffer.append(getSelectEntityOfPaging(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		}
		buffer.append(getSelectEntityById(StrUtil.fristToLoCase(daoName), entityName, idType));
		buffer.append(getInsert(StrUtil.fristToLoCase(daoName), entityName));
		buffer.append(getInsertNonEmpty(StrUtil.fristToLoCase(daoName), entityName));
		buffer.append(getDelete(StrUtil.fristToLoCase(daoName), entityName, idType, anyAssist));
		buffer.append(getUpdate(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		buffer.append(getUpdateNonEmpty(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		buffer.append("\r\n    public " + daoName + " get" + daoName + "() {\r\n");
		buffer.append("        return this." + StrUtil.fristToLoCase(daoName) + ";\r\n    }\r\n\r\n");
		buffer.append(
				"    public void set" + daoName + "(" + daoName + " " + StrUtil.fristToLoCase(daoName) + ") {\r\n");
		buffer.append("        this." + StrUtil.fristToLoCase(daoName) + " = " + StrUtil.fristToLoCase(daoName)
				+ ";\r\n    }\r\n\r\n");
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * 获得导入空间的字符串
	 * 
	 * @param packages
	 * @return
	 */
	private String getImport(List<String> packages) {
		if (packages == null ||packages.size()==0) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : packages) {
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
	private String getRowCount(String daoName, String entityName, boolean anyAssist) {
		StringBuffer buffer = new StringBuffer();
		if (anyAssist) {
			buffer.append("    @Override\r\n    public long get" + entityName + "RowCount(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".get" + entityName + "RowCount(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    @Override\r\n    public long get" + entityName + "RowCount(){\r\n");
			buffer.append("        return " + daoName + ".get" + entityName + "RowCount();\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();
	}

	/**
	 * 获得查询语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getSelectEntity(String daoName, String entityName, boolean anyAssist) {
		StringBuffer buffer = new StringBuffer();
		if (anyAssist) {
			buffer.append("    @Override\r\n    public List<" + entityName + "> select" + entityName + "(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".select" + entityName + "(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    @Override\r\n    public List<" + entityName + "> select" + entityName + "(){\r\n");
			buffer.append("        return " + daoName + ".select" + entityName + "();\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();
	}
	/**
	 * 获得需要分页的查询语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getSelectEntityOfPaging(String daoName, String entityName, boolean anyAssist) {
		StringBuffer buffer = new StringBuffer();
		if (anyAssist) {
			buffer.append("    @Override\r\n    public List<" + entityName + "> select" + entityName + "OfPaging(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".select" + entityName + "OfPaging(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    @Override\r\n    public List<" + entityName + "> select" + entityName + "OfPaging(){\r\n");
			buffer.append("        return " + daoName + ".select" + entityName + "OfPaging();\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();
	}

	/**
	 * 获得查询语句通过Id
	 * 
	 * @param entityName
	 * @param idType
	 * @return
	 */
	private String getSelectEntityById(String daoName, String entityName, String idType) {
		if (idType == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("    @Override\r\n    public " + entityName + " select" + entityName + "ById(" + idType + " id){\r\n");
		buffer.append("        return " + daoName + ".select" + entityName + "ById(id);\r\n");
		buffer.append("    }\r\n");
		return buffer.toString();
	}

	/**
	 * 获得插入语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsert(String daoName, String entityName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("    @Override\r\n    public int insert" + entityName + "(" + entityName + " value){\r\n");
		buffer.append("        return " + daoName + ".insert" + entityName + "(value);\r\n");
		buffer.append("    }\r\n");
		return buffer.toString();
	}

	/**
	 * 获得插入非空语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsertNonEmpty(String daoName, String entityName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("    @Override\r\n    public int insertNonEmpty" + entityName + "(" + entityName + " value){\r\n");
		buffer.append("        return " + daoName + ".insertNonEmpty" + entityName + "(value);\r\n");
		buffer.append("    }\r\n");
		return buffer.toString();
	}

	/**
	 * 创建删除语句
	 * 
	 * @param entityName
	 * @param idType
	 * @return
	 */
	private String getDelete(String daoName, String entityName, String idType, boolean anyAssist) {
		if (idType == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		if (anyAssist) {
			buffer.append("    @Override\r\n    public int delete" + entityName + "ById(" + idType + " id){\r\n");
			buffer.append("        return " + daoName + ".delete" + entityName + "ById(id);\r\n");
			buffer.append("    }\r\n");
			buffer.append("    @Override\r\n    public int delete" + entityName + "(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".delete" + entityName + "(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    @Override\r\n    public int delete" + entityName + "ById(" + idType + " id){\r\n");
			buffer.append("        return " + daoName + ".delete" + entityName + "ById(id);\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();

	}

	/**
	 * 获得更新语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getUpdate(String daoName, String entityName, boolean anyAssist) {
		StringBuffer buffer = new StringBuffer();
		if (anyAssist) {
			buffer.append("    @Override\r\n    public int update" + entityName + "ById(" + entityName + " enti){\r\n");
			buffer.append("        return " + daoName + ".update" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");

			buffer.append(
					"    @Override\r\n    public int update" + entityName + "(" + entityName + " value, Assist assist){\r\n");
			buffer.append("        return " + daoName + ".update" + entityName + "(value,assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    @Override\r\n    public int update" + entityName + "ById(" + entityName + " enti){\r\n");
			buffer.append("        return " + daoName + ".update" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();
	}

	/**
	 * 获得更新不为空的语句
	 * 
	 * @param entityName
	 * @param anyAssist
	 * @return
	 */
	private String getUpdateNonEmpty(String daoName, String entityName, boolean anyAssist) {
		StringBuffer buffer = new StringBuffer();
		if (anyAssist) {
			buffer.append(
					"    @Override\r\n    public int updateNonEmpty" + entityName + "ById(" + entityName + " enti){\r\n");
			buffer.append("        return " + daoName + ".updateNonEmpty" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");

			buffer.append("    @Override\r\n    public int updateNonEmpty" + entityName + "(" + entityName
					+ " value, Assist assist){\r\n");
			buffer.append("        return " + daoName + ".updateNonEmpty" + entityName + "(value,assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append(
					"    @Override\r\n    public int updateNonEmpty" + entityName + "ById(" + entityName + " enti){\r\n");
			buffer.append("        return " + daoName + ".updateNonEmpty" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();
	}

}
