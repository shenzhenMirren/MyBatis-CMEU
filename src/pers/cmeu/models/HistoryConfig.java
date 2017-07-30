package pers.cmeu.models;

public class HistoryConfig {
	private String historyConfigName;
	private String projectPath;
	private String rootDir;
	private String daoPackage;
	private String daoName;
	private String servicePackage;
	private String serviceName;
	private String serviceImplPackage;
	private String serviceImplName;
	private String entityPackage;
	private String entityName;
	private String mapPackage;
	private String mapName;
	private String updateMapper;
	private String assistPackage;
	private String assistName;
	private String configPackage;
	private String configName;
	private String myUtilPackage;
	private String myUtilName;
	private boolean isService;
	private boolean isSpringAnno;
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
	
	public String getUpdateMapper() {
		return updateMapper;
	}
	public void setUpdateMapper(String updateMapper) {
		this.updateMapper = updateMapper;
	}
	public String getServicePackage() {
		return servicePackage;
	}
	public void setServicePackage(String servicePackage) {
		this.servicePackage = servicePackage;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceImplPackage() {
		return serviceImplPackage;
	}
	public void setServiceImplPackage(String serviceImplPackage) {
		this.serviceImplPackage = serviceImplPackage;
	}
	public String getServiceImplName() {
		return serviceImplName;
	}
	public void setServiceImplName(String serviceImplName) {
		this.serviceImplName = serviceImplName;
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
	
	
	public boolean isService() {
		return isService;
	}
	public void setService(boolean isService) {
		this.isService = isService;
	}
	
	public boolean isSpringAnno() {
		return isSpringAnno;
	}
	public void setSpringAnno(boolean isSpringAnno) {
		this.isSpringAnno = isSpringAnno;
	}
	public HistoryConfig() {
		super();
	}
	public HistoryConfig(String projectPath, String rootDir, String daoPackage, String daoName, String servicePackage,
			String serviceName, String serviceImplPackage, String serviceImplName, String entityPackage,
			String entityName, String mapPackage, String mapName,String updateMapper, String assistPackage, String assistName,
			String configPackage, String configName, String myUtilPackage, String myUtilName, boolean isService,boolean isSpringAnno,
			boolean isAssist, boolean isConfig, boolean isMyUtil) {
		super();
		this.projectPath = projectPath;
		this.rootDir = rootDir;
		this.daoPackage = daoPackage;
		this.daoName = daoName;
		this.servicePackage = servicePackage;
		this.serviceName = serviceName;
		this.serviceImplPackage = serviceImplPackage;
		this.serviceImplName = serviceImplName;
		this.entityPackage = entityPackage;
		this.entityName = entityName;
		this.mapPackage = mapPackage;
		this.mapName = mapName;
		this.updateMapper=updateMapper;
		this.assistPackage = assistPackage;
		this.assistName = assistName;
		this.configPackage = configPackage;
		this.configName = configName;
		this.myUtilPackage = myUtilPackage;
		this.myUtilName = myUtilName;
		this.isService = isService;
		this.isSpringAnno = isSpringAnno;
		this.isAssist = isAssist;
		this.isConfig = isConfig;
		this.isMyUtil = isMyUtil;
	}

	

}
