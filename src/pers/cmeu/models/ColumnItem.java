package pers.cmeu.models;

import java.util.List;

public class ColumnItem {
	private boolean anyAssociation;//true代表association,false代表collection
	private boolean anyHasColl;//true代表存在collection,false代表不存在,用于表示是否需要创建dao层分页嵌套查询接口
	private String primaryKey;
	private String className;
	private String inPropertyName;
	private String tableName;
	
	private String joinType;
	private String joinTableName;
	private String joinColumn;
	
	private List<AttributeCVF> attributeCVFs;
	private List<ColumnItem> grandItem;//第三层关系属性
	public boolean isAnyAssociation() {
		return anyAssociation;
	}
	public void setAnyAssociation(boolean anyAssociation) {
		this.anyAssociation = anyAssociation;
	}
	
	
	public boolean isAnyHasColl() {
		return anyHasColl;
	}
	public void setAnyHasColl(boolean anyHasColl) {
		this.anyHasColl = anyHasColl;
	}
	public String getJoinType() {
		return joinType;
	}
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	
	public String getJoinTableName() {
		return joinTableName;
	}
	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}
	public String getJoinColumn() {
		return joinColumn;
	}
	public void setJoinColumn(String joinColumn) {
		this.joinColumn = joinColumn;
	}
	public List<ColumnItem> getGrandItem() {
		return grandItem;
	}
	public void setGrandItem(List<ColumnItem> grandItem) {
		this.grandItem = grandItem;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getInPropertyName() {
		return inPropertyName;
	}
	public void setInPropertyName(String inPropertyName) {
		this.inPropertyName = inPropertyName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<AttributeCVF> getAttributeCVFs() {
		return attributeCVFs;
	}
	public void setAttributeCVFs(List<AttributeCVF> attributeCVFs) {
		this.attributeCVFs = attributeCVFs;
	}
	
}
