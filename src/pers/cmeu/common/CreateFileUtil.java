package pers.cmeu.common;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.ColumnItem;
import pers.cmeu.models.DBType;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.SuperAttribute;

/**
 * 创建所以文件的类
 * 
 * @author Mirren
 *
 */
public class CreateFileUtil {
	private Logger log=Logger.getLogger(CreateFileUtil.class.getName());
	private static CreateFileUtil createFileUtil = null;

	private CreateFileUtil() {
	}

	public static CreateFileUtil getInstance() {
		if (createFileUtil == null) {
			synchronized (CreateFileUtil.class) {
				if (createFileUtil == null) {
					createFileUtil = new CreateFileUtil();
				}
			}
		}
		return createFileUtil;
	}
	private String codeFormat;
	private String projectPath;
	private String projectRoot;
	private String entityPackage;
	private String daoPackage;
	private String mapPackage;
	private boolean createService;
	private String servicePackage;
	private String serviceImplPackage;
	private String updateMapperURL;
	private DatabaseConfig databaseConfig;
	private List<SuperAttribute> attributes;
		
	private boolean createAssist;
	private String assistPackage;
	private String assistName;
	private boolean createConfig;
	private String configPackage;
	private String configName;
	private boolean createMyUtil;
	private String myUtilPackage;
	private String myUtilName;
	

	/**
	 * 初始化数据
	 * 
	 * @param databaseConfig
	 * @param attributes
	 * @param projectPath
	 * @param rootDir
	 * @param entityPackage
	 * @param daoPackage
	 * @param mapPackage
	 * @param servicePackage
	 * @param serviceImplPackage
	 * @param createAssist
	 * @param assistPackage
	 * @param assistName
	 * @param createConfig
	 * @param configPackage
	 * @param configName
	 * @param createMyUtil
	 * @param myUtilPackage
	 * @param myUtilName
	 */
	public void init(DatabaseConfig databaseConfig, List<SuperAttribute> attributes,String codeFormat, String projectPath,
			String projectRoot, String entityPackage, String daoPackage, String mapPackage, boolean createService,
			String servicePackage, String serviceImplPackage, String updateMapperURL, boolean createAssist,
			String assistPackage, String assistName, boolean createConfig, String configPackage, String configName,
			boolean createMyUtil, String myUtilPackage, String myUtilName) {
		setDatabaseConfig(databaseConfig);
		setAttributes(attributes);
		this.codeFormat=codeFormat;
		setProjectPath(projectPath);
		setProjectRoot(projectRoot);
		setEntityPackage(entityPackage);
		setDaoPackage(daoPackage);
		setMapPackage(mapPackage);
		this.createService = createService;
		setServicePackage(servicePackage);
		setServiceImplPackage(serviceImplPackage);
		setUpdateMapperURL(updateMapperURL);
		setAssistPackage(assistPackage);
		setAssistName(assistName);
		setConfigPackage(configPackage);
		setConfigName(configName);
		setMyUtilPackage(myUtilPackage);
		setMyUtilName(myUtilName);
		this.createAssist = createAssist;
		this.createConfig = createConfig;
		this.createMyUtil = createMyUtil;
	}

	/**
	 * 执行所以创建
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean createAll() throws Exception {
		// 判断实体类包是否存在,不存在则创建
		if (Files.notExists(Paths.get(projectPath, projectRoot, entityPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, entityPackage));
			log.debug("执行创建实体类包!");
		}
		// 判断是否存在dao包是否存在,不存在则创建
		if (Files.notExists(Paths.get(projectPath, projectRoot, daoPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, daoPackage));
			log.debug("执行创建dao接口包!");
		}
		// 判断是否存在map包是否存在,不存在则创建
		if (Files.notExists(Paths.get(projectPath, projectRoot, mapPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, mapPackage));
			log.debug("执行创建mapper映射文件包!");
		}
		// 判断是否创建service/serviceImpl包是否存在,不存在则创建
		boolean andCreateDir = false;
		if (createService) {
			andCreateDir = createServiceDir();
		}
		
		Set<String> mapperURL=null;
		if (!createConfig||updateMapperURL!=null) {
			mapperURL=new HashSet<String>();
		}
		
		// 生成相应的类与文件
		for (SuperAttribute attr : attributes) {
			// -----------通用变量--------------
			// 获得主键类型
			log.debug("执行获得表主键列对应的java数据类型...");
			String ketType = TableUtil.getParmaryKeyType(attr.getPrimaryKey(), attr.getAttributes());
			log.debug("获得表主键列对应的java数据类型成功!");
			// -------------------------------
			//获得更新mapper更新的mapper包名及名称;
			if (!createConfig||updateMapperURL!=null) {
				if (attr.getMapperName()!=null) {
					mapperURL.add(mapPackage+attr.getMapperName()+ ".xml");
				}
			}
			
			// 生成实体类
			if (attr.getClassName() != null) {
				log.debug("执行生成实体类...");
				List<String> entitySpaces = new ArrayList<String>();
				if (attr.getColumnItems() != null) {
					for (ColumnItem item : attr.getColumnItems()) {
						entitySpaces.add(uriToPackage(entityPackage) + "." + item.getClassName());
					}
				}
				List<String[]> property = new ArrayList<String[]>();
				for (AttributeCVF cvf : attr.getAttributes()) {
					if (cvf.getCheck()==true) {
						property.add(new String[] { cvf.getJavaTypeValue(), cvf.getPropertyName() });
					}
				}
				Path entityPath = Paths.get(projectPath, projectRoot, entityPackage, attr.getClassName() + ".java");
				String entityStr = ClassUtil.getInstance().getEntityString(uriToPackage(entityPackage), entitySpaces,
						attr.getClassName(), property, attr.isSerializable(), attr.isConstruct(), attr.isConstructAll(),
						attr.isCreateGetSet());
				createFile(entityPath, entityStr);
				log.debug("生成实体类成功!");
			}
			// 生成dao
			if (attr.getDaoName() != null) {
				log.debug("执行生成dao");
				List<String> daoInport = new ArrayList<String>();
				daoInport.add(uriToPackage(entityPackage) + "." + attr.getClassName());
				daoInport.add("java.util.List");
				if (createAssist) {
					daoInport.add(uriToPackage(assistPackage) + "." + assistName);
					daoInport.add("org.apache.ibatis.annotations.Param");
				}
				String daoStr = DaoUtil.getInstance().getDaoString(uriToPackage(daoPackage), daoInport,
						attr.getDaoName(), attr.getClassName(), ketType, createAssist,attr.isAnyHasColl());
				Path daoPath = Paths.get(projectPath, projectRoot, daoPackage, attr.getDaoName() + ".java");
				createFile(daoPath, daoStr);
				log.debug("生成dao成功!");
			}
			// 生成mapper
			if (attr.getMapperName() != null) {
				log.debug("执行生成Mapper...");
				String mapStr = MapperUtil.getInstance().getMapperString(
						uriToPackage(daoPackage) + "." + attr.getDaoName(),
						uriToPackage(entityPackage) ,
						uriToPackage(assistPackage), databaseConfig.getDbType(), attr, createAssist,
						attr.isCreateJDBCType());
				Path mapPath = Paths.get(projectPath, projectRoot, mapPackage, attr.getMapperName() + ".xml");
				createFile(mapPath, mapStr);
				log.debug("生成Mapper成功!");
			}

			// 生成service
			if (attr.getServiceName() != null) {
				if (!andCreateDir) {
					andCreateDir = createServiceDir();
				}
				// 生成接口
				if (attr.getServiceName() != null) {
					log.debug("执行生成service...");
					List<String> serImport =new ArrayList<String>();
					serImport.add("java.util.List");
					serImport.add(uriToPackage(entityPackage) + "." + attr.getClassName());
					if (createAssist) {
						serImport.add(uriToPackage(assistPackage) + "." + assistName);
					}
					String serStr = ServiceUtil.getInstance().getServiceString(uriToPackage(servicePackage), serImport,
							attr.getServiceName(), attr.getClassName(), ketType, createAssist,attr.isAnyHasColl());
					Path serPath = Paths.get(projectPath, projectRoot, servicePackage, attr.getServiceName() + ".java");
					createFile(serPath, serStr);
					log.debug("生成service成功!");
				}
				// 生成实现
				if (attr.getServiceImplName() != null) {
					log.debug("执行生成serviceImpl...");
					List<String> serImplImport =new ArrayList<String>();
					serImplImport.add("java.util.List");
					serImplImport.add(uriToPackage(daoPackage) + "." + attr.getDaoName());
					serImplImport.add(uriToPackage(entityPackage) + "." + attr.getClassName());
					if (createAssist) {
						serImplImport.add(uriToPackage(assistPackage) + "." + assistName);
					}
					if (!serviceImplPackage.equals(servicePackage)) {
						serImplImport.add(uriToPackage(servicePackage)+"."+attr.getServiceName());
					}
					String serImplStr=ServiceImplUtil.getInstance().getServiceImplString(uriToPackage(serviceImplPackage), serImplImport, attr.getDaoName(), attr.getServiceName(), attr.getServiceImplName(), attr.getClassName(), ketType, createAssist,attr.isAnyHasColl());
					Path serImplPath = Paths.get(projectPath, projectRoot, serviceImplPackage, attr.getServiceImplName() + ".java");
					createFile(serImplPath, serImplStr);
					log.debug("生成serviceImpl成功!");
				}
			}

		}

		// 判断是否需要创建Assist,如果需要就创建
		if (createAssist) {
			if (Files.notExists(Paths.get(projectPath, projectRoot, assistPackage, assistName + ".java"))) {
				Files.createDirectories(Paths.get(projectPath, projectRoot, assistPackage));
				log.debug("执行创建Assist包名!");
				Path path = Paths.get(projectPath, projectRoot, assistPackage, assistName + ".java");
				createAssist(path);
			}
		}

		// 判断是否创建config配置文件
		if (createConfig) {
			if (Files.notExists(Paths.get(projectPath, projectRoot, configPackage, configName))) {
				Files.createDirectories(Paths.get(projectPath, projectRoot, configPackage));
				log.debug("执行创建配置文件包名!");
				Path path = Paths.get(projectPath, projectRoot, configPackage, configName);
				createMyBatisConfig(path);
			}
		} else if (updateMapperURL != null && !("".equals(updateMapperURL))&&mapperURL!=null) {
			log.debug("执行更新MyBatis配置文件资源路径...");
			String updateStr=MyBatisConfigUtil.getInstance().getNewConfig(updateMapperURL, mapperURL);
			createFile(Paths.get(updateMapperURL), updateStr);

			log.debug("更新MyBatis配置文件资源路径成功!");
		}
		// 判断是否需要创建MyUtil帮助工具
		if (createMyUtil) {
			if (Files.notExists(Paths.get(projectPath, projectRoot, myUtilPackage, myUtilName + ".java"))) {
				Files.createDirectories(Paths.get(projectPath, projectRoot, myUtilPackage));
				log.debug("执行创建MyBatis帮助工具包名!");
				Path path = Paths.get(projectPath, projectRoot, myUtilPackage, myUtilName + ".java");
				createMyBatisUtil(path);
			}
		}

		// 创建成功清除已经创建过的属性集
		attributes.clear();
		return true;
	}

	/**
	 * 创建service目录
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean createServiceDir() throws Exception {
		if (Files.notExists(Paths.get(projectPath, projectRoot, servicePackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, servicePackage));
			log.debug("执行创建Service包!");
		}
		if (Files.notExists(Paths.get(projectPath, projectRoot, serviceImplPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, serviceImplPackage));
			log.debug("执行创建ServiceImpl包!");
		}
		return true;
	}

	/**
	 * 创建Assist帮助类
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void createAssist(Path path) throws Exception {
		log.debug("准备创建Assist帮助类...");
		log.debug("读取Assist模板...");
		String Materi = readToString("pers/resource/models/AssistMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		codeInfo.append("package " + uriToPackage(assistPackage) + ";" + "\r\n");
		codeInfo.append(Materi + "\r\n");
		log.debug("执行创建...");
		createFile(path, codeInfo.toString());
		log.debug("Assist帮助类创建成功!");
	}

	/**
	 * 创建mybatis配置文件
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void createMyBatisConfig(Path path) throws Exception {
		log.debug("准备创建配置文件...");
		log.debug("读取配置文件模板...");
		String Materi = readToString("pers/resource/models/MyBatisConfigMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		if (Materi.indexOf("{*driver*}") != -1) {
			Materi = Materi.replace("{*driver*}", getDriverByDBType(databaseConfig.getDbType()));
		}
		if (Materi.indexOf("{*url*}") != -1) {
			Materi = Materi.replace("{*url*}", getURLByDatabaseConfig(databaseConfig));
		}
		if (Materi.indexOf("{*user*}") != -1) {
			Materi = Materi.replace("{*user*}", databaseConfig.getUserName());
		}
		if (Materi.indexOf("{*pwd*}") != -1) {
			Materi = Materi.replace("{*pwd*}", databaseConfig.getUserPwd());
		}
		if (Materi.indexOf("{*mapper*}") != -1) {
			Materi = Materi.replace("{*mapper*}", getMapperResourceURL());
		}

		codeInfo.append(Materi + "\r\n");
		log.debug("执行创建...");
		createFile(path, codeInfo.toString());
		log.debug("配置文件创建成功!");
	}

	/**
	 * 生成Mapper资源路径
	 * 
	 * @return
	 */
	private String getMapperResourceURL() {
		StringBuffer result = new StringBuffer();
		Set<String> set=new HashSet<String>();
		for (SuperAttribute attr : attributes) {
			if (attr.getMapperName()!=null&&!(attr.getMapperName().isEmpty())) {
				set.add(attr.getMapperName());
			}
		}
		for (String item : set) {
			result.append("        <mapper resource=\"" + mapPackage + item + ".xml\" />\r\n");
		}
		return result.toString();
	}

	/**
	 * 创建帮助工具
	 * 
	 * @throws Exception
	 * 
	 */
	private void createMyBatisUtil(Path path) throws Exception {
		log.debug("准备创建MyBatis帮助工具...");
		log.debug("读取MyBatis帮助工具模板...");
		String Materi = readToString("pers/resource/models/MyBatisUtilMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		codeInfo.append("package " + uriToPackage(myUtilPackage) + ";" + "\r\n");

		if (Materi.indexOf("{*resource*}") != -1) {
			Materi = Materi.replace("{*resource*}", configPackage + configName);
		}
		codeInfo.append(Materi + "\r\n");
		log.debug("执行创建...");
		createFile(path, codeInfo.toString());

		log.debug("MyBatis帮助工具创建成功!");
	}

	// ----------------------common function-----------------------------

	/**
	 * 将路径改变成包名
	 * 
	 * @param str
	 * @return
	 */
	private String uriToPackage(String str) {
		String result = str.replace("\\", ".");
		result = str.replace("/", ".");
		return result.substring(0, result.length() - 1);
	}

	/**
	 * 将包名转换成路径并在结尾加上/
	 * 
	 * @param str
	 * @return
	 */
	private String packageToUri(String str) {
		String result = str.replace(".", "/");
		return result + "/";
	}

	/**
	 * 通过读取文件并转换为utf-8的字符串
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private String readToString(String path) throws Exception {
		InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		int size = fis.available();
		byte[] buffer = new byte[size];
		fis.read(buffer);
		if (fis != null) {
			fis.close();
		}
		return new String(buffer, "UTF-8");
	}
	/**
	 * 获得数据库的驱动
	 * 
	 * @param dbType
	 * @return
	 */
	private String getDriverByDBType(String dbType) {
		String result = null;
		if (dbType.equals("Oracle")) {
			result = "oracle.jdbc.driver.OracleDriver";
		} else if (dbType.equals("MySQL")) {
			result = "com.mysql.jdbc.Driver";
		} else if (dbType.equals("SqlServer")) {
			result = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (dbType.equals("PostgreSQL")) {
			result = "org.postgresql.Driver";
		}
		return result;
	}

	/**
	 * 获得数据库连接uRL
	 * 
	 * @param dbConfig
	 * @return
	 * @throws ClassNotFoundException
	 */
	public String getURLByDatabaseConfig(DatabaseConfig dbConfig) throws ClassNotFoundException {
		DBType dbType = DBType.valueOf(dbConfig.getDbType());
		String connectionRUL = String.format(dbType.getConnectionUrlPattern(), dbConfig.getConnURL(),
				dbConfig.getListenPort(), dbConfig.getDbName(), dbConfig.getEncoding());
		return connectionRUL.replace("&", "&amp;");
	}
	/**
	 * 执行创建自定格式的文件
	 * @param path
	 * @param type
	 * @param str
	 * @throws Exception
	 */
	private  void createFile(Path path,String str) throws Exception {
		OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE);
		OutputStreamWriter writer=new OutputStreamWriter(out,codeFormat);
		writer.write(str);
		writer.flush();
		out.close();
		writer.close();
	}
	
	// -------------------get/set---------------------------
	
	public String getProjectPath() {
		return projectPath;
	}

	public String getCodeFormat() {
		return codeFormat;
	}

	public void setCodeFormat(String codeFormat) {
		this.codeFormat = codeFormat;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath + "/";
	}

	public String getProjectRoot() {

		return projectRoot;
	}

	public void setProjectRoot(String projectRoot) {
		char tmp = projectRoot.charAt(projectRoot.length() - 1);
		if (tmp != '/') {
			this.projectRoot = projectRoot + "/";
		} else {
			this.projectRoot = projectRoot;
		}
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = packageToUri(entityPackage);
	}

	public String getDaoPackage() {
		return daoPackage;
	}

	public void setDaoPackage(String daoPackage) {
		this.daoPackage = packageToUri(daoPackage);
	}

	public String getMapPackage() {
		return mapPackage;
	}

	public void setMapPackage(String mapPackage) {
		this.mapPackage = packageToUri(mapPackage);
	}

	public String getServicePackage() {
		return servicePackage;
	}

	public void setServicePackage(String servicePackage) {
		this.servicePackage = packageToUri(servicePackage);
	}

	public String getServiceImplPackage() {
		return serviceImplPackage;
	}

	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = packageToUri(serviceImplPackage);
	}

	public String getUpdateMapperURL() {
		return updateMapperURL;
	}

	public void setUpdateMapperURL(String updateMapperURL) {
		this.updateMapperURL = updateMapperURL;
	}

	public List<SuperAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<SuperAttribute> attributes) {
		if (attributes==null) {
			return;
		}
		this.attributes = attributes;
	}

	public DatabaseConfig getDatabaseConfig() {
		return databaseConfig;
	}

	public void setDatabaseConfig(DatabaseConfig databaseConfig) {
		this.databaseConfig = databaseConfig;
	}

	public boolean isCreateAssist() {
		return createAssist;
	}

	public void setCreateAssist(boolean createAssist) {
		this.createAssist = createAssist;
	}

	public String getAssistPackage() {
		return assistPackage;
	}

	public void setAssistPackage(String assistPackage) {
		this.assistPackage = packageToUri(assistPackage);
	}

	public String getAssistName() {
		return assistName;
	}

	public void setAssistName(String assistName) {
		this.assistName = assistName;
	}

	public boolean isCreateConfig() {
		return createConfig;
	}

	public void setCreateConfig(boolean createConfig) {
		this.createConfig = createConfig;
	}

	public String getConfigPackage() {
		return configPackage;
	}

	public void setConfigPackage(String configPackage) {
		if (configPackage == null || configPackage.isEmpty() || "".equals(configPackage.trim())) {
			this.configPackage = "";
		} else {
			this.configPackage = packageToUri(configPackage);
		}
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public boolean isCreateMyUtil() {
		return createMyUtil;
	}

	public void setCreateMyUtil(boolean createMyUtil) {
		this.createMyUtil = createMyUtil;
	}

	public String getMyUtilPackage() {
		return myUtilPackage;
	}

	public void setMyUtilPackage(String myUtilPackage) {
		this.myUtilPackage = packageToUri(myUtilPackage);
	}

	public String getMyUtilName() {
		return myUtilName;
	}

	public void setMyUtilName(String myUtilName) {
		this.myUtilName = myUtilName;
	}

	public boolean isCreateService() {
		return createService;
	}

	public void setCreateService(boolean createService) {
		this.createService = createService;
	}

}
