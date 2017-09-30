package pers.cmeu.common;

import java.util.List;

import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.ColumnItem;
import pers.cmeu.models.SuperAttribute;

public class MapperUtil {
	private MapperUtil() {
	};

	private static MapperUtil mapperUtil = null;

	public static MapperUtil getInstance() {
		if (mapperUtil == null) {
			synchronized (MapperUtil.class) {
				if (mapperUtil == null) {
					mapperUtil = new MapperUtil();
				}
			}
		}
		return mapperUtil;
	}

	/**
	 * 获得mapper语句
	 * 
	 * @param daoNameAndSpace
	 * @param entitySpace
	 * @param assistSpace
	 * @param dbtype
	 * @param attribute
	 * @param anyAssist
	 * @param anyJDBC
	 * @return
	 */
	public String getMapperString(String daoNameAndSpace, String entitySpace, String assistSpace, String dbtype,
			SuperAttribute attribute, boolean anyAssist, boolean anyJDBC) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n"
				+ "<mapper namespace=\"" + daoNameAndSpace + "\">\r\n");
		buffer.append(createResultMap(entitySpace, anyJDBC, attribute));
		if (attribute.isAnyHasColl()) {
			buffer.append(createResultOfPagingMap(entitySpace, anyJDBC, attribute));
		}
		buffer.append(createSql(anyAssist, attribute));
		buffer.append(createRowCount(assistSpace, attribute, anyAssist));
		buffer.append(createSelectSql(attribute, assistSpace, dbtype, anyJDBC, anyAssist));
		if (attribute.isAnyHasColl()) {
			buffer.append(createSelectOfPagingSql(attribute, assistSpace, dbtype, anyJDBC, anyAssist));
			buffer.append(createCollectionNeedSelectSql(attribute, anyJDBC));
		}
		buffer.append(createSelectByObj(attribute, entitySpace, anyJDBC));
		buffer.append(createSelectById(attribute, anyJDBC));
		buffer.append(createInsertAll(attribute, entitySpace, anyJDBC));
		buffer.append(createInsertNonEmpty(attribute, entitySpace, anyJDBC));
		buffer.append(createInsertBatch(attribute, entitySpace, anyJDBC, dbtype));
		buffer.append(createDeleteById(attribute, entitySpace, anyJDBC));
		if (anyAssist) {
			buffer.append(createDeleteByAssist(attribute, assistSpace));
		}
		buffer.append(createUpdateById(attribute, entitySpace, anyJDBC));
		buffer.append(createUpdateNonEmptyById(attribute, entitySpace, anyJDBC));
		if (anyAssist) {
			buffer.append(createUpdateByAssist(attribute, anyJDBC));
			buffer.append(createUpdateNonEmptyByAssist(attribute, anyJDBC));
		}
		buffer.append("</mapper>");
		return buffer.toString();
	}

	/**
	 * 创建resultMap方法
	 * 
	 * @param entitySpace
	 * @param anyJDBC
	 * @param attr
	 * @return
	 */
	private String createResultMap(String entitySpace, boolean anyJDBC, SuperAttribute attr) {
		if (attr.getAttributes() == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		List<AttributeCVF> item = attr.getAttributes();
		// 创建resultMap
		result.append("    <!-- " + attr.getClassName() + "的resultMap,column是给数据库列起的别名,它对应property类的属性-->\r\n");
		result.append("    <resultMap id=\"result_" + attr.getClassName() + "_Map\" type=\"" + entitySpace + "."
				+ attr.getClassName() + "\">\r\n");
		// 创建resultMap的普通属性
		for (int i = 0; i < item.size(); i++) {
			if (item.get(i).getConlumn() == null || item.get(i).getConlumn() == "" || item.get(i).getCheck() == false) {
				continue;
			}
			if (attr.getColumnItems() != null) {
				if (i == 0 && item.get(i).getConlumn().equals(attr.getPrimaryKey())) {
					if (attr.getTableAlias() != null && !(attr.getTableAlias().isEmpty())) {
						result.append(
								"        <id column=\"" + attr.getTableAlias() + "_" + item.get(i).getConlumn() + "\"");
					} else {
						result.append(
								"        <!-- 当你看到column=类名+数字0123时不要问这是什么鬼,请看文档==>  http://mybatiscmeu.com/  ,中=>修改属性页说明==>表的别名   ;它是MyBatis-CMEU生成多表关联时防止列名超长或者列名重复的策略,可以自义定表的别名,就会以表的别名_表列名命名  -->\r\n");
						result.append("        <id column=\"" + attr.getClassName() + i + "\"");
					}

					if (anyJDBC) {
						result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
					continue;
				}
				if (attr.getTableAlias() != null && !(attr.getTableAlias().isEmpty())) {
					result.append(
							"        <result column=\"" + attr.getTableAlias() + "_" + item.get(i).getConlumn() + "\"");
				} else {
					result.append("        <result column=\"" + attr.getClassName() + i + "\"");
				}
				if (anyJDBC) {
					result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
				}
				result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
			} else {
				if (i == 0 && item.get(i).getConlumn().equals(attr.getPrimaryKey())) {
					result.append("        <id column=\"" + item.get(i).getConlumn() + "\"");
					if (anyJDBC) {
						result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
					continue;
				}
				result.append("        <result column=\"" + item.get(i).getConlumn() + "\"");
				if (anyJDBC) {
					result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
				}
				result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
			}
		}
		if (attr.getColumnItems() != null) {
			// 创建resultMap里面的association/collection
			result.append(addSonMap(entitySpace, attr.getColumnItems(), anyJDBC));
		}
		result.append("    </resultMap>\r\n\r\n");
		return result.toString();
	}

	/**
	 * 添加子类ResultMap
	 * 
	 * @param item
	 * @param anyAs
	 * @return
	 */
	private StringBuffer addSonMap(String entitySpace, List<ColumnItem> item, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		if (item == null) {
			return result;
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == false) {
					continue;
				}
				result.append("        <association property=\"" + col.getInPropertyName() + "\" javaType=\""
						+ entitySpace + "." + col.getClassName() + "\">\r\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getConlumn() == null || list.get(i).getConlumn() == ""
							|| list.get(i).getCheck() == false) {
						continue;
					}
					if (i == 0 && list.get(i).getConlumn().equals(col.getPrimaryKey())) {
						if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
							result.append("            <id column=\"" + col.getTableAlias() + "_"
									+ list.get(i).getConlumn() + "\"");
						} else {
							result.append("            <id column=\"" + col.getClassName() + i + "\"");
						}
						if (anyJDBC) {
							result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
						}
						result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
						continue;
					}
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append("            <result column=\"" + col.getTableAlias() + "_"
								+ list.get(i).getConlumn() + "\"");
					} else {
						result.append("            <result column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
				}
				if (col.getGrandItem() != null) {
					result.append(addGrandMap(entitySpace, col.getGrandItem(), anyJDBC));
				}
				result.append("        </association>\r\n");
			}
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == true) {
					continue;
				}
				result.append("        <collection property=\"" + col.getInPropertyName() + "\" ofType=\"" + entitySpace
						+ "." + col.getClassName() + "\">\r\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getConlumn() == null || list.get(i).getConlumn() == ""
							|| list.get(i).getCheck() == false) {
						continue;
					}
					if (i == 0 && list.get(i).getConlumn().equals(col.getPrimaryKey())) {
						if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
							result.append("            <id column=\"" + col.getTableAlias() + "_"
									+ list.get(i).getConlumn() + "\"");
						} else {
							result.append("            <id column=\"" + col.getClassName() + i + "\"");
						}
						if (anyJDBC) {
							result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
						}
						result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
						continue;
					}
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append("            <result column=\"" + col.getTableAlias() + "_"
								+ list.get(i).getConlumn() + "\"");
					} else {
						result.append("            <result column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
				}
				if (col.getGrandItem() != null) {
					result.append(addGrandMap(entitySpace, col.getGrandItem(), anyJDBC));
				}
				result.append("        </collection>\r\n");
			}
		}

		return result;
	}

	/**
	 * 添加孙类ResultMap
	 * 
	 * @param item
	 * @param anyAs
	 * @return
	 */
	private StringBuffer addGrandMap(String entitySpace, List<ColumnItem> item, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		if (item == null) {
			return result;
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == false) {
					continue;
				}
				result.append("            <association property=\"" + col.getInPropertyName() + "\" javaType=\""
						+ entitySpace + "." + col.getClassName() + "\">\r\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getConlumn() == null || list.get(i).getConlumn() == ""
							|| list.get(i).getCheck() == false) {
						continue;
					}
					if (i == 0 && list.get(i).getConlumn().equals(col.getPrimaryKey())) {
						if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
							result.append("                <id column=\"" + col.getTableAlias() + "_"
									+ list.get(i).getConlumn() + "\"");
						} else {
							result.append("                <id column=\"" + col.getClassName() + i + "\"");
						}
						if (anyJDBC) {
							result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
						}
						result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
						continue;
					}
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append("                <result column=\"" + col.getTableAlias() + "_"
								+ list.get(i).getConlumn() + "\"");
					} else {
						result.append("                <result column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
				}
				result.append("            </association>\r\n");
			}
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == true) {
					continue;
				}
				result.append("            <collection property=\"" + col.getInPropertyName() + "\" ofType=\""
						+ entitySpace + "." + col.getClassName() + "\">\r\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getConlumn() == null || list.get(i).getConlumn() == ""
							|| list.get(i).getCheck() == false) {
						continue;
					}
					if (i == 0 && list.get(i).getConlumn().equals(col.getPrimaryKey())) {
						if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
							result.append("                <id column=\"" + col.getTableAlias() + "_"
									+ list.get(i).getConlumn() + "\"");
						} else {
							result.append("                <id column=\"" + col.getClassName() + i + "\"");
						}
						if (anyJDBC) {
							result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
						}
						result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
						continue;
					}
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append("                <result column=\"" + col.getTableAlias() + "_"
								+ list.get(i).getConlumn() + "\"");
					} else {
						result.append("                <result column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
				}
				result.append("            </collection>\r\n");
			}
		}

		return result;
	}

	/**
	 * 创建resultMap方法
	 * 
	 * @param entitySpace
	 * @param anyJDBC
	 * @param attr
	 * @return
	 */
	private String createResultOfPagingMap(String entitySpace, boolean anyJDBC, SuperAttribute attr) {
		if (attr.getAttributes() == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		List<AttributeCVF> item = attr.getAttributes();
		// 创建resultMap
		result.append(
				"    <!-- " + attr.getClassName() + "的resultMap,该resultMap是专门用在有多表关联的地方,它保证分页时数据映射不会丢失或者重复 -->\r\n");
		result.append("    <resultMap id=\"result_" + attr.getClassName() + "OfPaging_Map\" type=\"" + entitySpace + "."
				+ attr.getClassName() + "\">\r\n");
		String tempSelectChildrenColumnId = null;
		// 创建resultMap的普通属性
		for (int i = 0; i < item.size(); i++) {
			if (item.get(i).getConlumn() == null || item.get(i).getConlumn() == "" || item.get(i).getCheck() == false) {
				continue;
			}
			if (i == 0 && item.get(i).getConlumn().equals(attr.getPrimaryKey())) {
				if (attr.getTableAlias() != null && !(attr.getTableAlias().isEmpty())) {
					result.append(
							"        <id column=\"" + attr.getTableAlias() + "_" + item.get(i).getConlumn() + "\"");
				} else {
					result.append("        <id column=\"" + attr.getClassName() + i + "\"");
				}

				tempSelectChildrenColumnId = attr.getClassName() + i;
				if (anyJDBC) {
					result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
				}
				result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
				continue;
			}
			if (attr.getTableAlias() != null && !(attr.getTableAlias().isEmpty())) {
				result.append(
						"        <result column=\"" + attr.getTableAlias() + "_" + item.get(i).getConlumn() + "\"");
			} else {
				result.append("        <result column=\"" + attr.getClassName() + i + "\"");
			}
			if (anyJDBC) {
				result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
			}
			result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
		}
		if (attr.getColumnItems() != null) {
			// 创建resultMap里面的association/collection
			result.append(addSonOfPagingMap(entitySpace, attr.getColumnItems(), anyJDBC, tempSelectChildrenColumnId));
		}
		result.append("    </resultMap>\r\n\r\n");
		result.append(createNeedOfPagingResultMap(entitySpace, attr.getColumnItems(), anyJDBC));
		return result.toString();
	}

	/**
	 * 获得分页查询子表需要返回的resultMap
	 * 
	 * @param entitySpace
	 * @param item
	 * @param anyJDBC
	 * @return
	 */
	private String createNeedOfPagingResultMap(String entitySpace, List<ColumnItem> columnItem, boolean anyJDBC) {
		if (columnItem == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (ColumnItem col : columnItem) {
			// 创建resultMap
			result.append(
					"    <!-- " + col.getClassName() + "的resultMap,该resultMap是专门用在有多表关联的地方,它保证分页时数据映射不会丢失或者重复 -->\r\n");

			result.append("    <resultMap id=\"result_" + col.getClassName() + "OfPaging_Map\" type=\"" + entitySpace
					+ "." + col.getClassName() + "\">\r\n");
			List<AttributeCVF> item = col.getAttributeCVFs();
			// 创建resultMap的普通属性
			for (int i = 0; i < item.size(); i++) {
				if (item.get(i).getConlumn() == null || item.get(i).getConlumn() == ""
						|| item.get(i).getCheck() == false) {
					continue;
				}
				if (i == 0 && item.get(i).getConlumn().equals(col.getPrimaryKey())) {
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append(
								"        <id column=\"" + col.getTableAlias() + "_" + item.get(i).getConlumn() + "\"");
					} else {
						result.append("        <id column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
					continue;
				}
				if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
					result.append(
							"        <result column=\"" + col.getTableAlias() + "_" + item.get(i).getConlumn() + "\"");
				} else {
					result.append("        <result column=\"" + col.getClassName() + i + "\"");
				}

				if (anyJDBC) {
					result.append(" jdbcType=\"" + item.get(i).getJdbcType() + "\"");
				}
				result.append(" property=\"" + item.get(i).getPropertyName() + "\" />\r\n");
			}
			if (col.getGrandItem() != null) {
				// 创建resultMap里面的association/collection
				result.append(addGrandMap(entitySpace, col.getGrandItem(), anyJDBC));
			}
			result.append("    </resultMap>\r\n\r\n");
		}
		return result.toString();

	}

	/**
	 * 添加子类ResultMap
	 * 
	 * @param item
	 * @param anyAs
	 * @return
	 */
	private StringBuffer addSonOfPagingMap(String entitySpace, List<ColumnItem> item, boolean anyJDBC,
			String basePrimaryKey) {
		StringBuffer result = new StringBuffer();
		if (item == null || basePrimaryKey == null) {
			return result;
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == false) {
					continue;
				}
				result.append("        <association property=\"" + col.getInPropertyName() + "\" javaType=\""
						+ entitySpace + "." + col.getClassName() + "\">\r\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getConlumn() == null || list.get(i).getConlumn() == ""
							|| list.get(i).getCheck() == false) {
						continue;
					}
					if (i == 0 && list.get(i).getConlumn().equals(col.getPrimaryKey())) {
						if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
							result.append("            <id column=\"" + col.getTableAlias() + "_"
									+ list.get(i).getConlumn() + "\"");
						} else {
							result.append("            <id column=\"" + col.getClassName() + i + "\"");
						}
						if (anyJDBC) {
							result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
						}
						result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
						continue;
					}
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append("            <result column=\"" + col.getTableAlias() + "_"
								+ list.get(i).getConlumn() + "\"");
					} else {
						result.append("            <result column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
				}
				if (col.getGrandItem() != null) {
					result.append(addGrandOfPagingMap(entitySpace, col.getGrandItem(), anyJDBC, col.getPrimaryKey()));
				}
				result.append("        </association>\r\n");
			}
		}
		for (ColumnItem col : item) {
			if (col.getAttributeCVFs() != null) {
				if (col.isAnyAssociation() == true) {
					continue;
				}
				result.append("        <collection property=\"" + col.getInPropertyName() + "\" ofType=\"" + entitySpace
						+ "." + col.getClassName() + "\" column=\"" + basePrimaryKey + "\" select=\"select"
						+ col.getClassName() + "OfPaging\">");
				result.append("        </collection>\r\n");
			}
		}

		return result;
	}

	/**
	 * 添加孙类ResultMap
	 * 
	 * @param item
	 * @param anyAs
	 * @return
	 */
	private StringBuffer addGrandOfPagingMap(String entitySpace, List<ColumnItem> item, boolean anyJDBC,
			String basePrimaryKey) {
		StringBuffer result = new StringBuffer();
		if (item == null) {
			return result;
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == false) {
					continue;
				}
				result.append("            <association property=\"" + col.getInPropertyName() + "\" javaType=\""
						+ entitySpace + "." + col.getClassName() + "\">\r\n");
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getConlumn() == null || list.get(i).getConlumn() == ""
							|| list.get(i).getCheck() == false) {
						continue;
					}
					if (i == 0 && list.get(i).getConlumn().equals(col.getPrimaryKey())) {
						if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
							result.append("                <id column=\"" + col.getTableAlias() + "_"
									+ list.get(i).getConlumn() + "\"");
						} else {
							result.append("                <id column=\"" + col.getClassName() + i + "\"");
						}
						if (anyJDBC) {
							result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
						}
						result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
						continue;
					}
					if (col.getTableAlias() != null && !(col.getTableAlias().isEmpty())) {
						result.append("                <result column=\"" + col.getTableAlias() + "_"
								+ list.get(i).getConlumn() + "\"");
					} else {
						result.append("                <result column=\"" + col.getClassName() + i + "\"");
					}
					if (anyJDBC) {
						result.append(" jdbcType=\"" + list.get(i).getJdbcType() + "\"");
					}
					result.append(" property=\"" + list.get(i).getPropertyName() + "\" />\r\n");
				}
				result.append("            </association>\r\n");
			}
		}
		for (ColumnItem col : item) {
			List<AttributeCVF> list = col.getAttributeCVFs();
			if (list != null) {
				if (col.isAnyAssociation() == true) {
					continue;
				}
				result.append("        <collection property=\"" + col.getInPropertyName() + "\" ofType=\"" + entitySpace
						+ "." + col.getClassName() + "\" column=\"" + basePrimaryKey + "\" select=\"select"
						+ col.getClassName() + "OfPaging\">");
				result.append("        </collection>\r\n");
			}
		}

		return result;
	}

	/**
	 * 创建通用sql
	 * 
	 * @param anyAssist
	 * @param attr
	 * @return
	 */
	private String createSql(boolean anyAssist, SuperAttribute attr) {
		StringBuffer result = new StringBuffer();

		if (anyAssist) {
			result.append("	   <!-- Assist用于辅助工具类setRequires设置的条件,${req.require}表示列名,#{req.value}表示值它是防SQL注入的 -->\r\n");
			result.append("    <sql id=\"Assist\">\r\n");
			result.append("        <where>\r\n");
			result.append("            <foreach collection=\"require\" item=\"req\" separator=\" \">\r\n");
			result.append(
					"                ${req.require} #{req.value} <if test=\"req.suffix != null\"> ${req.suffix}</if>\r\n");
			result.append("            </foreach>\r\n");
			result.append("        </where>\r\n");
			result.append("    </sql>\r\n\r\n");
			result.append(
					"	   <!-- updateAssist用于更新数据的使用它拓展自Assist,用于辅助工具类setRequires设置的条件,${req.require}表示列名,#{req.value}表示值它是防SQL注入的 -->\r\n");
			result.append("    <sql id=\"updateAssist\">\r\n");
			result.append("        <where>\r\n");
			result.append("            <foreach collection=\"assist.require\" item=\"req\" separator=\" \">\r\n");
			result.append(
					"                ${req.require} #{req.value} <if test=\"req.suffix != null\"> ${req.suffix}</if>\r\n");
			result.append("            </foreach>\r\n");
			result.append("        </where>\r\n");
			result.append("    </sql>\r\n\r\n");
		}

		result.append("    <!-- 数据库中表名为:" + attr.getTableName()
				+ "的列名,as前是数据库的列明,as后是列的别名用于映射成实体类中的属性,需要注意的是别名必须与resultMap中的column别名一致 -->\r\n");
		result.append("    <sql id=\"" + attr.getTableName() + "_Column\">\r\n");
		List<AttributeCVF> item = attr.getAttributes();
		for (int i = 0; i < item.size(); i++) {
			if (item.get(i).getConlumn() == null || item.get(i).getConlumn() == "" || item.get(i).getCheck() == false) {
				continue;
			}
			if (attr.getColumnItems() != null) {
				if (i == 0) {
					result.append("        " + attr.getTableName() + "." + item.get(i).getConlumn() + " as ");
					if (attr.getTableAlias() != null && !(attr.getTableAlias().isEmpty())) {
						result.append(attr.getTableAlias() + "_" + item.get(i).getConlumn());
					} else {
						result.append(attr.getClassName() + i);
					}
					continue;
				}
				result.append("\r\n        ," + attr.getTableName() + "." + item.get(i).getConlumn() + " as ");
				if (attr.getTableAlias() != null && !(attr.getTableAlias().isEmpty())) {
					result.append(attr.getTableAlias() + "_" + item.get(i).getConlumn());
				} else {
					result.append(attr.getClassName() + i);
				}
			} else {
				if (i == 0) {
					result.append("        " + attr.getTableName() + "." + item.get(i).getConlumn() + " as "
							+ item.get(i).getConlumn());
					continue;
				}
				result.append("\r\n        ," + attr.getTableName() + "." + item.get(i).getConlumn() + " as "
						+ item.get(i).getConlumn());
			}
		}
		result.append("\r\n");
		result.append("    </sql>\r\n\r\n");
		// 创建其对应的属性或者集合
		if (attr.getColumnItems() != null) {
			String original = attr.getClassName();
			result.append(addSonSql(attr.getColumnItems(), original));
		}
		return result.toString();
	}

	/**
	 * 添加子代的通用SQL
	 * 
	 * @param item
	 * @return
	 */
	private StringBuffer addSonSql(List<ColumnItem> items, String original) {
		StringBuffer result = new StringBuffer();
		if (items == null) {
			return result;
		}
		for (ColumnItem item : items) {
			List<AttributeCVF> cvf = item.getAttributeCVFs();
			if (cvf != null) {
				result.append("    <!-- 数据库中表名为:" + item.getTableName()
						+ "的列名,as前是数据库的列明,as后是列的别名用于映射成实体类中的属性,需要注意的是别名必须与resultMap中的column别名一致 -->\r\n");
				result.append("    <sql id=\"" + item.getTableName() + "_Column\">\r\n");
				for (int i = 0; i < cvf.size(); i++) {
					if (cvf.get(i).getConlumn() == null || cvf.get(i).getConlumn() == "") {
						continue;
					}
					if (i == 0) {
						result.append("        " + item.getTableName() + "." + cvf.get(i).getConlumn() + " as ");
						if (item.getTableAlias() != null && !(item.getTableAlias().isEmpty())) {
							result.append(item.getTableAlias() + "_" + cvf.get(i).getConlumn());
						} else {
							result.append(item.getClassName() + i);
						}
						continue;
					}
					result.append("\r\n        ," + item.getTableName() + "." + cvf.get(i).getConlumn() + " as ");
					if (item.getTableAlias() != null && !(item.getTableAlias().isEmpty())) {
						result.append(item.getTableAlias() + "_" + cvf.get(i).getConlumn());
					} else {
						result.append(item.getClassName() + i);
					}
				}
				result.append("\r\n");
				result.append("    </sql>\r\n\r\n");
				if (item.getGrandItem() != null) {
					result.append(addGrandSql(item.getGrandItem(), original));
				}
			}
		}
		return result;
	}

	/**
	 * 添加孙代的通用SQL
	 * 
	 * @param item
	 * @return
	 */
	private StringBuffer addGrandSql(List<ColumnItem> items, String original) {
		StringBuffer result = new StringBuffer();
		if (items == null) {
			return result;
		}
		for (ColumnItem item : items) {
			if (item.getClassName().equals(original)) {
				continue;
			}
			List<AttributeCVF> cvf = item.getAttributeCVFs();
			if (cvf != null) {
				result.append("    <!-- 数据库中表名为:" + item.getTableName()
						+ "的列名,as前是数据库的列明,as后是列的别名用于映射成实体类中的属性,需要注意的是别名必须与resultMap中的column别名一致 -->\r\n");
				result.append("    <sql id=\"" + item.getTableName() + "_Column\">\r\n");
				for (int i = 0; i < cvf.size(); i++) {
					if (cvf.get(i).getConlumn() == null || cvf.get(i).getConlumn() == "") {
						continue;
					}
					if (i == 0) {
						result.append("        " + item.getTableName() + "." + cvf.get(i).getConlumn() + " as ");
						if (item.getTableAlias() != null && !(item.getTableAlias().isEmpty())) {
							result.append(item.getTableAlias() + "_" + cvf.get(i).getConlumn());
						} else {
							result.append(item.getClassName() + i);
						}
						continue;
					}
					result.append("\r\n        ," + item.getTableName() + "." + cvf.get(i).getConlumn() + " as ");
					if (item.getTableAlias() != null && !(item.getTableAlias().isEmpty())) {
						result.append(item.getTableAlias() + "_" + cvf.get(i).getConlumn());
					} else {
						result.append(item.getClassName() + i);
					}
				}
				result.append("\r\n");
				result.append("    </sql>\r\n\r\n");
			}
		}
		return result;
	}

	/**
	 * 获得getRowCount语句
	 * 
	 * @param assistSpace
	 * @param attr
	 * @param anyAssist
	 * @return
	 */
	private String createRowCount(String assistSpace, SuperAttribute attr, boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!--获得类名为:" + attr.getClassName() + "对应的数据库表的数据总行数 -->\r\n");
		result.append("    <select id=\"get" + attr.getClassName() + "RowCount\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\"");
		}
		result.append(" resultType=\"java.lang.Long\">\r\n");
		if (attr.getPrimaryKey() != null) {
			result.append("        select count(" + attr.getPrimaryKey() + ") from " + attr.getTableName() + "\r\n");
		} else {
			result.append("        select count(*) from " + attr.getTableName() + "\r\n");
		}
		if (anyAssist) {
			result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
		}
		result.append("    </select>\r\n");
		return result.toString();
	}

	/**
	 * 通过数据库类型创建不同的查询语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param dbType
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createSelectSql(SuperAttribute attr, String assistSpace, String dbType, boolean anyJDBC,
			boolean anyAssist) {
		if (dbType.equals("Oracle")) {
			return createOraclePage(attr, assistSpace, anyJDBC, anyAssist);
		} else if (dbType.equals("SqlServer")) {
			return createSqlServerPage(attr, assistSpace, anyJDBC, anyAssist);
		} else if (dbType.equals("PostgreSQL")) {
			return createPostgrePage(attr, assistSpace, anyJDBC, anyAssist);
		} else {
			return createMySql(attr, assistSpace, anyJDBC, anyAssist);
		}
	}

	/**
	 * 创建mysql
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createMySql(SuperAttribute attr, String assistSpace, boolean anyJDBC, boolean anyAssist) {
		StringBuffer result = new StringBuffer();

		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\" ");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "_Map\">\r\n");
		result.append("        select ");
		if (anyAssist) {
			result.append(" <if test=\"distinct !=null\">${distinct}</if>");
		}
		if (anyAssist) {
			result.append("\r\n        <choose>\r\n");
			result.append("            <when test=\"resultColumn!=null\">${resultColumn}</when>\r\n");
			result.append("            <otherwise>");
		}
		result.append("\r\n        <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		if (attr.getColumnItems() != null) {
			for (ColumnItem item : attr.getColumnItems()) {
				result.append("        ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
				if (item.getGrandItem() != null) {
					for (ColumnItem itemg : item.getGrandItem()) {
						if (attr.getTableName().equals(itemg.getTableName())) {
							continue;
						}
						result.append("            ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
					}
				}
			}
		}
		if (anyAssist) {
			result.append("            </otherwise>\r\n");
			result.append("        </choose>\r\n");
		}
		result.append("        from " + attr.getTableName() + "\r\n");
		if (attr.getColumnItems() != null) {
			result.append(getJoinStr(attr.getColumnItems(), attr.getTableName(), 8));
		}
		if (anyAssist) {
			result.append("        <choose>\r\n");
			result.append("            <when test=\"rowSize ==null\">\r\n");
			result.append("                <if test=\"require!=null\">\r\n");
			result.append("                    <include refid=\"Assist\" />\r\n");
			result.append("                </if>\r\n");
			result.append("                <if test=\"order !=null\">${order}</if>\r\n");
			result.append("            </when>\r\n");
			result.append("            <otherwise>\r\n");
			result.append("                inner join\r\n");
			result.append("                (\r\n");
			result.append("                SELECT " + attr.getPrimaryKey() + " as primary_key FROM "
					+ attr.getTableName() + "\r\n");
			result.append("                <if test=\"require!=null\">\r\n");
			result.append("                    <include refid=\"Assist\" />\r\n");
			result.append("                </if>\r\n");
			result.append("                <if test=\"order !=null\">${order}</if>\r\n");
			result.append("                <choose>\r\n");
			result.append("                    <when test=\"startRow !=null and rowSize !=null\">LIMIT #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("},#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} </when>\r\n");
			result.append("                    <when test=\"rowSize !=null\">LIMIT #{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</when>\r\n");
			result.append("                 </choose>\r\n");
			result.append("                 ) as tmp\r\n");
			result.append(
					"                 on tmp.primary_key=" + attr.getTableName() + "." + attr.getPrimaryKey() + "\r\n");
			result.append("            </otherwise>\r\n");
			result.append("        </choose>\r\n");
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 创建mysql与postgre
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createPostgrePage(SuperAttribute attr, String assistSpace, boolean anyJDBC, boolean anyAssist) {
		StringBuffer result = new StringBuffer();

		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\" ");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "_Map\">\r\n");
		result.append("        select ");
		if (anyAssist) {
			result.append(" <if test=\"distinct !=null\">${distinct}</if>");
		}
		if (anyAssist) {
			result.append("\r\n        <choose>\r\n");
			result.append("            <when test=\"resultColumn!=null\">${resultColumn}</when>\r\n");
			result.append("            <otherwise>");
		}
		result.append("\r\n        <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		if (attr.getColumnItems() != null) {
			for (ColumnItem item : attr.getColumnItems()) {
				result.append("        ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
				if (item.getGrandItem() != null) {
					for (ColumnItem itemg : item.getGrandItem()) {
						if (attr.getTableName().equals(itemg.getTableName())) {
							continue;
						}
						result.append("            ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
					}
				}
			}
		}
		if (anyAssist) {
			result.append("            </otherwise>\r\n");
			result.append("        </choose>\r\n");
		}
		result.append("        from " + attr.getTableName() + "\r\n");
		if (attr.getColumnItems() != null) {
			result.append(getJoinStr(attr.getColumnItems(), attr.getTableName(), 8));
		}
		if (anyAssist) {
			result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
			result.append("        <if test=\"order !=null\">${order}</if>\r\n");
			result.append("        <if test=\"rowSize !=null\"> LIMIT #{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} <if test=\"startRow !=null\"> OFFSET #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</if></if>\r\n");
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 创建sqlserver语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @return
	 */
	private String createSqlServerPage(SuperAttribute attr, String assistSpace, boolean anyJDBC, boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\"");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "_Map\">\r\n");
		result.append("        select * from\r\n");
		result.append("        (\r\n");
		result.append("            select ");
		if (anyAssist) {
			result.append("<if test=\"distinct !=null\">${distinct}</if>");
			result.append(" ROW_NUMBER() over(<choose><when test=\"order!=null\">${order}</when><otherwise>order by "
					+ attr.getTableName() + "." + attr.getPrimaryKey() + "</otherwise></choose>) as page,\r\n");
		}
		if (anyAssist) {
			result.append("            <choose>\r\n");
			result.append("                <when test=\"resultColumn!=null\">${resultColumn}</when>\r\n");
			result.append("                <otherwise>");
		}
		result.append("\r\n            <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		if (attr.getColumnItems() != null) {
			for (ColumnItem item : attr.getColumnItems()) {
				result.append("            ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
				if (item.getGrandItem() != null) {
					for (ColumnItem itemg : item.getGrandItem()) {
						if (attr.getTableName().equals(itemg.getTableName())) {
							continue;
						}
						result.append("            ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
					}
				}
			}
		}
		if (anyAssist) {
			result.append("                </otherwise>\r\n");
			result.append("            </choose>\r\n");
		}
		result.append("            from " + attr.getTableName() + "\r\n");
		if (attr.getColumnItems() != null) {
			result.append(getJoinStr(attr.getColumnItems(), attr.getTableName(), 8));
		}
		if (anyAssist) {
			result.append("\r\n            <if test=\"require!=null\"><include refid=\"Assist\" /></if>");
			//result.append("\r\n            <if test=\"order !=null\">${order}</if>");
		}
		result.append("\r\n        ) result \r\n");
		if (anyAssist) {
			result.append("        <choose>\r\n");
			result.append("            <when test=\"startRow!=null\">where page &gt; #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append(
					"} <if test=\"rowSize!=null\">and page &lt;= <if test=\"startRow!=null\">#{startRow}+</if>#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} </if></when>\r\n");
			result.append("            <otherwise><if test=\"rowSize!=null\">where page &lt;= #{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</if></otherwise>\r\n");
			result.append("        </choose>\r\n");
			
			result.append("        <if test=\"order !=null\">${order}</if>\r\n");
			
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得oracle查询语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createOraclePage(SuperAttribute attr, String assistSpace, boolean anyJDBC, boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\"");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "_Map\">\r\n");
		result.append("        select * from\r\n");
		result.append("        (\r\n");
		result.append("            select ");
		if (anyAssist) {
			result.append(" rownum as page,");
		}
		result.append("result.* from\r\n            (\r\n");
		result.append("                select ");
		if (anyAssist) {
			result.append(" <if test=\"distinct !=null\">${distinct}</if>\r\n");
		}
		if (anyAssist) {
			result.append("                <choose>\r\n");
			result.append("            	       <when test=\"resultColumn!=null\">${resultColumn}</when>\r\n");
			result.append("            	       <otherwise>");
		}
		result.append("\r\n                <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		if (attr.getColumnItems() != null) {
			for (ColumnItem item : attr.getColumnItems()) {
				result.append("                ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
				if (item.getGrandItem() != null) {
					for (ColumnItem itemg : item.getGrandItem()) {
						if (attr.getTableName().equals(itemg.getTableName())) {
							continue;
						}
						result.append(
								"                ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
					}
				}
			}
		}
		if (anyAssist) {
			result.append("            	       </otherwise>\r\n");
			result.append("                </choose>\r\n");
		}
		result.append("                from " + attr.getTableName() + " \r\n");
		if (attr.getColumnItems() != null) {
			result.append(getJoinStr(attr.getColumnItems(), attr.getTableName(), 16));
		}
		if (anyAssist) {
			result.append("\r\n                <if test=\"require!=null\"><include refid=\"Assist\" /></if>");
			result.append("\r\n                <if test=\"order !=null\">${order}</if>");
		}
		result.append("\r\n            ) result \r\n");
		if (anyAssist) {
			result.append(
					"            <if test=\"rowSize!=null\">where rownum &lt;= <if test=\"startRow!=null\">#{startRow}+</if>#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</if>\r\n");
		}

		result.append("        )\r\n");
		if (anyAssist) {
			result.append("        <if test=\"startRow!=null\">where page &gt; #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} </if>\r\n");
		}

		result.append("    </select> \r\n\r\n");

		return result.toString();
	}

	/**
	 * 通过数据库类型创建带分页不同的查询语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param dbType
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createSelectOfPagingSql(SuperAttribute attr, String assistSpace, String dbType, boolean anyJDBC,
			boolean anyAssist) {
		if (dbType.equals("Oracle")) {
			return createOraclePageOfPaging(attr, assistSpace, anyJDBC, anyAssist);
		} else if (dbType.equals("SqlServer")) {
			return createSqlServerPageOfPaging(attr, assistSpace, anyJDBC, anyAssist);
		} else if (dbType.equals("PostgreSQL")) {
			return createPostgrePageOfPaging(attr, assistSpace, anyJDBC, anyAssist);
		} else {
			return createMySqlOfPaging(attr, assistSpace, anyJDBC, anyAssist);
		}
	}

	/**
	 * 创建mysql
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createMySqlOfPaging(SuperAttribute attr, String assistSpace, boolean anyJDBC, boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合,该查询语句用于多表关联分页查询使用 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "OfPaging\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\" ");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "OfPaging_Map\">\r\n");
		result.append("        select ");
		if (anyAssist) {
			result.append(" <if test=\"distinct !=null\">${distinct}</if>");
		}
		result.append("\r\n        <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		result.append("        from " + attr.getTableName() + "\r\n");
		if (anyAssist) {
			result.append("        <choose>\r\n");
			result.append("            <when test=\"rowSize ==null\">\r\n");
			result.append("                <if test=\"require!=null\">\r\n");
			result.append("                    <include refid=\"Assist\" />\r\n");
			result.append("                </if>\r\n");
			result.append("                <if test=\"order !=null\">${order}</if>\r\n");
			result.append("            </when>\r\n");
			result.append("            <otherwise>\r\n");
			result.append("                inner join\r\n");
			result.append("                (\r\n");
			result.append("                SELECT " + attr.getPrimaryKey() + " as primary_key FROM "
					+ attr.getTableName() + "\r\n");
			result.append("                <if test=\"require!=null\">\r\n");
			result.append("                    <include refid=\"Assist\" />\r\n");
			result.append("                </if>\r\n");
			result.append("                <if test=\"order !=null\">${order}</if>\r\n");
			result.append("                <choose>\r\n");
			result.append("                    <when test=\"startRow !=null and rowSize !=null\">LIMIT #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("},#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} </when>\r\n");
			result.append("                    <when test=\"rowSize !=null\">LIMIT #{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</when>\r\n");
			result.append("                 </choose>\r\n");
			result.append("                 ) as tmp\r\n");
			result.append(
					"                 on tmp.primary_key=" + attr.getTableName() + "." + attr.getPrimaryKey() + "\r\n");
			result.append("            </otherwise>\r\n");
			result.append("        </choose>\r\n");
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 创建需要分页的MySqlAndPostgresql语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createPostgrePageOfPaging(SuperAttribute attr, String assistSpace, boolean anyJDBC,
			boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合,该查询语句用于多表关联分页查询使用 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "OfPaging\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\" ");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "OfPaging_Map\">\r\n");
		result.append("        select ");
		if (anyAssist) {
			result.append(" <if test=\"distinct !=null\">${distinct}</if>");
		}
		result.append("\r\n        <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		result.append("        from " + attr.getTableName() + "\r\n");
		if (anyAssist) {
			result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
			result.append("        <if test=\"order !=null\">${order}</if>\r\n");
			result.append("        <if test=\"rowSize !=null\"> LIMIT #{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} <if test=\"startRow !=null\"> OFFSET #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</if></if>\r\n");
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 创建需要分页的SqlServersql语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createSqlServerPageOfPaging(SuperAttribute attr, String assistSpace, boolean anyJDBC,
			boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合,该查询语句用于多表关联分页查询使用 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "OfPaging\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\"");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "OfPaging_Map\">\r\n");
		result.append("        select * from\r\n");
		result.append("        (\r\n");
		result.append("            select ");
		if (anyAssist) {
			result.append("<if test=\"distinct !=null\">${distinct}</if>");
			result.append(" ROW_NUMBER() over(<choose><when test=\"order!=null\">${order}</when><otherwise>order by "
					+ attr.getTableName() + "." + attr.getPrimaryKey() + "</otherwise></choose>) as page,\r\n");
		}
		result.append("            <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		result.append("            from " + attr.getTableName());

		if (anyAssist) {
			result.append("\r\n            <if test=\"require!=null\"><include refid=\"Assist\" /></if>");
			result.append("\r\n            <if test=\"order !=null\">${order}</if>");
		}
		result.append("\r\n        ) result \r\n");
		if (anyAssist) {
			result.append("        <choose>\r\n");
			result.append("            <when test=\"startRow!=null\">where page &gt; #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append(
					"} <if test=\"rowSize!=null\">and page &lt;= <if test=\"startRow!=null\">#{startRow}+</if>#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} </if></when>\r\n");
			result.append(
					"            <otherwise><if test=\"rowSize!=null\">where page &lt;= <if test=\"startRow!=null\">#{startRow}+</if>#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</if></otherwise>\r\n");
			result.append("        </choose>\r\n");
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 创建需要分页的Oraclesql语句
	 * 
	 * @param attr
	 * @param assistSpace
	 * @param anyJDBC
	 * @param anyAssist
	 * @return
	 */
	private String createOraclePageOfPaging(SuperAttribute attr, String assistSpace, boolean anyJDBC,
			boolean anyAssist) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合,该查询语句用于多表关联分页查询使用 -->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "OfPaging\"");
		if (anyAssist) {
			result.append(" parameterType=\"" + assistSpace + ".Assist\"");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "OfPaging_Map\">\r\n");
		result.append("        select * from\r\n");
		result.append("        (\r\n");
		result.append("            select ");
		if (anyAssist) {
			result.append(" rownum as page,");
		}
		result.append("result.* from\r\n            (\r\n");
		result.append("                select ");
		if (anyAssist) {
			result.append(" <if test=\"distinct !=null\">${distinct}</if>");
		}
		result.append("\r\n                <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		result.append("                from " + attr.getTableName());
		if (anyAssist) {
			result.append("\r\n                <if test=\"require!=null\"><include refid=\"Assist\" /></if>");
			result.append("\r\n                <if test=\"order !=null\">${order}</if>");
		}
		result.append("\r\n            ) result \r\n");
		if (anyAssist) {
			result.append(
					"            <if test=\"rowSize!=null\">where rownum &lt;= <if test=\"startRow!=null\">#{startRow}+</if>#{rowSize");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("}</if>\r\n");
		}

		result.append("        )\r\n");
		if (anyAssist) {
			result.append("        <if test=\"startRow!=null\">where page &gt; #{startRow");
			if (anyJDBC) {
				result.append(",jdbcType=INTEGER");
			}
			result.append("} </if>\r\n");
		}

		result.append("    </select> \r\n\r\n");

		return result.toString();
	}

	/**
	 * 创建分页所需的查询
	 * 
	 * @param attrBase
	 * @param anyJDBC
	 * @return
	 */
	private String createCollectionNeedSelectSql(SuperAttribute attrBase, boolean anyJDBC) {
		if (attrBase.getColumnItems() == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (ColumnItem attr : attrBase.getColumnItems()) {
			if (attr.isAnyAssociation()) {
				continue;
			}
			result.append("    <!-- 获得类名为:" + attr.getClassName() + "对应数据库中表的数据集合,该查询语句用于多表关联分页查询所需要的子查询-->\r\n");
			result.append("    <select id=\"select" + attr.getClassName() + "OfPaging\"");
			result.append(" resultMap=\"result_" + attr.getClassName() + "OfPaging_Map\">\r\n");
			result.append("        select ");
			result.append("\r\n            <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
			if (attr.getGrandItem() != null) {
				for (ColumnItem item : attr.getGrandItem()) {
					result.append("            ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
					if (item.getGrandItem() != null) {
						for (ColumnItem itemg : item.getGrandItem()) {
							if (attr.getTableName().equals(itemg.getTableName())) {
								continue;
							}
							result.append(
									"            ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
						}
					}
				}
			}
			result.append("        from " + attr.getTableName() + "\r\n");
			if (attr.getGrandItem() != null) {
				result.append(getJoinStr(attr.getGrandItem(), attr.getTableName(), 8));
			}
			result.append("        where " + attrBase.getPrimaryKey() + "=#{" + attrBase.getPrimaryKey());
			if (anyJDBC) {
				if (attrBase.getAttributes() != null) {
					String tmpType = selectJDBCType(attrBase.getAttributes(), attrBase.getPrimaryKey());
					result.append(",jdbcType=" + tmpType);
				}
			}
			result.append("}\r\n");
			result.append("    </select> \r\n\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得连接方式字符串
	 * 
	 * @param attrs
	 *            属性项
	 * @param spaceCount
	 *            空格个数
	 * @return
	 */
	private String getJoinStr(List<ColumnItem> attrs, String original, int spaceCount) {
		if (attrs == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (ColumnItem item : attrs) {
			if (item.getJoinTableName() == null || "".equals(item.getJoinTableName()) || item.getJoinColumn() == null
					|| "".equals(item.getJoinColumn())) {
				continue;
			}
			for (int i = 0; i < spaceCount; i++) {
				result.append(" ");
			}
			result.append(item.getJoinType() + " join " + item.getTableName() + " on " + item.getTableName() + "."
					+ item.getJoinColumn() + "=" + item.getJoinTableName() + "." + item.getJoinColumn() + "\r\n");
			if (item.getGrandItem() != null) {
				for (ColumnItem itemg : item.getGrandItem()) {
					if (itemg.getJoinTableName() == null || "".equals(itemg.getJoinTableName())
							|| itemg.getJoinColumn() == null || "".equals(itemg.getJoinColumn())
							|| original.equals(itemg.getTableName())) {
						continue;
					}
					for (int i = 0; i < spaceCount; i++) {
						result.append(" ");
					}
					result.append(itemg.getJoinType() + " join " + itemg.getTableName() + " on " + itemg.getTableName()
							+ "." + itemg.getJoinColumn() + "=" + itemg.getJoinTableName() + "." + itemg.getJoinColumn()
							+ "\r\n");
				}
			}
		}
		return result.toString();
	}

	/**
	 * 获得查询语句ById
	 * 
	 * @param attr
	 * @param anyJDBC
	 * @return
	 */
	private String createSelectByObj(SuperAttribute attr, String entitySpace, boolean anyJDBC) {
		if (attr.getPrimaryKey() == null || attr.getPrimaryKey() == "") {
			return "";
		}
		StringBuffer result = new StringBuffer();

		result.append(
				"    <!-- 获得一个" + attr.getClassName() + "对象,以参数" + attr.getClassName() + "对象中不为空的属性作为条件进行查询-->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "ByObj\"");
		result.append(" parameterType=\"" + entitySpace + "." + attr.getClassName() + "\"");
		result.append(" resultMap=\"result_" + attr.getClassName() + "_Map\">\r\n");
		result.append("        select ");
		result.append("\r\n            <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		if (attr.getColumnItems() != null) {
			for (ColumnItem item : attr.getColumnItems()) {
				result.append("            ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
				if (item.getGrandItem() != null) {
					for (ColumnItem itemg : item.getGrandItem()) {
						if (attr.getTableName().equals(itemg.getTableName())) {
							continue;
						}
						result.append("            ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
					}
				}
			}
		}
		result.append("        from " + attr.getTableName() + "\r\n");
		if (attr.getColumnItems() != null) {
			result.append(getJoinStr(attr.getColumnItems(), attr.getTableName(), 8));
		}
		List<AttributeCVF> cvf = attr.getAttributes();
		if (cvf != null) {
			result.append("        <where>\r\n");
			for (int i = cvf.size() - 1; i >= 0; i--) {
				if (cvf.get(i).getConlumn() != null) {
					result.append("            <if test=\"" + cvf.get(i).getPropertyName() + " != null \"> " + "and "
							+ attr.getTableName() + "." + cvf.get(i).getConlumn() + " = #{"
							+ cvf.get(i).getPropertyName());
					if (anyJDBC) {
						result.append(",jdbcType=" + cvf.get(i).getJdbcType());
					}
					result.append("}</if>\r\n");
				}
			}
			result.append("        </where>\r\n");
		}
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得查询语句ById
	 * 
	 * @param attr
	 * @param anyJDBC
	 * @return
	 */
	private String createSelectById(SuperAttribute attr, boolean anyJDBC) {
		if (attr.getPrimaryKey() == null || attr.getPrimaryKey() == "") {
			return "";
		}
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 通过" + attr.getClassName() + "的id获得对应数据库中表的数据对象-->\r\n");
		result.append("    <select id=\"select" + attr.getClassName() + "ById\"");
		if (attr.getAttributes() != null) {
			String tmpType = selectIdType(attr.getAttributes(), attr.getPrimaryKey());
			result.append(" parameterType=\"java.lang." + tmpType + "\"");
		}
		result.append(" resultMap=\"result_" + attr.getClassName() + "_Map\">\r\n");
		result.append("        select ");
		result.append("\r\n            <include refid=\"" + attr.getTableName() + "_Column\" /> \r\n");
		if (attr.getColumnItems() != null) {
			for (ColumnItem item : attr.getColumnItems()) {
				result.append("            ,<include refid=\"" + item.getTableName() + "_Column\" /> \r\n");
				if (item.getGrandItem() != null) {
					for (ColumnItem itemg : item.getGrandItem()) {
						if (attr.getTableName().equals(itemg.getTableName())) {
							continue;
						}
						result.append("            ,<include refid=\"" + itemg.getTableName() + "_Column\" /> \r\n");
					}
				}
			}
		}
		result.append("        from " + attr.getTableName() + "\r\n");
		if (attr.getColumnItems() != null) {
			result.append(getJoinStr(attr.getColumnItems(), attr.getTableName(), 8));
		}
		result.append("        where " + attr.getTableName() + "." + attr.getPrimaryKey() + " = #{id");
		if (anyJDBC) {
			if (attr.getAttributes() != null) {
				String tmpType = selectJDBCType(attr.getAttributes(), attr.getPrimaryKey());
				result.append(",jdbcType=" + tmpType);
			}
		}
		result.append("}\r\n");
		result.append("    </select> \r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得插入全部语句
	 * 
	 * @param attr
	 * @param entitySpace
	 * @return
	 */
	private String createInsertAll(SuperAttribute attr, String entitySpace, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 将" + attr.getClassName() + "插入到对应数据库的表中,包括属性值为null的数据-->\r\n");
		result.append("\r\n    <insert id=\"insert" + attr.getClassName() + "\" parameterType=\"" + entitySpace + "."
				+ attr.getClassName() + "\">\r\n");
		if (attr.getSelectKey() != null) {
			result.append(attr.getSelectKey() + "\r\n");
		}
		result.append("        insert into " + attr.getTableName() + "(");
		List<AttributeCVF> cvf = attr.getAttributes();
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				if (i == 0) {
					result.append(cvf.get(i).getConlumn());
				} else {
					result.append("," + cvf.get(i).getConlumn());
				}
			}
		}
		result.append(") \r\n");
		result.append("        values(");
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				if (i == 0) {
					result.append("#{" + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("}");
				} else {
					result.append(",#{" + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("}");
				}
			}
		}
		result.append(")\r\n");
		result.append("    </insert>\r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得插入不为空的语句
	 * 
	 * @param attr
	 * @param entitySpace
	 * @param anyJDBC
	 * @return
	 */
	private String createInsertNonEmpty(SuperAttribute attr, String entitySpace, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 将" + attr.getClassName() + "中属性值不为null的数据,插入到对应数据库的表中-->\r\n");
		result.append("    <insert id=\"insertNonEmpty" + attr.getClassName() + "\" parameterType=\"" + entitySpace
				+ "." + attr.getClassName() + "\">\r\n");
		if (attr.getSelectKey() != null) {
			result.append(attr.getSelectKey() + "\r\n");
		}
		result.append("        insert into " + attr.getTableName() + "\r\n");
		result.append("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\r\n");
		List<AttributeCVF> cvf = attr.getAttributes();
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				result.append("            <if test=\"" + cvf.get(i).getPropertyName() + " != null\">"
						+ cvf.get(i).getConlumn() + ",</if>\r\n");
			}
		}
		result.append("        </trim>\r\n");
		result.append("        <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\r\n");
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				result.append("            <if test=\"" + cvf.get(i).getPropertyName() + " != null\"> #{"
						+ cvf.get(i).getPropertyName());
				if (anyJDBC) {
					if (cvf.get(i).getJdbcType() != null) {
						result.append(",jdbcType=" + cvf.get(i).getJdbcType());
					}
				}
				result.append("},</if>\r\n");
			}
		}
		result.append("        </trim>\r\n");
		result.append("    </insert>\r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得插入全部语句 parameterType="ArrayList"
	 * 
	 * @param attr
	 * @param entitySpace
	 * @return
	 */
	private String createInsertBatch(SuperAttribute attr, String entitySpace, boolean anyJDBC, String dbType) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 将" + attr.getClassName() + "批量插入到对应数据库的表中-->\r\n");
		result.append("    <insert id=\"insert" + attr.getClassName() + "ByBatch\" parameterType=\"ArrayList\">\r\n");
		if (attr.getSelectKey() != null) {
			result.append(attr.getSelectKey() + "\r\n");
		}
		result.append("        insert into " + attr.getTableName() + "(");
		List<AttributeCVF> cvf = attr.getAttributes();
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				if (i == 0) {
					result.append(cvf.get(i).getConlumn());
				} else {
					result.append("," + cvf.get(i).getConlumn());
				}
			}
		}
		result.append(") ");
		result.append(getInsertBatchValues(cvf, anyJDBC, dbType));
		result.append("    </insert>\r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得批量插入的值
	 * 
	 * @param cvf
	 * @param anyJDBC
	 * @param dbType
	 * @return
	 */
	private String getInsertBatchValues(List<AttributeCVF> cvf, boolean anyJDBC, String dbType) {
		if (dbType.equals("Oracle")) {
			return getOracleBatchValue(cvf, anyJDBC, dbType);
		} else if (dbType.equals("SqlServer")) {
			return getSqlServerBatchValue(cvf, anyJDBC, dbType);
		} else {
			return getPostgreAndMySqlBatchValue(cvf, anyJDBC, dbType);
		}

	}

	/**
	 * 获得oracle批量插入的值
	 * 
	 * @param cvf
	 * @param anyJDBC
	 * @param dbType
	 * @return
	 */
	private String getOracleBatchValue(List<AttributeCVF> cvf, boolean anyJDBC, String dbType) {
		StringBuffer result = new StringBuffer();
		result.append(
				"\r\n        <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\" union all \" >");
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				if (i == 0) {
					result.append("\r\n            select #{item." + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("}");
				} else {
					result.append(",#{item." + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("} from dual ");
				}
			}
		}
		result.append("\r\n        </foreach>\r\n");
		return result.toString();
	};

	/**
	 * 获得sqlserver批量插入的值
	 * 
	 * @param cvf
	 * @param anyJDBC
	 * @param dbType
	 * @return
	 */
	private String getSqlServerBatchValue(List<AttributeCVF> cvf, boolean anyJDBC, String dbType) {
		StringBuffer result = new StringBuffer();
		result.append(
				"\r\n        <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\" union all \" >");
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				if (i == 0) {
					result.append("\r\n            select #{item." + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("}");
				} else {
					result.append(",#{item." + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("}");
				}
			}
		}
		result.append("\r\n        </foreach>\r\n");
		return result.toString();
	};

	/**
	 * 获得mysql与postgre的批量插入值
	 * 
	 * @param cvf
	 * @param anyJDBC
	 * @param dbType
	 * @return
	 */
	private String getPostgreAndMySqlBatchValue(List<AttributeCVF> cvf, boolean anyJDBC, String dbType) {
		StringBuffer result = new StringBuffer();
		result.append("values\r\n");
		result.append("        <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\" >");
		for (int i = 0; i < cvf.size(); i++) {
			if (cvf.get(i).getConlumn() != null) {
				if (i == 0) {
					result.append("\r\n            (#{item." + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("}");
				} else {
					result.append(",#{item." + cvf.get(i).getPropertyName());
					if (anyJDBC) {
						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("})");
				}
			}
		}
		result.append("\r\n        </foreach>\r\n");
		return result.toString();
	};

	/**
	 * 创建删除ById
	 * 
	 * @param attr
	 * @param entitySpace
	 * @param anyJDBC
	 * @return
	 */
	private String createDeleteById(SuperAttribute attr, String entitySpace, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		if (attr.getPrimaryKey() != null) {
			String javaIdType = selectIdType(attr.getAttributes(), attr.getPrimaryKey());
			if (javaIdType != null) {
				result.append("    <!-- 通过" + attr.getClassName() + "的id将数据库表中对应的数据删除-->\r\n");
				result.append("    <delete id=\"delete" + attr.getClassName() + "ById\" parameterType=\"java.lang."
						+ javaIdType + "\">\r\n");
				result.append("        delete from " + attr.getTableName() + "\r\n");
				result.append("        where " + attr.getPrimaryKey() + " = #{id");
				if (anyJDBC) {
					if (selectJDBCType(attr.getAttributes(), attr.getPrimaryKey()) != null) {
						result.append(",jdbcType=" + selectJDBCType(attr.getAttributes(), attr.getPrimaryKey()));
					}
				}
				result.append("}\r\n");
				result.append("    </delete>\r\n\r\n");
			}
		}
		return result.toString();
	}

	/**
	 * 获得deleteByAssist
	 * 
	 * @param attr
	 * @param assistSpace
	 * @return
	 */
	private String createDeleteByAssist(SuperAttribute attr, String assistSpace) {
		StringBuffer result = new StringBuffer();
		result.append("    <!-- 通过辅助工具Assist中的条件将" + attr.getClassName() + "对应的数据库表的数据删除-->\r\n");
		result.append("    <delete id=\"delete" + attr.getClassName() + "\" parameterType=\"" + assistSpace
				+ ".Assist\">\r\n");
		result.append("        delete from " + attr.getTableName() + "\r\n");
		result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
		result.append("    </delete>\r\n\r\n");
		return result.toString();
	}

	/**
	 * 获得更新语句ById
	 * 
	 * @param attr
	 * @param entitySpace
	 * @param anyJDBC
	 * @return
	 */
	private String createUpdateById(SuperAttribute attr, String entitySpace, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		String tmpIdRow = "";
		if (attr.getPrimaryKey() != null) {
			boolean falg = true;
			result.append("    <!-- 通过" + attr.getClassName() + "的id将" + attr.getClassName()
					+ "的数据更新到数据库中对应的表,包括值null的数据-->\r\n");
			result.append("    <update id=\"update" + attr.getClassName() + "ById\" parameterType=\"" + entitySpace
					+ "." + attr.getClassName() + "\">\r\n");
			result.append("        update " + attr.getTableName() + " set\r\n");
			List<AttributeCVF> cvf = attr.getAttributes();
			for (int i = 0; i < cvf.size(); i++) {
				if (cvf.get(i).getConlumn() != null) {
					if (attr.getPrimaryKey().equals(cvf.get(i).getConlumn())) {
						tmpIdRow = cvf.get(i).getConlumn() + "=" + "#{" + cvf.get(i).getPropertyName();
						if (anyJDBC) {
							if (cvf.get(i).getJdbcType() != null) {
								tmpIdRow += ",jdbcType=" + cvf.get(i).getJdbcType();
							}
						}
						tmpIdRow += "}\r\n";
					} else {
						if (falg) {
							result.append("            " + cvf.get(i).getConlumn() + "=" + "#{"
									+ cvf.get(i).getPropertyName());
							if (anyJDBC) {
								if (cvf.get(i).getJdbcType() != null) {
									result.append(",jdbcType=" + cvf.get(i).getJdbcType());
								}
							}
							result.append("}\r\n");
							falg = false;
						} else {

							result.append("            ," + cvf.get(i).getConlumn() + "=" + "#{"
									+ cvf.get(i).getPropertyName());
							if (anyJDBC) {
								if (cvf.get(i).getJdbcType() != null) {
									result.append(",jdbcType=" + cvf.get(i).getJdbcType());
								}
							}
							result.append("}\r\n");
							falg = false;
						}
					}
				}
			}
			result.append("        where " + tmpIdRow);
			result.append("    </update>\r\n\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得更新不为空语句ById
	 * 
	 * @param attr
	 * @param entitySpace
	 * @param anyJDBC
	 * @return
	 */
	private String createUpdateNonEmptyById(SuperAttribute attr, String entitySpace, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		String tmpIdRow = "";
		if (attr.getPrimaryKey() != null) {
			result.append("    <!-- 通过" + attr.getClassName() + "的id将" + attr.getClassName()
					+ "中属性值不为null的数据更新到数据库对应的表中-->\r\n");
			result.append("    <update id=\"updateNonEmpty" + attr.getClassName() + "ById\" parameterType=\""
					+ entitySpace + "." + attr.getClassName() + "\">\r\n");
			result.append("        update " + attr.getTableName() + "\r\n");
			result.append("        <set>\r\n");
			List<AttributeCVF> cvf = attr.getAttributes();
			for (int i = 0; i < cvf.size(); i++) {
				if (cvf.get(i).getConlumn() != null) {
					if (attr.getPrimaryKey().equals(cvf.get(i).getConlumn())) {
						tmpIdRow = cvf.get(i).getConlumn() + "=" + "#{" + cvf.get(i).getPropertyName();
						if (anyJDBC) {
							if (cvf.get(i).getJdbcType() != null) {
								tmpIdRow += ",jdbcType=" + cvf.get(i).getJdbcType();
							}
						}
						tmpIdRow += "}\r\n";
					} else {
						result.append("            <if test=\"" + cvf.get(i).getPropertyName() + " != null\">\r\n");
						result.append("                " + cvf.get(i).getConlumn() + "=" + "#{"
								+ cvf.get(i).getPropertyName());
						if (anyJDBC) {
							if (cvf.get(i).getJdbcType() != null) {
								result.append(",jdbcType=" + cvf.get(i).getJdbcType());
							}
						}
						result.append("},\r\n");
						result.append("            </if>\r\n");
					}
				}
			}
			result.append("        </set>\r\n");
			result.append("        where " + tmpIdRow);
			result.append("    </update>\r\n\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得更新语句ByAssist
	 * 
	 * @param attr
	 * @param anyJDBC
	 * @return
	 */
	private String createUpdateByAssist(SuperAttribute attr, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		if (attr.getPrimaryKey() != null) {
			result.append("    <!-- 通过辅助工具Assist中的条件将" + attr.getClassName() + "中的数据更新到数据库对应的表中,包括值为null的数据-->\r\n");
			result.append("    <update id=\"update" + attr.getClassName() + "\" parameterType=\"map\">\r\n");
			result.append("        update " + attr.getTableName() + "\r\n");
			result.append("        <set>\r\n");
			List<AttributeCVF> cvf = attr.getAttributes();
			for (int i = 0; i < cvf.size(); i++) {
				if (cvf.get(i).getConlumn() != null) {
					result.append("                " + cvf.get(i).getConlumn() + "=" + "#{enti."
							+ cvf.get(i).getPropertyName());
					if (anyJDBC) {

						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("},\r\n");
				}
			}
			result.append("        </set>\r\n");
			result.append("        <if test=\"assist.require!=null\"><include refid=\"updateAssist\" /></if>\r\n");
			result.append("    </update>\r\n\r\n");
		}
		return result.toString();
	}

	/**
	 * 获得更新不为空语句ByAssist
	 * 
	 * @param attr
	 * @param anyJDBC
	 * @return
	 */
	private String createUpdateNonEmptyByAssist(SuperAttribute attr, boolean anyJDBC) {
		StringBuffer result = new StringBuffer();
		if (attr.getPrimaryKey() != null) {
			result.append("    <!-- 通过辅助工具Assist中的条件将" + attr.getClassName() + "中属性值不为null的数据更新到数据库对应的表中-->\r\n");
			result.append("    <update id=\"updateNonEmpty" + attr.getClassName() + "\" parameterType=\"map\">\r\n");
			result.append("        update " + attr.getTableName() + "\r\n");
			result.append("        <set>\r\n");
			List<AttributeCVF> cvf = attr.getAttributes();
			for (int i = 0; i < cvf.size(); i++) {
				if (cvf.get(i).getConlumn() != null) {
					result.append("            <if test=\"enti." + cvf.get(i).getPropertyName() + " != null\">\r\n");
					result.append("                " + cvf.get(i).getConlumn() + "=" + "#{enti."
							+ cvf.get(i).getPropertyName());
					if (anyJDBC) {

						if (cvf.get(i).getJdbcType() != null) {
							result.append(",jdbcType=" + cvf.get(i).getJdbcType());
						}
					}
					result.append("},\r\n");
					result.append("            </if>\r\n");
				}
			}
			result.append("        </set>\r\n");
			result.append("        <if test=\"assist.require!=null\"><include refid=\"updateAssist\" /></if>\r\n");
			result.append("    </update>\r\n\r\n");
		}
		return result.toString();
	}

	/**
	 * 查询出表id对应的java类型
	 * 
	 * @param cvfs
	 * @param str
	 * @return
	 */
	private String selectIdType(List<AttributeCVF> cvfs, String str) {
		for (int i = 0; i < cvfs.size(); i++) {
			if (cvfs.get(i).getConlumn() != null) {
				if (str.equals(cvfs.get(i).getConlumn())) {
					return cvfs.get(i).getJavaTypeValue();
				}
			}
		}
		return null;
	}

	/**
	 * 查询出表id对应的JDBC类型
	 * 
	 * @param cvfs
	 * @param str
	 * @return
	 */
	private String selectJDBCType(List<AttributeCVF> cvfs, String str) {
		for (int i = 0; i < cvfs.size(); i++) {
			if (cvfs.get(i).getConlumn() != null) {
				if (str.equals(cvfs.get(i).getConlumn())) {
					return cvfs.get(i).getJdbcType();
				}
			}
		}
		return null;
	}
}
