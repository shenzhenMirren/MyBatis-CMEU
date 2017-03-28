package pers.cmeu.common;

import java.util.List;

import pers.cmeu.models.SuperAttribute;

public abstract class MapperFactory {
	/**
	 * 获得resultMap;espace为实体类的包名
	 * 
	 * @param espace
	 * @param supAttr
	 * @return
	 */
	public abstract String getResultMap(String espace, List<SuperAttribute> supAttr);

	/**
	 * 获sql模块
	 * 
	 * @return
	 */
	public abstract String getSQL(List<SuperAttribute> supAttr);
	

	/**
	 * 
	 * @param supAttr
	 * @param asSpace Assist包名
	 * @param espace entity包名
	 * @param dbType 数据库类型
	 * @return
	 */
	public abstract String getSelect(List<SuperAttribute> supAttr, String asSpace, String espace,String dbType);

	/**
	 * 获得插入语句
	 * @param supAttr
	 * @param espace entity包名
	 * @return
	 */
	public abstract String getInsert(List<SuperAttribute> supAttr, String espace);
	
	/**
	 * 获得删除语句
	 * @param supAttr
	 * @param asSpace Assist包名
	 * @param espace entity包名
	 * @return
	 */
	public abstract String getDelete(List<SuperAttribute> supAttr, String asSpace, String espace);
	
	/**
	 * 获得删除语句
	 * @param supAttr
	 * @param espace entity包名
	 * @return
	 */
	public abstract String getUpdate(List<SuperAttribute> supAttr,String espace);
}
