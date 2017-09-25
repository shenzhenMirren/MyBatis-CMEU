package pers.cmeu.models;

/**
 * 实体类的配置文件
 * 
 * @author Mirren
 *
 */
public class ClassConfig {
	private boolean getAndSet = true;
	private boolean construct = true;
	private boolean constructAll = true;
	private boolean unlineCamel = true;
	private boolean seriz = false;
	private boolean createJDBCType = false;

	public ClassConfig() {
		super();
	}

	public ClassConfig(boolean getAndSet, boolean construct, boolean constructAll, boolean unlineCamel, boolean seriz,
			boolean createJDBCType) {
		super();
		this.getAndSet = getAndSet;
		this.construct = construct;
		this.constructAll = constructAll;
		this.unlineCamel = unlineCamel;
		this.seriz = seriz;
		this.createJDBCType = createJDBCType;
	}

	public boolean isGetAndSet() {
		return getAndSet;
	}

	public void setGetAndSet(boolean getAndSet) {
		this.getAndSet = getAndSet;
	}

	public boolean isConstruct() {
		return construct;
	}

	public void setConstruct(boolean construct) {
		this.construct = construct;
	}

	public boolean isConstructAll() {
		return constructAll;
	}

	public void setConstructAll(boolean constructAll) {
		this.constructAll = constructAll;
	}

	public boolean isUnlineCamel() {
		return unlineCamel;
	}

	public void setUnlineCamel(boolean unlineCamel) {
		this.unlineCamel = unlineCamel;
	}

	public boolean isSeriz() {
		return seriz;
	}

	public void setSeriz(boolean seriz) {
		this.seriz = seriz;
	}

	public boolean isCreateJDBCType() {
		return createJDBCType;
	}

	public void setCreateJDBCType(boolean createJDBCType) {
		this.createJDBCType = createJDBCType;
	}

	@Override
	public String toString() {
		return "ClassConfig [getAndSet=" + getAndSet + ", construct=" + construct + ", constructAll=" + constructAll
				+ ", unlineCamel=" + unlineCamel + ", seriz=" + seriz + ", createJDBCType=" + createJDBCType + "]";
	}

}
