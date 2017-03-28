package pers.cmeu.models;

public class HistoryConfig {
	private String historyConfigName;
	private String projectPath;
	private String rootDir;
	private String daoPackage;
	private String daoName;
	private String entityPackage;
	private String entityName;
	private String mapPackage;
	private String mapName;
	private String assistPackage;
	private String assistName;
	private String configPackage;
	private String configName;
	private String myUtilPackage;
	private String myUtilName;
	private boolean isAssist;
	private boolean isConfig;
	private boolean isMyUtil;
	
	
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getHistoryConfigName() {
		return historyConfigName;
	}
	public void setHistoryConfigName(String historyConfigName) {
		this.historyConfigName = historyConfigName;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public String getRootDir() {
		return rootDir;
	}
	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	public String getDaoPackage() {
		return daoPackage;
	}
	public void setDaoPackage(String daoPackage) {
		this.daoPackage = daoPackage;
	}
	public String getDaoName() {
		return daoName;
	}
	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}
	public String getEntityPackage() {
		return entityPackage;
	}
	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getMapPackage() {
		return mapPackage;
	}
	public void setMapPackage(String mapPackage) {
		this.mapPackage = mapPackage;
	}
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	public String getAssistPackage() {
		return assistPackage;
	}
	public void setAssistPackage(String assistPackage) {
		this.assistPackage = assistPackage;
	}
	public String getAssistName() {
		return assistName;
	}
	public void setAssistName(String assistName) {
		this.assistName = assistName;
	}
	public String getConfigPackage() {
		return configPackage;
	}
	public void setConfigPackage(String configPackage) {
		this.configPackage = configPackage;
	}
	public String getMyUtilPackage() {
		return myUtilPackage;
	}
	public void setMyUtilPackage(String myUtilPackage) {
		this.myUtilPackage = myUtilPackage;
	}
	public String getMyUtilName() {
		return myUtilName;
	}
	public void setMyUtilName(String myUtilName) {
		this.myUtilName = myUtilName;
	}

	public boolean isAssist() {
		return isAssist;
	}
	public void setAssist(boolean isAssist) {
		this.isAssist = isAssist;
	}
	public boolean isConfig() {
		return isConfig;
	}
	public void setConfig(boolean isConfig) {
		this.isConfig = isConfig;
	}
	public boolean isMyUtil() {
		return isMyUtil;
	}
	public void setMyUtil(boolean isMyUtil) {
		this.isMyUtil = isMyUtil;
	}
	
	
	public HistoryConfig() {
		super();
	}
	/**
	 * 没有配置文件名称的构造方法
	 * @param projectPath
	 * @param rootDir
	 * @param daoPackage
	 * @param daoName
	 * @param entityPackage
	 * @param entityName
	 * @param mapPackage
	 * @param mapName
	 * @param assistPackage
	 * @param assistName
	 * @param configPackage
	 * @param configName
	 * @param myUtilPackage
	 * @param myUtilName
	 * @param isAssist
	 * @param isConfig
	 * @param isMyUtil
	 */
	public HistoryConfig(String projectPath, String rootDir, String daoPackage, String daoName, String entityPackage,
			String entityName, String mapPackage, String mapName, String assistPackage, String assistName,
			String configPackage, String configName, String myUtilPackage, String myUtilName, boolean isAssist,
			boolean isConfig, boolean isMyUtil) {
		super();
		this.projectPath = projectPath;
		this.rootDir = rootDir;
		this.daoPackage = daoPackage;
		this.daoName = daoName;
		this.entityPackage = entityPackage;
		this.entityName = entityName;
		this.mapPackage = mapPackage;
		this.mapName = mapName;
		this.assistPackage = assistPackage;
		this.assistName = assistName;
		this.configPackage = configPackage;
		this.configName = configName;
		this.myUtilPackage = myUtilPackage;
		this.myUtilName = myUtilName;
		this.isAssist = isAssist;
		this.isConfig = isConfig;
		this.isMyUtil = isMyUtil;
	}
	
	
	

}
