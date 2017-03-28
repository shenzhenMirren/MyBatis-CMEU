package pers.cmeu.common;

/**
 *实现没有Assist帮助类的dao字符串
 */
public class DaoMateriNotHaveAssist extends DaoFactory {

	@Override
	public String getRowCount(String[] ename) {
		if (ename == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (String str : ename) {
			result.append("    long get" + str + "RowCount();\r\n");
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
			result.append("    List<" + ename[i] + "> select" + ename[i] + "();\r\n");
		}
		return result.toString();
	}
	
	@Override
	public String getUpdate(String[] ename) {
		return "";
	}
	
	@Override
	public String getUpdateNonEmpty(String[] ename) {
		return "";
	}

	@Override
	public String getDelete(String[] ename) {
		return "";
	}

	
	
}
