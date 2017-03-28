package pers.cmeu.common;

import java.util.List;

/**
 * 一开始使用StringBuilder,出现生成错乱,因此改为StrinBuffer
 * @author duhua
 *
 */
public class EntityFactory {

	/**
	 * 获得属性
	 * @param str
	 * @return
	 */
	public static String getProperty(List<String[]> str){
		if (str==null) {
			return "";
		}
		StringBuffer result=new StringBuffer();
		for (String[] list : str) {			
			result.append("    private "+list[0]+" "+list[1]+";\r\n");
		}
		return result.toString();
	}
	/**
	 * 获得无参构造方法
	 * @param cname
	 * @return
	 */
	public static String getConstr(String cname) {
		return "    public "+cname+"() {\r\n        super();\r\n    }\r\n";
	}
	/**
	 * 获得带参构造方法;
	 * @param str
	 */
	public static String getConstrAll(String cname,List<String[]> str){
		if (str==null) {
			return "";
		}
		StringBuffer result=new StringBuffer("    public "+cname+"(");
		for (int i = 0; i < str.size(); i++) {
			if (str.get(i)[1]==null) {
				continue;
			}
			if (i==0) {
				result.append(str.get(i)[0]+" "+str.get(i)[1]);
			}else {
				result.append(","+str.get(i)[0]+" "+str.get(i)[1]);				
			}
		}

		for (int i = 0; i < str.size(); i++) {
			if (str.get(i)[1]==null) {
				continue;
			}
			if (i==0) {
				result.append(") {\r\n        super();\r\n");
				result.append("        this."+str.get(i)[1]+" = "+str.get(i)[1]+";\r\n");				
			}else {
				result.append("        this."+str.get(i)[1]+" = "+str.get(i)[1]+";\r\n");				
			}
		}
		result.append("    }\r\n");
		return result.toString();
	}
	/**
	 * 获得get与set
	 * @param str
	 * @return
	 */
	public static String getGetSet(List<String[]> str) {
		if (str==null) {
			return "";
		}
		StringBuffer result=new StringBuffer();
		for (int i = 0; i < str.size(); i++) {
			result.append("    public "+str.get(i)[0]+" get"+StrUtil.fristToUpCase(str.get(i)[1])+"() {\r\n");
			result.append("        return this."+str.get(i)[1]+";\r\n    }\r\n\r\n");
			result.append("    public void set"+StrUtil.fristToUpCase(str.get(i)[1])+"("+str.get(i)[0]+" "+str.get(i)[1]+") {\r\n");
			result.append("        this."+str.get(i)[1]+" = "+str.get(i)[1]+";\r\n    }\r\n\r\n");
		
		}
		
		return result.toString();
	}
}
