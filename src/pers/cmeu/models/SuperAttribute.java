package pers.cmeu.models;

import java.util.List;

public class SuperAttribute {
	
	private boolean isCamel=true;
	private boolean isSerializable=false;
	private boolean isCreateJDBCType=false;
	private boolean isCreateGetSet=true;
	private boolean isConstruct=true;
	private boolean isConstructAll=true;
	
	private String className;
	private String tableName;
	private String tableAlias;
	private String primaryKey;
	private String daoName;
	private String mapperName;
	private String serviceName;
	private String serviceImplName;
	private String joinType;
	private String joinColumn;
	private String selectKey;
	private List<AttributeCVF> attributes;
	private List<ColumnItem> columnItems;
	private boolean anyHasColl;//true代表即将创建的类中存在集合,false代表不存在,用于表示是否需要创建dao层分页嵌套查询接口
	
	public SuperAttribute() {
		super();
	}
	public SuperAttribute(boolean isCamel, boolean isCreateJDBCType, boolean isCreateGetSet, boolean isConstruct,
			boolean isConstructAll, String className, String tableName, String primaryKey,
			List<AttributeCVF> attributes) {
		super();
		this.isCamel = isCamel;
		this.isCreateJDBCType = isCreateJDBCType;
		this.isCreateGetSet = isCreateGetSet;
		this.isConstruct = isConstruct;
		this.isConstructAll = isConstructAll;
		this.className = className;
		this.tableName = tableName;
		this.primaryKey = primaryKey;
		this.attributes = attributes;
	}
	
	
	public String getTableAlias() {
		return tableAlias;
	}
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}
	public List<ColumnItem> getColumnItems() {
		return columnItems;
	}
	public void setColumnItems(List<ColumnItem> columnItems) {
		this.columnItems = columnItems;
	}
	public String getSelectKey() {
		return selectKey;
	}
	public void setSelectKey(String selectKey) {
		this.selectKey = selectKey;
	}
	public String getDaoName() {
		return daoName;
	}
	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}
	public String getMapperName() {
		return mapperName;
	}
	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceImplName() {
		return serviceImplName;
	}
	public void setServiceImplName(String serviceImplName) {
		this.serviceImplName = serviceImplName;
	}
	public String getJoinType() {
		return joinType;
	}
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	public String getJoinColumn() {
		return joinColumn;
	}
	public void setJoinColumn(String joinColumn) {
		this.joinColumn = joinColumn;
	}
	public boolean isCamel() {
		return isCamel;
	}
	public void setCamel(boolean isCamel) {
		this.isCamel = isCamel;
	}	
	
	public boolean isSerializable() {
		return isSerializable;
	}
	public void setSerializable(boolean isSerializable) {
		this.isSerializable = isSerializable;
	}
	public boolean isCreateJDBCType() {
		return isCreateJDBCType;
	}
	public void setCreateJDBCType(boolean isCreateJDBCType) {
		this.isCreateJDBCType = isCreateJDBCType;
	}
	public boolean isCreateGetSet() {
		return isCreateGetSet;
	}
	public void setCreateGetSet(boolean isCreateGetSet) {
		this.isCreateGetSet = isCreateGetSet;
	}
	public boolean isConstruct() {
		return isConstruct;
	}
	public void setConstruct(boolean isConstruct) {
		this.isConstruct = isConstruct;
	}
	public boolean isConstructAll() {
		return isConstructAll;
	}
	public void setConstructAll(boolean isConstructAll) {
		this.isConstructAll = isConstructAll;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public List<AttributeCVF> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeCVF> attributes) {
		this.attributes = attributes;
	}
	public boolean isAnyHasColl() {
		return anyHasColl;
	}
	public void setAnyHasColl(boolean anyHasColl) {
		this.anyHasColl = anyHasColl;
	}

	
	
	
	
	

	
}
