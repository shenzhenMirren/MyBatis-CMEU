package pers.cmeu.common;

import java.util.ArrayList;
import java.util.List;

import pers.cmeu.models.AttributeCVF;
import pers.cmeu.models.SuperAttribute;

public class MapperMateriNoJDBC extends MapperFactory {
	private boolean isAssist;

	/**
	 * 初始化该工厂必须指定是以有Assist的模式还是没有Assist的模式true为有Assist,反过来false没有
	 * 
	 * @param isAssist
	 *            true为有Assist
	 */
	public MapperMateriNoJDBC(boolean isAssist) {
		super();
		this.isAssist = isAssist;
	}

	@Override
	public String getResultMap(String mspace, List<SuperAttribute> supAttr) {
		if (mspace == null || supAttr == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		StringBuffer oneInMany = new StringBuffer();// 用于记录记录主键给多对一天添加association可以使用
		boolean falg = true;// 结合oneInMany用于标记第一次为主表属性
		for (SuperAttribute sa : supAttr) {
			result.append("    <resultMap id=\"result_" + sa.getClassName() + "_Map\" type=\"" + mspace + "."
					+ sa.getClassName() + "\">\r\n");
			List<AttributeCVF> cvf = sa.getAttributes();
			for (int i = 0; i < cvf.size(); i++) {
				if (cvf.get(i).getCheck() != false) {

					if (i == 0) {
						if (sa.getPrimaryKey() != null && !"".equals(sa.getPrimaryKey())) {
							for (int j = 0; j < cvf.size(); j++) {
								if (sa.getPrimaryKey().equals(cvf.get(j).getConlumn())) {
									result.append("        <id column=\"" + sa.getClassName()+cvf.get(j).getPropertyName() + "\" property=\""
											+ cvf.get(j).getPropertyName() + "\" />\r\n");
									if (falg) {
										// 记录用于给多对一天添加association
										oneInMany.append("            <id column=\"" +  sa.getClassName()+cvf.get(j).getPropertyName()
												+ "\" property=\"" + cvf.get(j).getPropertyName() + "\" />\r\n");
									}
								}
							}
						}
					}
					if (cvf.get(i).getConlumn() != null) {
						if (cvf.get(i).getConlumn().equals(sa.getPrimaryKey())) {
							continue;
						} else {
							result.append("        <result column=\"" + sa.getClassName()+cvf.get(i).getPropertyName() + "\" property=\""
									+ cvf.get(i).getPropertyName() + "\" />\r\n");
							if (falg) {
								oneInMany.append("            <result column=\"" + sa.getClassName()+cvf.get(i).getPropertyName()
										+ "\" property=\"" + cvf.get(i).getPropertyName() + "\" />\r\n");
							}
						}
					} else {
						String type = cvf.get(i).getJavaTypeValue();
						String typePeo = cvf.get(i).getPropertyName();
						for (SuperAttribute cvfN : supAttr) {
							if (type.indexOf("<" + cvfN.getClassName() + ">") != -1) {
								result.append("        <collection property=\"" + typePeo + "\" ofType=\"" + mspace
										+ "." + cvfN.getClassName() + "\" resultMap=\"result_" + cvfN.getClassName()
										+ "_Map\"/>\r\n");
							} else if (type.equals(cvfN.getClassName())) {
								result.append("        <association property=\"" + typePeo + "\" javaType=\"" + mspace
										+ "." + type + "\">\r\n");
								result.append(oneInMany);
								result.append("        </association>\r\n");
							}
						}
					}
				}
			}

			result.append("    </resultMap>\r\n\r");
			falg = false;
		}
		return result.toString();
	}

	@Override
	public String getSQL(List<SuperAttribute> supAttr) {
		if (isAssist) {
			return createSqlHaveAS(supAttr);
		} else {
			return createSqlNoAS(supAttr);
		}
	}

	@Override
	public String getSelect(List<SuperAttribute> supAttr, String asSpace, String espace, String dbType) {
		StringBuffer result = new StringBuffer();

		if (isAssist) {
			result.append(getRowCountHaveAs(supAttr, asSpace, espace));
			result.append(getSelectListHaveAs(supAttr, asSpace, espace,dbType));
			result.append(getSelectById(supAttr, asSpace, espace));
		} else {
			result.append(getRowCountNoAs(supAttr, asSpace, espace));
			result.append(getSelectListNoAs(supAttr, asSpace, espace));
			result.append(getSelectById(supAttr, asSpace, espace));

		}

		return result.toString();
	}

	@Override
	public String getInsert(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		result.append(getInsertAll(supAttr, espace));
		result.append(getInsertNonEmpty(supAttr, espace));
		return result.toString();
	}

	@Override
	public String getUpdate(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		if (isAssist) {
			result.append(updateHaveAs(supAttr, espace));
			result.append(updateNonEmptyHaveAs(supAttr, espace));
			result.append(updateById(supAttr, espace));
			result.append(updateNonEmpty(supAttr, espace));
		} else {
			result.append(updateById(supAttr, espace));
			result.append(updateNonEmpty(supAttr, espace));
		}
		return result.toString();
	}

	@Override
	public String getDelete(List<SuperAttribute> supAttr, String asSpace, String espace) {
		if (isAssist) {
			return deleteByAssist(supAttr, asSpace, espace) + deleteById(supAttr, asSpace, espace);
		} else {
			return deleteById(supAttr, asSpace, espace);
		}

	}

	// ------------------实现区haveAs表示带Assist/noAs表示不带------------------------
	/**
	 * 实现有Assist的sql模块
	 * 
	 * @param supAttr
	 * @return
	 */
	private String createSqlHaveAS(List<SuperAttribute> supAttr) {
		StringBuffer result = new StringBuffer();
		result.append("    <sql id=\"Assist\">\r\n");
		result.append("        <where>\r\n");
		result.append("            <foreach collection=\"require\" item=\"req\" separator=\" \">\r\n");
		result.append("                ${req}\r\n");
		result.append("            </foreach>\r\n");
		result.append("        </where>\r\n");
		result.append("    </sql>\r\n\r\n");
		result.append("    <sql id=\"updateAssist\">\r\n");
		result.append("        <where>\r\n");
		result.append("            <foreach collection=\"assist.require\" item=\"req\" separator=\" \">\r\n");
		result.append("                ${req}\r\n");
		result.append("            </foreach>\r\n");
		result.append("        </where>\r\n");
		result.append("    </sql>\r\n\r\n");

		result.append(createSqlNoAS(supAttr));

		return result.toString();

	}

	/**
	 * 实现没有Assist的模块
	 * 
	 * @param supAttr
	 * @return
	 */
	private String createSqlNoAS(List<SuperAttribute> supAttr) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute Attr : supAttr) {
			result.append("    <sql id=\"" + Attr.getTableName() + "_Column\">\r\n");
			List<AttributeCVF> cvf = Attr.getAttributes();
			for (int i = 0; i < cvf.size(); i++) {
				if (cvf.get(i).getConlumn() == null) {
					continue;
				}
				if (i == 0) {
					result.append("        " + Attr.getTableName() + "." + cvf.get(i).getConlumn()+" as "+Attr.getClassName()+ cvf.get(i).getPropertyName());
				} else {
					result.append(" ," + Attr.getTableName() + "." + cvf.get(i).getConlumn()+" as "+Attr.getClassName()+ cvf.get(i).getPropertyName());
				}
			}
			result.append("\r\n");
			result.append("    </sql>\r\n\r\n");

		}

		return result.toString();
	}

	/**
	 * 实现带Assist的获得总行数
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String getRowCountHaveAs(List<SuperAttribute> supAttr, String asSpace, String espace) {
		StringBuffer result = new StringBuffer();

		for (SuperAttribute attr : supAttr) {
			result.append("    <select id=\"get" + attr.getClassName() + "RowCount\" parameterType=\"" + asSpace
					+ ".Assist\" resultType=\"java.lang.Long\">\r\n");
			if (attr.getPrimaryKey() != null) {
				result.append(
						"        select count(" + attr.getPrimaryKey() + ") from " + attr.getTableName() + "\r\n");
			} else {
				result.append("        select count(*) from " + attr.getTableName() + "\r\n");
			}
			result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
			result.append("    </select>\r\n");
		}
		return result.toString();
	}

	/**
	 * 实现没有Assist的获得总行数
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String getRowCountNoAs(List<SuperAttribute> supAttr, String asSpace, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute attr : supAttr) {
			result.append("    <select id=\"get" + attr.getClassName()
					+ "RowCount\" resultType=\"java.lang.Long\">\r\n");
			if (attr.getPrimaryKey() != null) {
				result.append(
						"        select count(" + attr.getPrimaryKey() + ") from " + attr.getTableName() + "\r\n");
			} else {
				result.append("        select count(*) from " + attr.getTableName() + "\r\n");
			}
			result.append("    </select>\r\n");
		}

		return result.toString();
	}

	/**
	 * 实现查询集合带Assist
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String getSelectListHaveAs(List<SuperAttribute> supAttr, String asSpace, String espace, String dbType) {
		if (dbType.equals("Oracle")) {
			return createOraclePage(supAttr, asSpace);
		}else if (dbType.equals("SqlServer")) {
			return createSqlServerPage(supAttr, asSpace);
		}else {
			return createMySqlAndPostgrePage(supAttr, asSpace);
		}
		
	}
	/**
	 * 生成MySql与PostgreSql的分页查询语句
	 * @param supAttr
	 * @param asSpace
	 * @return
	 */
	private String createMySqlAndPostgrePage(List<SuperAttribute> supAttr, String asSpace){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < supAttr.size(); i++) {
			result.append("    <select id=\"select" + supAttr.get(i).getClassName() + "\" parameterType=\"" + asSpace
					+ ".Assist\" resultMap=\"result_" + supAttr.get(i).getClassName() + "_Map\">\r\n");
			result.append("        select <if test=\"distinct !=null\">${distinct}</if>\r\n");
			result.append("        <include refid=\"" + supAttr.get(i).getTableName() + "_Column\" /> \r\n");
			List<String> fromList = new ArrayList<String>();
			List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
			for (AttributeCVF attr : cvf) {
				if (attr.getConlumn() == null) {
					String[] tc = selectOntOrMany(attr.getJavaTypeValue(), supAttr);
					if (tc != null) {
						result.append("        ,<include refid=\"" + tc[0] + "_Column\" /> \r\n");
						fromList.add(tc[0]);
					}
				}
			}
			result.append("        from " + supAttr.get(i).getTableName() + "\r\n");
			for (String tbn : fromList) {
				result.append("        inner join " + tbn + " on " + supAttr.get(i).getTableName() + "."
						+ supAttr.get(0).getPrimaryKey() + "=" + tbn + "." + supAttr.get(0).getPrimaryKey() + "\r\n");
			}
			result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
			result.append("        <if test=\"order !=null\">${order}</if>\r\n");
			result.append("        <if test=\"rowSize !=null\"> LIMIT #{rowSize} <if test=\"startRow !=null\"> OFFSET #{startRow}</if></if>\r\n");
			result.append("    </select> \r\n\r\n");
		}
		
		return result.toString();
		
	}
	/**
	 * 创建SqlServer的分页语句
	 * @param supAttr
	 * @param asSpace
	 * @return
	 */
	private String createSqlServerPage(List<SuperAttribute> supAttr, String asSpace){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < supAttr.size(); i++) {
			result.append("    <select id=\"select" + supAttr.get(i).getClassName() + "\" parameterType=\"" + asSpace
					+ ".Assist\" resultMap=\"result_" + supAttr.get(i).getClassName() + "_Map\">\r\n");
			result.append("        select * from\r\n");
			result.append("        (\r\n");
			result.append("            select <if test=\"distinct !=null\">${distinct}</if> ROW_NUMBER() over(<choose><when test=\"order!=null\">${order}</when><otherwise>order by "+supAttr.get(i).getTableName()+"."+supAttr.get(i).getPrimaryKey()+"</otherwise></choose>) as page,\r\n");
			result.append("            <include refid=\"" + supAttr.get(i).getTableName() + "_Column\" /> \r\n");
			List<String> fromList = new ArrayList<String>();
			List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
			for (AttributeCVF attr : cvf) {
				if (attr.getConlumn() == null) {
					//获取属性名对应对象的表名
					String[] tc = selectOntOrMany(attr.getJavaTypeValue(), supAttr);
					if (tc != null) {
						result.append("            ,<include refid=\"" + tc[0] + "_Column\" /> \r\n");
						fromList.add(tc[0]);
					}
				}
			}
			result.append("            from " + supAttr.get(i).getTableName() + "\r\n");
			for (String tbn : fromList) {
				result.append("            inner join " + tbn + " on " + supAttr.get(i).getTableName()+ "."
						+ supAttr.get(0).getPrimaryKey() + "=" + tbn + "." + supAttr.get(0).getPrimaryKey() + "\r\n");
			}
			result.append("        ) result\r\n");
			result.append("        <choose>\r\n");
			result.append("            <when test=\"require!=null\"><include refid=\"Assist\" />\r\n");
			result.append("                <if test=\"startRow!=null\">and page &gt; #{startRow} </if>\r\n");
			result.append("                <if test=\"endRow!=null\">and page &lt;= #{endRow} </if>\r\n");
			result.append("            </when>\r\n");
			result.append("            <otherwise>\r\n");
			result.append("                <choose>\r\n");
			result.append("                    <when test=\"startRow!=null\">where page &gt; #{startRow} <if test=\"endRow!=null\">and page &lt;= #{endRow} </if></when>\r\n");
			result.append("                    <otherwise><if test=\"endRow!=null\">where page &lt;= #{endRow}</if></otherwise>\r\n");
			result.append("                </choose>\r\n");
			result.append("            </otherwise>\r\n");
			result.append("        </choose>\r\n");
			result.append("    </select> \r\n\r\n");
		}
		return result.toString();
	}
	
	/**
	 * 创建Oracle分页语句
	 * @param supAttr
	 * @param asSpace
	 * @return
	 */
	private String createOraclePage(List<SuperAttribute> supAttr, String asSpace){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < supAttr.size(); i++) {
			result.append("    <select id=\"select" + supAttr.get(i).getClassName() + "\" parameterType=\"" + asSpace
					+ ".Assist\" resultMap=\"result_" + supAttr.get(i).getClassName() + "_Map\">\r\n");
			result.append("        select * from\r\n");
			result.append("        (\r\n");
			result.append("            select <if test=\"distinct !=null\">${distinct}</if> rownum as page,\r\n");
			result.append("            <include refid=\"" + supAttr.get(i).getTableName() + "_Column\" /> \r\n");
			List<String> fromList = new ArrayList<String>();
			List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
			for (AttributeCVF attr : cvf) {
				if (attr.getConlumn() == null) {
					String[] tc = selectOntOrMany(attr.getJavaTypeValue(), supAttr);
					if (tc != null) {
						result.append("            ,<include refid=\"" + tc[0] + "_Column\" /> \r\n");
						fromList.add(tc[0]);
					}
				}
			}
			result.append("            from " + supAttr.get(i).getTableName() + "\r\n");
			for (String tbn : fromList) {
				result.append("            inner join " + tbn + " on " + supAttr.get(i).getTableName() + "."
						+ supAttr.get(0).getPrimaryKey() + "=" + tbn + "." + supAttr.get(0).getPrimaryKey() + "\r\n");
			}
			result.append("            <if test=\"order !=null\">${order}</if>\r\n");
			result.append("        ) result\r\n");
			result.append("        <choose>\r\n");
			result.append("            <when test=\"require!=null\">\r\n");
			result.append("                <include refid=\"Assist\" />\r\n");
			result.append("                <if test=\"startRow!=null\">and page &gt; #{startRow} </if>\r\n");
			result.append("                <if test=\"endRow!=null\">and page &lt;= #{endRow} </if>\r\n");
			result.append("            </when>\r\n");
			result.append("            <otherwise>\r\n");
			result.append("                <choose>\r\n");
			result.append("                    <when test=\"startRow!=null\">where page &gt; #{startRow} <if test=\"endRow!=null\">and page &lt;= #{endRow} </if></when>\r\n");
			result.append("                    <otherwise><if test=\"endRow!=null\">where page &lt;= #{endRow}</if></otherwise>\r\n");
			result.append("                </choose>\r\n");
			result.append("            </otherwise>\r\n");
			result.append("        </choose>\r\n");
			result.append("    </select> \r\n\r\n");
		}
		return result.toString();
	}
	

	/**
	 * 实现查询集合不带Assist
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String getSelectListNoAs(List<SuperAttribute> supAttr, String asSpace, String espace) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < supAttr.size(); i++) {
			result.append("    <select id=\"select" + supAttr.get(i).getClassName() + "\" resultMap=\"result_"
					+ supAttr.get(i).getClassName() + "_Map\">\r\n");
			result.append("        select \r\n");
			result.append("        <include refid=\"" + supAttr.get(i).getTableName() + "_Column\" /> \r\n");
			List<String> fromList = new ArrayList<String>();
			List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
			for (AttributeCVF attr : cvf) {
				if (attr.getConlumn() == null) {
					String[] tc = selectOntOrMany(attr.getJavaTypeValue(), supAttr);
					if (tc != null) {
						result.append("        ,<include refid=\"" + tc[0] + "_Column\" /> \r\n");
						fromList.add(tc[0]);
					}
				}
			}
			result.append("        from " + supAttr.get(i).getTableName() + "\r\n");
			for (String tbn : fromList) {
				result.append("        inner join " + tbn + " on " + supAttr.get(i).getTableName() + "."
						+ supAttr.get(0).getPrimaryKey() + "=" + tbn + "." + supAttr.get(0).getPrimaryKey() + "\r\n");
			}
			result.append("    </select> \r\n\r\n");
		}
		return result.toString();
	}

	/**
	 * 实现通过id查询
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String getSelectById(List<SuperAttribute> supAttr, String asSpace, String espace) {

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < supAttr.size(); i++) {
			if (supAttr.get(i).getPrimaryKey() != null) {
				List<AttributeCVF> cvf = supAttr.get(i).getAttributes();
				for (int j = 0; j < cvf.size(); j++) {
					if (cvf.get(j).getConlumn() != null) {
						if (supAttr.get(i).getPrimaryKey().equals(cvf.get(j).getConlumn())) {
							result.append("    <select id=\"select" + supAttr.get(i).getClassName()
									+ "ById\" parameterType=\"java.lang." + cvf.get(j).getJavaTypeValue()
									+ "\" resultMap=\"result_" + supAttr.get(i).getClassName() + "_Map\">\r\n");
							result.append("        select \r\n");
							result.append(
									"        <include refid=\"" + supAttr.get(i).getTableName() + "_Column\" /> \r\n");
							List<String> fromList = new ArrayList<String>();
							for (AttributeCVF attr : cvf) {
								if (attr.getConlumn() == null) {
									String[] tc = selectOntOrMany(attr.getJavaTypeValue(), supAttr);
									if (tc != null) {
										result.append("        ,<include refid=\"" + tc[0] + "_Column\" /> \r\n");
										fromList.add(tc[0]);
									}
								}
							}
							result.append("        from " + supAttr.get(i).getTableName() + "\r\n");
							for (String tbn : fromList) {
								result.append("        inner join " + tbn + " on " + supAttr.get(i).getClassName() + "."
										+ supAttr.get(0).getPrimaryKey() + "=" + tbn + "."
										+ supAttr.get(0).getPrimaryKey() + "\r\n");
							}
							result.append("        where " + supAttr.get(i).getClassName() + "."
									+ supAttr.get(i).getPrimaryKey() + " = #{id}\r\n");
							result.append("    </select> \r\n\r\n");
							continue;
						}
					}

				}
			}
		}
		return result.toString();
	}

	/**
	 * 实现插入所有
	 * 
	 * @param supAttr
	 * @param espace
	 * @return
	 */
	private String getInsertAll(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute sa : supAttr) {

			result.append("    <insert id=\"insert" + sa.getClassName() + "\" parameterType=\"" + espace + "."
					+ sa.getClassName() + "\">\r\n");
			result.append("        insert into " + sa.getTableName() + "(");
			List<AttributeCVF> cvf = sa.getAttributes();
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
						result.append("#{" + cvf.get(i).getPropertyName() + "}");
					} else {
						result.append(",#{" + cvf.get(i).getPropertyName() + "}");
					}
				}
			}
			result.append(")\r\n");
			result.append("    </insert>\r\n\r\n");

		}
		return result.toString();
	}

	/**
	 * 实现插入不为空属性的
	 * 
	 * @param supAttr
	 * @param espace
	 * @return
	 */
	private String getInsertNonEmpty(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute sa : supAttr) {

			result.append("    <insert id=\"insertNonEmpty" + sa.getClassName() + "\" parameterType=\"" + espace + "."
					+ sa.getClassName() + "\">\r\n");
			result.append("        insert into " + sa.getTableName() + "\r\n");
			result.append("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\r\n");
			List<AttributeCVF> cvf = sa.getAttributes();
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
							+ cvf.get(i).getPropertyName() + "},</if>\r\n");
				}
			}
			result.append("        </trim>\r\n");
			result.append("    </insert>\r\n\r\n");

		}

		return result.toString();
	}

	/**
	 * 实现通过Id删除
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String deleteById(List<SuperAttribute> supAttr, String asSpace, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute sa : supAttr) {
			if (sa.getPrimaryKey() != null) {
				String javaIdType = selectIdType(sa.getAttributes(), sa.getPrimaryKey());
				if (javaIdType != null) {
					result.append("    <delete id=\"delete" + sa.getClassName() + "ById\" parameterType=\"java.lang."
							+ javaIdType + "\">\r\n");
					result.append("        delete from " + sa.getTableName() + "\r\n");
					result.append("        where " + sa.getPrimaryKey() + " = #{id}\r\n");
					result.append("    </delete>\r\n\r\n");
				}
			}
		}
		return result.toString();
	}

	/**
	 * 实现通过Assist删除
	 * 
	 * @param supAttr
	 * @param asSpace
	 * @param espace
	 * @return
	 */
	private String deleteByAssist(List<SuperAttribute> supAttr, String asSpace, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute sa : supAttr) {

			result.append(
					"    <delete id=\"delete" + sa.getClassName() + "\" parameterType=\"" + asSpace + ".Assist\">\r\n");
			result.append("        delete from " + sa.getTableName() + "\r\n");
			result.append("        <if test=\"require!=null\"><include refid=\"Assist\" /></if>\r\n");
			result.append("    </delete>\r\n\r\n");

		}
		return result.toString();
	}

	/**
	 * 实现通过Id更新全部
	 * 
	 * @param supAttr
	 * @param espace
	 * @return
	 */
	private String updateById(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		String tmpIdRow = "";
		for (SuperAttribute sa : supAttr) {
			if (sa.getPrimaryKey() != null) {
				boolean falg = true;
				result.append("    <update id=\"update" + sa.getClassName() + "ById\" parameterType=\"" + espace + "."
						+ sa.getClassName() + "\">\r\n");
				result.append("        update " + sa.getTableName() + " set\r\n");
				List<AttributeCVF> cvf = sa.getAttributes();
				for (int i = 0; i < cvf.size(); i++) {
					if (cvf.get(i).getConlumn() != null) {
						if (sa.getPrimaryKey().equals(cvf.get(i).getConlumn())) {
							tmpIdRow = cvf.get(i).getConlumn() + "=" + "#{" + cvf.get(i).getPropertyName() + "}\r\n";
						} else {
							if (falg) {
								result.append("            " + cvf.get(i).getConlumn() + "=" + "#{"
										+ cvf.get(i).getPropertyName() + "}\r\n");
								falg = false;
							} else {
								result.append("            ," + cvf.get(i).getConlumn() + "=" + "#{"
										+ cvf.get(i).getPropertyName() + "}\r\n");
							}

						}
					}
				}
				result.append("            where " + tmpIdRow);
				result.append("    </update>\r\n\r\n");
			}
		}
		return result.toString();
	}

	/**
	 * 实现通过Id更新不为空的
	 * 
	 * @param supAttr
	 * @param espace
	 * @return
	 */
	private String updateNonEmpty(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		String tmpIdRow = "";
		for (SuperAttribute sa : supAttr) {
			if (sa.getPrimaryKey() != null) {
				result.append("    <update id=\"updateNonEmpty" + sa.getClassName() + "ById\" parameterType=\"" + espace
						+ "." + sa.getClassName() + "\">\r\n");
				result.append("        update " + sa.getTableName() + "\r\n");
				result.append("        <set>\r\n");
				List<AttributeCVF> cvf = sa.getAttributes();
				for (int i = 0; i < cvf.size(); i++) {
					if (cvf.get(i).getConlumn() != null) {
						if (sa.getPrimaryKey().equals(cvf.get(i).getConlumn())) {
							tmpIdRow = cvf.get(i).getConlumn() + "=" + "#{" + cvf.get(i).getPropertyName() + "}\r\n";
						} else {
							result.append("            <if test=\"" + cvf.get(i).getPropertyName() + " != null\">\r\n");
							result.append("                " + cvf.get(i).getConlumn() + "=" + "#{"
									+ cvf.get(i).getPropertyName() + "},\r\n");
							result.append("            </if>\r\n");
						}
					}
				}
				result.append("        </set>\r\n");
				result.append("        where " + tmpIdRow);
				result.append("    </update>\r\n\r\n");
			}
		}
		return result.toString();
	}

	/**
	 * 实现带Assist的条件更新
	 * 
	 * @param supAttr
	 * @param espace
	 * @return
	 */
	private String updateHaveAs(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute sa : supAttr) {
			if (sa.getPrimaryKey() != null) {
				result.append("    <update id=\"update" + sa.getClassName() + "\" parameterType=\"map\">\r\n");
				result.append("        update " + sa.getTableName() + "\r\n");
				result.append("        <set>\r\n");
				List<AttributeCVF> cvf = sa.getAttributes();
				for (int i = 0; i < cvf.size(); i++) {
					if (cvf.get(i).getConlumn() != null) {
						result.append("                " + cvf.get(i).getConlumn() + "=" + "#{enti."
								+ cvf.get(i).getPropertyName() + "},\r\n");
					}
				}
				result.append("        </set>\r\n");
				result.append("        <if test=\"assist.require!=null\"><include refid=\"updateAssist\" /></if>\r\n");
				result.append("    </update>\r\n\r\n");
			}
		}
		return result.toString();
	}

	/**
	 * 实现通过Assist更新不为空属性的
	 * 
	 * @param supAttr
	 * @param espace
	 * @return
	 */
	private String updateNonEmptyHaveAs(List<SuperAttribute> supAttr, String espace) {
		StringBuffer result = new StringBuffer();
		for (SuperAttribute sa : supAttr) {
			if (sa.getPrimaryKey() != null) {
				result.append("    <update id=\"updateNonEmpty" + sa.getClassName() + "\" parameterType=\"map\">\r\n");
				result.append("        update " + sa.getTableName() + "\r\n");
				result.append("        <set>\r\n");
				List<AttributeCVF> cvf = sa.getAttributes();
				for (int i = 0; i < cvf.size(); i++) {
					if (cvf.get(i).getConlumn() != null) {
						result.append(
								"            <if test=\"enti." + cvf.get(i).getPropertyName() + " != null\">\r\n");
						result.append("                " + cvf.get(i).getConlumn() + "=" + "#{enti."
								+ cvf.get(i).getPropertyName() + "},\r\n");
						result.append("            </if>\r\n");
					}
				}
				result.append("        </set>\r\n");
				result.append("        <if test=\"assist.require!=null\"><include refid=\"updateAssist\" /></if>\r\n");
				result.append("    </update>\r\n\r\n");
			}
		}
		return result.toString();
	}

	// ---------------------通用方法-------------------------------------
	/**
	 * 下标0返回表名,1放回类名
	 * 
	 * @param reqi
	 * @param supAttr
	 * @return
	 */
	public String[] selectOntOrMany(String reqi, List<SuperAttribute> supAttr) {
		if (reqi != null && supAttr != null) {

		}
		for (SuperAttribute sa : supAttr) {
			if (reqi.equals(sa.getClassName())||reqi.indexOf(("<"+sa.getClassName())+">") != -1) {
				return new String[] { sa.getTableName(), sa.getClassName() };
			}
		}

		return null;
	}

	/**
	 * 查询出表id对应的java类型
	 * 
	 * @param cvfs
	 * @param str
	 * @return
	 */
	public String selectIdType(List<AttributeCVF> cvfs, String str) {
		for (int i = 0; i < cvfs.size(); i++) {
			if (cvfs.get(i).getConlumn() != null) {
				if (str.equals(cvfs.get(i).getConlumn())) {
					return cvfs.get(i).getJavaTypeValue();
				}
			}
		}
		return null;
	}
}
