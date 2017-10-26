package pers.cmeu.common;

public class StrUtil {

	/**
	 * 去掉下划线并将字符串转换成帕斯卡命名规范
	 * 
	 * @param str
	 * @return
	 */
	public static String unlineToPascal(String str) {
		if (str != null) {
			if (str.indexOf("_") == -1) {
				return fristToUpCase(str);
			}
			StringBuilder result = new StringBuilder();
			String[] temp = str.split("_");
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals("") || temp[i].isEmpty()) {
					continue;
				}
				result.append(fristToUpCaseLaterToLoCase(temp[i]));
			}
			return result.toString();
		}

		return str;
	}

	/**
	 * 去掉下划线并将字符串转换成驼峰命名规范
	 * 
	 * @param str
	 * @return
	 */
	public static String unlineToCamel(String str) {
		if (str != null) {
			if (str.indexOf("_") == -1) {
				return fristToLoCase(str);
			}
			StringBuilder result = new StringBuilder();
			String[] temp = str.split("_");
			boolean falg = false;
			for (int i = 0; i < temp.length; i++) {
				if (temp[i].equals("") || temp[i].isEmpty()) {
					continue;
				}
				if (falg == false) {
					falg = true;
					result.append(temp[i].toLowerCase());
				} else {
					result.append(fristToUpCaseLaterToLoCase(temp[i]));
				}
			}
			return result.toString();
		}

		return str;
	}

	/**
	 * 将字符串首字母大写其后小写
	 * 
	 * @param str
	 * @return
	 */
	public static String fristToUpCaseLaterToLoCase(String str) {
		if (str != null && str.length() > 1) {
			str = (str.substring(0, 1).toUpperCase()) + (str.substring(1).toLowerCase());
		}
		return str;
	}

	/**
	 * 将字符串首字母小写其后大写
	 * 
	 * @param str
	 * @return
	 */
	public static String fristToLoCaseLaterToUpCase(String str) {
		if (str != null && str.length() > 1) {
			str = (str.substring(0, 1).toLowerCase()) + (str.substring(1).toUpperCase());

		}
		return str;
	}

	/**
	 * 将字符串首字母大写
	 * 
	 * @param str
	 * @return
	 */
	public static String fristToUpCase(String str) {
		if (str != null && str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		}
		return str;
	}

	/**
	 * 将字符串首字母小写
	 * 
	 * @param str
	 * @return
	 */
	public static String fristToLoCase(String str) {
		if (str != null && str.length() > 1) {
			str = str.substring(0, 1).toLowerCase() + str.substring(1);
		}
		return str;
	}

}
