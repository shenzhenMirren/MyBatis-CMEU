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
	private String primaryKey;
	private List<AttributeCVF> attributes;
	
	
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
	
	
	
	
	

	
}
