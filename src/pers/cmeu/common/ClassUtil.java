package pers.cmeu.common;

import java.util.List;

public class ClassUtil {
	private ClassUtil() {
	};

	/**
	 * 获得对象
	 * 
	 * @return
	 */
	public static ClassUtil getInstance() {
		return new ClassUtil();
	}

	/**
	 * 获得实体类,属性列表包名,实体类名,属性数据集合下标0属性类型,1位属性名字
	 * 
	 * @param packageName
	 * @param entityName
	 * @param property
	 * @return
	 */
	public String getEntityString(String packageName, List<String> importSpaces, String entityName,
			List<String[]> property, boolean anySerializable, boolean createConstr, boolean createConstrAll,
			boolean createSetGet) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + packageName + ";\r\n");
		buffer.append(getImport(importSpaces));
		if (anySerializable) {
			buffer.append("public class " + entityName
					+ " implements java.io.Serializable {\r\n    private static final long serialVersionUID = 1L;\r\n");
		} else {
			buffer.append("public class " + entityName + " {\r\n");
		}
		buffer.append(getProperty(property));
		if (createConstr) {
			buffer.append(getConstr(entityName));
		}
		if (createConstrAll) {
			buffer.append(getConstrAll(entityName, property));
		}
		if (createSetGet) {
			buffer.append(getGetSet(property));
		}
		buffer.append("}\r\n");
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
	 * 获得无参构造方法
	 * 
	 * @param cname
	 * @return
	 */
	private String getConstr(String entityName) {
		return "    public " + entityName + "() {\r\n        super();\r\n    }\r\n";
	}

	/**
	 * 获得属性
	 * 
	 * @param str
	 * @return
	 */
	private String getProperty(List<String[]> str) {
		if (str == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String[] list : str) {
			result.append("    private " + list[0] + " " + list[1] + ";");
			if (list[2]!=null&&!("".equals(list[2]))) {
				result.append("//" + list[2] + "\r\n");
			}else{
				result.append("\r\n");
			}
		}
		return result.toString();
	}

	/**
	 * 获得带参构造方法;
	 * 
	 * @param str
	 */
	private String getConstrAll(String entityName, List<String[]> str) {
		if (str == null) {
			return "";
		}
		StringBuffer result = new StringBuffer("    public " + entityName + "(");
		for (int i = 0; i < str.size(); i++) {
			if (str.get(i)[1] == null) {
				continue;
			}
			if (i == 0) {
				result.append(str.get(i)[0] + " " + str.get(i)[1]);
			} else {
				result.append("," + str.get(i)[0] + " " + str.get(i)[1]);
			}
		}

		for (int i = 0; i < str.size(); i++) {
			if (str.get(i)[1] == null) {
				continue;
			}
			if (i == 0) {
				result.append(") {\r\n        super();\r\n");
				result.append("        this." + str.get(i)[1] + " = " + str.get(i)[1] + ";\r\n");
			} else {
				result.append("        this." + str.get(i)[1] + " = " + str.get(i)[1] + ";\r\n");
			}
		}
		result.append("    }\r\n");
		return result.toString();
	}

	/**
	 * 获得get与set
	 * 
	 * @param str
	 * @return
	 */
	private String getGetSet(List<String[]> str) {
		if (str == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < str.size(); i++) {
			result.append("    public " + str.get(i)[0] + " get" + StrUtil.fristToUpCase(str.get(i)[1]) + "() {\r\n");
			result.append("        return this." + str.get(i)[1] + ";\r\n    }\r\n\r\n");
			result.append("    public void set" + StrUtil.fristToUpCase(str.get(i)[1]) + "(" + str.get(i)[0] + " "
					+ str.get(i)[1] + ") {\r\n");
			result.append("        this." + str.get(i)[1] + " = " + str.get(i)[1] + ";\r\n    }\r\n\r\n");

		}

		return result.toString();
	}

}
