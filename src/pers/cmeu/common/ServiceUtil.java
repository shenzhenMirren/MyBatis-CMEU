package pers.cmeu.common;

import java.util.List;

public class ServiceUtil {
	private ServiceUtil() {
	}

	public static ServiceUtil getInstance() {
		return new ServiceUtil();
	}

	public String getServiceImplString(String packageName, List<String> importSpaces, String daoName,
			String serviceName, String serviceImplName, String entityName, String idType, boolean anyAssist,
			boolean anyHasColl, boolean anySpringAnno) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + packageName + ";\r\n");
		buffer.append(getImport(importSpaces));
		if (anySpringAnno) {
			buffer.append(
					"import org.springframework.beans.factory.annotation.Autowired;\r\nimport org.springframework.stereotype.Service;\r\n");
			buffer.append("@Service\r\n");
		}
		buffer.append("public class " + serviceImplName + "{\r\n");
		if (anySpringAnno) {
			buffer.append("    @Autowired\r\n");
		}
		buffer.append("    private " + daoName + " " + StrUtil.fristToLoCase(daoName) + ";\r\n");
		buffer.append(getRowCount(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		buffer.append(getSelectEntity(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		if (anyHasColl) {
			buffer.append(getSelectEntityOfPaging(StrUtil.fristToLoCase(daoName), entityName, anyAssist));
		}
		buffer.append(getSelectEntityByObj(StrUtil.fristToLoCase(daoName), entityName));
		buffer.append(getSelectEntityById(StrUtil.fristToLoCase(daoName), entityName, idType));
		buffer.append(getInsert(StrUtil.fristToLoCase(daoName), entityName));
		buffer.append(getInsertNonEmpty(StrUtil.fristToLoCase(daoName), entityName));
		buffer.append(getInsertBatch(StrUtil.fristToLoCase(daoName), entityName));
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
		if (packages == null || packages.size() == 0) {
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
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		//生成注释
		if (anyAssist) {
			buffer.append("	 * 获得" + entityName + "数据的总行数,可以通过辅助工具Assist进行条件查询,如果没有条件则传入null\r\n");
			buffer.append("	 * @param assist\r\n");
		} else {
			buffer.append("	 * 获得" + entityName + "数据的总行数\r\n");
		}
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		if (anyAssist) {
			buffer.append("    public long get" + entityName + "RowCount(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".get" + entityName + "RowCount(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    public long get" + entityName + "RowCount(){\r\n");
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
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		if (anyAssist) {
			buffer.append("	 * 获得" + entityName + "数据集合,可以通过辅助工具Assist进行条件查询,如果没有条件则传入null\r\n");
			buffer.append("	 * @param assist\r\n");
		} else {
			buffer.append("	 * 获得" + entityName + "数据集合\r\n");
		}
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		if (anyAssist) {
			buffer.append("   public List<" + entityName + "> select" + entityName
					+ "(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".select" + entityName + "(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    public List<" + entityName + "> select" + entityName + "(){\r\n");
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
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		if (anyAssist) {
			buffer.append("	 * 获得" + entityName
					+ "数据集合,该方法为多表关联时保证分页的数据不缺失不重复,可以正常得到所有数据,如果非多表分页的情况建议使用不带ofPaging的方法,可以通过辅助工具Assist进行查询,如果没有条件则传入null\r\n");
			buffer.append("	 * @param assist\r\n");
		} else {
			buffer.append(
					"	 * 获得" + entityName + "数据集合,该方法为多表关联时保证分页的数据不缺失不重复,可以正常得到所有数据,如果非多表分页的情况建议使用不带ofPaging的方法\r\n");
		}
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		if (anyAssist) {
			buffer.append("    public List<" + entityName + "> select" + entityName
					+ "OfPaging(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".select" + entityName + "OfPaging(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append(
					"    public List<" + entityName + "> select" + entityName + "OfPaging(){\r\n");
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
	private String getSelectEntityByObj(String daoName, String entityName) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 获得一个" + entityName + "对象,以参数" + entityName + "对象中不为空的属性作为条件进行查询\r\n");
		buffer.append("	 * @param obj\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		buffer.append("    public " + entityName + " select" + entityName + "ByObj(" + entityName
				+ " obj){\r\n");
		buffer.append("        return " + daoName + ".select" + entityName + "ByObj(obj);\r\n");
		buffer.append("    }\r\n");
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
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 通过" + entityName + "的id获得" + entityName + "对象\r\n");
		buffer.append("	 * @param id\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		buffer.append(
				"    public " + entityName + " select" + entityName + "ById(" + idType + " id){\r\n");
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
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 插入" + entityName + "到数据库,包括null值\r\n");
		buffer.append("	 * @param value\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		buffer.append("    public int insert" + entityName + "(" + entityName + " value){\r\n");
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
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 插入" + entityName + "中属性值不为null的数据到数据库\r\n");
		buffer.append("	 * @param value\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		buffer.append(
				"    public int insertNonEmpty" + entityName + "(" + entityName + " value){\r\n");
		buffer.append("        return " + daoName + ".insertNonEmpty" + entityName + "(value);\r\n");
		buffer.append("    }\r\n");
		return buffer.toString();
	}

	/**
	 * 获得批量插入语句
	 * 
	 * @param entityName
	 * @return
	 */
	private String getInsertBatch(String daoName, String entityName) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 批量插入" + entityName + "到数据库\r\n");
		buffer.append("	 * @param value\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		buffer.append(
				"    public int insert" + entityName + "ByBatch(List<" + entityName + "> value){\r\n");
		buffer.append("        return " + daoName + ".insert" + entityName + "ByBatch(value);\r\n");
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
		
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 通过" + entityName + "的id删除" + entityName + "\r\n");
		buffer.append("	 * @param id\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		if (anyAssist) {
			buffer.append("    public int delete" + entityName + "ById(" + idType + " id){\r\n");
			buffer.append("        return " + daoName + ".delete" + entityName + "ById(id);\r\n");
			buffer.append("    }\r\n");
			
			buffer.append("	/**\r\n");
			buffer.append("	 * @Author CodeGenerator \r\n ");
			buffer.append("	 * 通过辅助工具Assist的条件删除" + entityName + "\r\n");
			buffer.append("	 * @param assist\r\n");
			buffer.append("	 * @return\r\n");
			buffer.append("	 */\r\n");
			buffer.append("    public int delete" + entityName + "(Assist assist){\r\n");
			buffer.append("        return " + daoName + ".delete" + entityName + "(assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    public int delete" + entityName + "ById(" + idType + " id){\r\n");
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
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 通过" + entityName + "的id更新" + entityName + "中的数据,包括null值\r\n");
		buffer.append("	 * @param enti\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		
		if (anyAssist) {
			buffer.append("    public int update" + entityName + "ById(" + entityName + " enti){\r\n");
			buffer.append("        return " + daoName + ".update" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");

			buffer.append("	/**\r\n");
			buffer.append("	 * @Author CodeGenerator \r\n ");
			buffer.append("	 * 通过辅助工具Assist的条件更新" + entityName + "中的数据,包括null值\r\n");
			buffer.append("	 * @param value\r\n");
			buffer.append("	 * @param assist\r\n");
			buffer.append("	 * @return\r\n");
			buffer.append("	 */\r\n");
			buffer.append("   public int update" + entityName + "(" + entityName
					+ " value, Assist assist){\r\n");
			buffer.append("        return " + daoName + ".update" + entityName + "(value,assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    public int update" + entityName + "ById(" + entityName + " enti){\r\n");
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
		buffer.append("	/**\r\n");
		buffer.append("	 * @Author CodeGenerator \r\n ");
		buffer.append("	 * 通过" + entityName + "的id更新" + entityName + "中属性不为null的数据\r\n");
		buffer.append("	 * @param enti\r\n");
		buffer.append("	 * @return\r\n");
		buffer.append("	 */\r\n");
		if (anyAssist) {
			buffer.append("    public int updateNonEmpty" + entityName + "ById(" + entityName
					+ " enti){\r\n");
			buffer.append("        return " + daoName + ".updateNonEmpty" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");

			buffer.append("	/**\r\n");
			buffer.append("	 * @Author CodeGenerator \r\n ");
			buffer.append("	 * 通过辅助工具Assist的条件更新" + entityName + "中属性不为null的数据\r\n");
			buffer.append("	 * @param value\r\n");
			buffer.append("	 * @param assist\r\n");
			buffer.append("	 * @return\r\n");
			buffer.append("	 */\r\n");
			buffer.append("    public int updateNonEmpty" + entityName + "(" + entityName
					+ " value, Assist assist){\r\n");
			buffer.append("        return " + daoName + ".updateNonEmpty" + entityName + "(value,assist);\r\n");
			buffer.append("    }\r\n");
		} else {
			buffer.append("    public int updateNonEmpty" + entityName + "ById(" + entityName
					+ " enti){\r\n");
			buffer.append("        return " + daoName + ".updateNonEmpty" + entityName + "ById(enti);\r\n");
			buffer.append("    }\r\n");
		}
		return buffer.toString();
	}

}
