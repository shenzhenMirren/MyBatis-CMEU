package pers.cmeu.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

public class MyBatisConfigUtil {
	private MyBatisConfigUtil() {
	}

	public static MyBatisConfigUtil getInstance() {
		return new MyBatisConfigUtil();
	}

	/**
	 * 更新制定路径的配置文件
	 * 
	 * @param configPath
	 * @param mapperURL
	 * @return
	 * @throws Exception
	 */
	public String getNewConfig(String configPath, Set<String> mapperURL) throws Exception {

		StringBuffer buffer = new StringBuffer();
		buffer.append(getConfigFile(configPath));
		// 如果存在mappers指直接在mappers里面添加,如果没有mappers,存在跟节点则创建mappers并添加,反则放回null
		if (buffer.indexOf("</mappers>") != -1) {
			StringBuffer temp = new StringBuffer();
			for (String map : mapperURL) {
				temp.append("        <mapper resource=\"" + map + "\" />\r\n");
			}
			buffer.insert(buffer.indexOf("</mappers>") - 4, temp);
		} else if (buffer.indexOf("</configuration>") != -1) {
			StringBuffer temp = new StringBuffer();
			temp.append("    <mappers>\r\n");
			for (String map : mapperURL) {
				temp.append("        <mapper resource=\"" + map + "\" />\r\n");
			}
			temp.append("    </mappers>\r\n");
			buffer.insert(buffer.indexOf("</configuration>"), temp);
		} else {
			return null;
		}
		return buffer.toString();
	}

	/**
	 * 获得配置文件文本信息
	 * 
	 * @param configPath
	 * @return
	 * @throws Exception
	 */
	private String getConfigFile(String configPath) throws Exception {
		FileInputStream fis = new FileInputStream(new File(configPath));
		int size = fis.available();
		byte[] buffer = new byte[size];
		fis.read(buffer);
		if (fis != null) {
			fis.close();
		}
		return new String(buffer, "UTF-8");
	}

}
