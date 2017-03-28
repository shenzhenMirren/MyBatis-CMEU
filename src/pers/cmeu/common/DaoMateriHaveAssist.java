package pers.cmeu.common;
/**
 *实现带有Assist帮助类的dao生成字符串
 */
public class DaoMateriHaveAssist extends DaoFactory {

	@Override
	public String getRowCount(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    long get" + str + "RowCount(Assist assist);\r\n");
		}
		return result.toString();
	}
	
	@Override
	public String getSelect(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ename.length; i++) {
			result.append("    List<" + ename[i] + "> select" + ename[i] + "(Assist assist);\r\n");
		}
		return result.toString();
	}
	
	@Override
	public String getDelete(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int delete" + str + "(Assist assist);\r\n");
		}
		return result.toString();
	}
	
	@Override
	public String getUpdate(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int update" + str + "(@Param(\"enti\") " + str
					+ " value, @Param(\"assist\") Assist assist);\r\n");
		}
		return result.toString();
	}
	
	@Override
	public String getUpdateNonEmpty(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    int updateNonEmpty" + str + "(@Param(\"enti\") " + str
					+ " value, @Param(\"assist\") Assist assist);\r\n");
		}
		return result.toString();
	}


	
	
	
}
