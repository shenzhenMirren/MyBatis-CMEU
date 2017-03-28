package pers.cmeu.common;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.DBType;
import pers.cmeu.models.DatabaseConfig;
import pers.cmeu.models.SuperAttribute;

public class FileFactory {
	private String projectPath;
	private String projectRoot;
	private String entityPackage;
	private String entityName;// 该变量基本不用,因为名字通过传进来获取
	private String daoPackage;
	private String daoName;
	private String mapPackage;
	private String mapName;
	private DatabaseConfig databaseConfig;

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
	 * 创建实体,mapper,dao,assist,配置文件,帮助工具
	 * 
	 * @param supAttr
	 * @return
	 * @throws Exception
	 */
	public boolean create(List<SuperAttribute> supAttr) throws Exception {
		// 判断实体类包是否存在,不存在则创建
		if (Files.notExists(Paths.get(projectPath, projectRoot, entityPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, entityPackage));
		}
		// 创建实体类
		createEntity(supAttr);

		// 判断是否存在dao包是否存在,不存在则创建
		if (Files.notExists(Paths.get(projectPath, projectRoot, daoPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, daoPackage));
		}
		// 创建dao
		createDao(supAttr);

		// 判断是否存在map包是否存在,不存在则创建
		if (Files.notExists(Paths.get(projectPath, projectRoot, mapPackage))) {
			Files.createDirectories(Paths.get(projectPath, projectRoot, mapPackage));
		}
		CreateMapper(supAttr);

		// 判断是否需要创建Assist,如果需要就创建
		if (createAssist) {
			if (Files.notExists(Paths.get(projectPath, projectRoot, assistPackage, assistName + ".java"))) {
				Files.createDirectories(Paths.get(projectPath, projectRoot, assistPackage));
				Path path = Paths.get(projectPath, projectRoot, assistPackage, assistName + ".java");
				createAssist(path);
			}
		}
		// 判断是否创建config配置文件
		if (createConfig) {
			if (Files.notExists(Paths.get(projectPath, projectRoot, configPackage, configName))) {
				Files.createDirectories(Paths.get(projectPath, projectRoot, configPackage));
				Path path = Paths.get(projectPath, projectRoot, configPackage, configName);
				createMyBatisConfig(path);
			}
		}
		// 判断是否需要创建MyUtil帮助工具
		if (createMyUtil) {
			if (Files.notExists(Paths.get(projectPath, projectRoot, myUtilPackage, myUtilName + ".java"))) {
				Files.createDirectories(Paths.get(projectPath, projectRoot, myUtilPackage));
				Path path = Paths.get(projectPath, projectRoot, myUtilPackage, myUtilName + ".java");
				createMyBatisUtil(path);
			}
		}
		supAttr.clear();
		return true;
	}

	/**
	 * 初始化并创建实体类
	 * 
	 * @param supAttr
	 * @throws Exception
	 */
	private void createEntity(List<SuperAttribute> supAttr) throws Exception {
		// 存储类的属性
		List<String[]> property = null;
		for (int i = 0; i < supAttr.size(); i++) {
			List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
			property = new ArrayList<String[]>();

			for (int j = 0; j < cvf.size(); j++) {
				if (cvf.get(j).getCheck().booleanValue() == true) {
					property.add(new String[] { cvf.get(j).getJavaTypeValue(), cvf.get(j).getPropertyName() });
				}
			}
			Path path = Paths.get(projectPath, projectRoot, entityPackage, supAttr.get(i).getClassName() + ".java");
			String className = supAttr.get(i).getClassName();
			String serializable = "";
			String getSet = "";
			String construct = "";
			String constructAll = "";
			if (supAttr.get(i).isSerializable()) {
				className += " implements java.io.Serializable";
				serializable = "    private static final long serialVersionUID = 1L;";
			}
			if (supAttr.get(i).isCreateGetSet()) {
				getSet = EntityFactory.getGetSet(property);
			}
			if (supAttr.get(i).isConstruct()) {
				construct = EntityFactory.getConstr(supAttr.get(i).getClassName());
			}
			if (supAttr.get(i).isConstructAll()) {
				constructAll = EntityFactory.getConstrAll(supAttr.get(i).getClassName(), property);
			}

			runCreateEntity(path, className, serializable, EntityFactory.getProperty(property), construct, constructAll,
					getSet);
		}

	}

	/**
	 * 执行创建entity
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void runCreateEntity(Path path, String className, String serializable, String property, String constr,
			String constrAll, String getSet) throws Exception {
		String Materi = readToString("pers/resource/models/entityMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		codeInfo.append("package " + uriToPackage(entityPackage) + ";" + "\r\n");
			if (Materi.indexOf("{*cname*}") != -1) {
				Materi = Materi.replace("{*cname*}", className);
			}
			if (Materi.indexOf("{*serializable*}") != -1) {
				Materi = Materi.replace("{*serializable*}", serializable);
			}
			if (Materi.indexOf("{*propertys*}") != -1) {
				Materi = Materi.replace("{*propertys*}", property);
			}
			if (Materi.indexOf("{*constr*}") != -1) {
				Materi = Materi.replace("{*constr*}", constr);
			}
			if (Materi.indexOf("{*constrAll*}") != -1) {
				Materi = Materi.replace("{*constrAll*}", constrAll);
			}
			if (Materi.indexOf("{*getSet*}") != -1) {
				Materi = Materi.replace("{*getSet*}", getSet);
			}
			codeInfo.append(Materi + "\r\n");
		Files.write(path, codeInfo.toString().getBytes());
	}

	/**
	 * 初始化并创建dao
	 * 
	 * @param supAttr
	 * @throws Exception
	 */
	private void createDao(List<SuperAttribute> supAttr) throws Exception {
		String[] ename = new String[supAttr.size()];
		String[][] nameAndType = new String[supAttr.size()][2];
		for (int i = 0; i < supAttr.size(); i++) {
			ename[i] = supAttr.get(i).getClassName();
			nameAndType[i][0] = supAttr.get(i).getClassName();
			// 如果主键不为空的话通过列名获得java数据类型
			if (supAttr.get(i).getPrimaryKey() != null) {
				List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
				for (int j = 0; j < cvf.size(); j++) {
					if (cvf.get(j).getConlumn() != null) {
						if (supAttr.get(i).getPrimaryKey().equals(cvf.get(j).getConlumn())) {
							nameAndType[i][1] = cvf.get(j).getJavaTypeValue();
						}
					}

				}
			} else {
				nameAndType[i][1] = null;
			}
		}

		String imp = "";// 需要导入的包
		String rowCount = "";// 获得总行数方法
		String select = "";// 获得查询方法
		String insert = "";// 获得插入方法
		String delete = "";// 获得删除方法
		String update = "";// 获得更新方法

		// 创建工厂
		DaoFactory daoFactory;
		if (createAssist) {
			daoFactory = new DaoMateriHaveAssist();
		} else {
			daoFactory = new DaoMateriNotHaveAssist();
		}
		List<String> packages = new ArrayList<String>();
		for (String page : ename) {
			packages.add(uriToPackage(entityPackage) + "." + page);
		}
		if (createAssist) {
			packages.add(uriToPackage(assistPackage) + "." + assistName);
			packages.add("org.apache.ibatis.annotations.Param");
		}
		imp = daoFactory.getImport(packages);
		rowCount = daoFactory.getRowCount(ename);
		select = daoFactory.getSelect(ename);
		select += daoFactory.getSelectById(nameAndType);
		insert = daoFactory.getInsert(ename);
		insert += daoFactory.getInsertNonEmpty(ename);
		delete = daoFactory.getDelete(ename);
		delete += daoFactory.getDeleteById(nameAndType);
		update = daoFactory.getUpdate(ename);
		update += daoFactory.getUpdateNonEmpty(ename);
		update += daoFactory.getUpdateById(ename);
		update += daoFactory.getUpdateNonEmptyById(ename);

		Path path = Paths.get(projectPath, projectRoot, daoPackage, daoName + ".java");
		runCreateDao(path, imp, daoName, rowCount, select, insert, delete, update);

	}

	/**
	 * 执行创建dao
	 * 
	 * @param path
	 * @param imp
	 * @param dname
	 * @param rowCount
	 * @param select
	 * @param insert
	 * @param delete
	 * @param update
	 * @throws Exception
	 */
	private void runCreateDao(Path path, String imp, String daoName, String rowCount, String select, String insert,
			String delete, String update) throws Exception {
		String Materi = readToString("pers/resource/models/daoMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		codeInfo.append("package " + uriToPackage(daoPackage) + ";" + "\r\n");
		if (Materi.indexOf("{*import*}") != -1) {
			Materi = Materi.replace("{*import*}", imp);
		}
		if (Materi.indexOf("{*dname*}") != -1) {
			Materi = Materi.replace("{*dname*}", daoName);
		}
		if (Materi.indexOf("{*count*}") != -1) {
			Materi = Materi.replace("{*count*}", rowCount);
		}
		if (Materi.indexOf("{*select*}") != -1) {
			Materi = Materi.replace("{*select*}", select);
		}
		if (Materi.indexOf("{*insert*}") != -1) {
			Materi = Materi.replace("{*insert*}", insert);
		}
		if (Materi.indexOf("{*delete*}") != -1) {
			Materi = Materi.replace("{*delete*}", delete);
		}
		if (Materi.indexOf("{*update*}") != -1) {
			Materi = Materi.replace("{*update*}", update);
		}
		codeInfo.append(Materi + "\r\n");
		Files.write(path, codeInfo.toString().getBytes());
	}

	/**
	 * 初始化生成mapper
	 * 
	 * @param supAttr
	 * @throws Exception
	 */
	private void CreateMapper(List<SuperAttribute> supAttr) throws Exception {
		if (supAttr == null || supAttr.size() < 1) {
			return;
		}
		String space = uriToPackage(daoPackage) + "." + daoName;// 获得命名空间
		String resultMap;// resultMap字符串
		String sql;// sql字符串
		String select;// select字符串
		String insert;// insert字符串
		String update;// update字符串
		String delete;// delete字符串

		MapperFactory factory;
		if (supAttr.get(0).isCreateJDBCType()) {
			factory = new MapperMateriHaveJDBC(createAssist);
		} else {
			factory = new MapperMateriNoJDBC(createAssist);
		}
		resultMap = factory.getResultMap(uriToPackage(entityPackage), supAttr);
		sql = factory.getSQL(supAttr);
		select = factory.getSelect(supAttr, uriToPackage(assistPackage), uriToPackage(entityPackage),
				databaseConfig.getDbType());
		insert = factory.getInsert(supAttr, uriToPackage(entityPackage));
		update = factory.getUpdate(supAttr, uriToPackage(entityPackage));
		delete = factory.getDelete(supAttr, uriToPackage(assistPackage), uriToPackage(entityPackage));

		Path path = Paths.get(projectPath, projectRoot, mapPackage, mapName + ".xml");

		runCreateMapper(path, space, resultMap, sql, select, insert, update, delete);
	}

	/**
	 * 执行创建mapper
	 * 
	 * @param path
	 * @param space
	 * @param resultMap
	 * @param sql
	 * @param select
	 * @param insert
	 * @param update
	 * @param delete
	 * @throws Exception
	 */
	private void runCreateMapper(Path path, String space, String resultMap, String sql, String select, String insert,
			String update, String delete) throws Exception {
		String Materi = readToString("pers/resource/models/mapperMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		if (Materi.indexOf("{*namespace*}") != -1) {
			Materi = Materi.replace("{*namespace*}", space);
		}
		if (Materi.indexOf("{*resultMap*}") != -1) {
			Materi = Materi.replace("{*resultMap*}", resultMap);
		}
		if (Materi.indexOf("{*sql*}") != -1) {
			Materi = Materi.replace("{*sql*}", sql);
		}
		if (Materi.indexOf("{*select*}") != -1) {
			Materi = Materi.replace("{*select*}", select);
		}
		if (Materi.indexOf("{*insert*}") != -1) {
			Materi = Materi.replace("{*insert*}", insert);
		}
		if (Materi.indexOf("{*delete*}") != -1) {
			Materi = Materi.replace("{*delete*}", delete);
		}
		if (Materi.indexOf("{*update*}") != -1) {
			Materi = Materi.replace("{*update*}", update);
		}
		codeInfo.append(Materi + "\r\n");
		Files.write(path, codeInfo.toString().getBytes());
	}

	/**
	 * 创建Assist帮助类
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void createAssist(Path path) throws Exception {
		String Materi = readToString("pers/resource/models/AssistMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		codeInfo.append("package " + uriToPackage(assistPackage) + ";" + "\r\n");
		codeInfo.append(Materi + "\r\n");
		Files.write(path, codeInfo.toString().getBytes());
	}

	/**
	 * 创建mybatis配置文件
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void createMyBatisConfig(Path path) throws Exception {
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
			Materi = Materi.replace("{*mapper*}", mapPackage + mapName + ".xml");
		}

		codeInfo.append(Materi + "\r\n");

		Files.write(path, codeInfo.toString().getBytes());
	}

	/**
	 * 创建帮助工具
	 * 
	 * @throws Exception
	 * 
	 */
	private void createMyBatisUtil(Path path) throws Exception {
		String Materi = readToString("pers/resource/models/MyBatisUtilMateri.txt");
		StringBuilder codeInfo = new StringBuilder();
		codeInfo.append("package " + uriToPackage(myUtilPackage) + ";" + "\r\n");

		if (Materi.indexOf("{*resource*}") != -1) {
			Materi = Materi.replace("{*resource*}", configPackage + configName);
		}
		codeInfo.append(Materi + "\r\n");

		Files.write(path, codeInfo.toString().getBytes());
	}

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
	public String readToString(String path) throws Exception {
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

	// -----------------constructor/init-------------------------------
	public FileFactory() {
		super();
	}

	/**
	 * 初始化数据
	 * 
	 * @param daoPackage
	 * @param daoName
	 * @param mapPackage
	 * @param mapName
	 * @param assistPackage
	 * @param assistName
	 * @param configPackage
	 * @param configName
	 * @param myUtilPackage
	 * @param myUtilName
	 */
	public void init(DatabaseConfig databaseConfig, String entityPackage, String daoPackage, String daoName,
			String mapPackage, String mapName, String assistPackage, String assistName, String configPackage,
			String configName, String myUtilPackage, String myUtilName) {
		this.databaseConfig = databaseConfig;
		setEntityPackage(entityPackage);
		setDaoPackage(daoPackage);
		setDaoName(daoName);
		setMapPackage(mapPackage);
		setMapName(mapName);
		setAssistPackage(assistPackage);
		setAssistName(assistName);
		setConfigPackage(configPackage);
		setConfigName(configName);
		setMyUtilPackage(myUtilPackage);
		setMyUtilName(myUtilName);
	}

	/**
	 * 项目路径,根目录,是否创建assist,是否创建config,是否创建mybatisUtil;
	 * 
	 * @param projectPath
	 * @param projectRoot
	 * @param createAssist
	 * @param createConfig
	 * @param createMyUtil
	 */
	public FileFactory(String projectPath, String projectRoot, boolean createAssist, boolean createConfig,
			boolean createMyUtil) {
		super();
		setProjectPath(projectPath);
		this.projectRoot = projectRoot;
		this.createAssist = createAssist;
		this.createConfig = createConfig;
		this.createMyUtil = createMyUtil;
	}

	public FileFactory(String projectPath, String projectRoot, String entityPackage, String entityName,
			String daoPackage, String daoName, String mapPackage, String mapName, boolean createAssist,
			String assistPackage, String assistName, boolean createConfig, String configPackage, String configName,
			boolean createMyUtil, String myUtilPackage, String myUtilName) {
		super();
		setProjectPath(projectPath);
		this.projectRoot = projectRoot;
		setEntityPackage(entityPackage);
		this.entityName = entityName;
		setDaoPackage(daoPackage);
		this.daoName = daoName;
		setMapPackage(mapPackage);
		this.mapName = mapName;
		this.createAssist = createAssist;
		setAssistPackage(assistPackage);
		this.assistName = assistName;
		this.createConfig = createConfig;
		setConfigPackage(configPackage);
		this.configName = configName;
		this.createMyUtil = createMyUtil;
		setMyUtilPackage(myUtilPackage);
		this.myUtilName = myUtilName;
	}

	// ----------------------get/set---------------------------------

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath + "/";
	}

	public String getProjectRoot() {
		return projectRoot;
	}

	public void setProjectRoot(String projectRoot) {
		this.projectRoot = projectRoot;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = packageToUri(entityPackage);
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getDaoPackage() {
		return daoPackage;
	}

	public void setDaoPackage(String daoPackage) {
		this.daoPackage = packageToUri(daoPackage);
	}

	public String getDaoName() {
		return daoName;
	}

	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}

	public String getMapPackage() {
		return mapPackage;
	}

	public void setMapPackage(String mapPackage) {
		this.mapPackage = packageToUri(mapPackage);
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
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

}
