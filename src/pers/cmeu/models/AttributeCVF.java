package pers.cmeu.models;



import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;

public class AttributeCVF {
	private ColumnItem columnItem;//用于存储子表子集
	private BooleanProperty check = new SimpleBooleanProperty(true);;
	private StringProperty conlumn= new SimpleStringProperty();
	private StringProperty jdbcType =new SimpleStringProperty();
	private ComboBox<String> javaType =new ComboBox<String>();
	private StringProperty propertyName= new SimpleStringProperty();
	private String comment;//表列的注释

	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public ColumnItem getColumnItem() {
		return columnItem;
	}

	public void setColumnItem(ColumnItem columnItem) {
		this.columnItem = columnItem;
	}

	public BooleanProperty checkProperty() {
		return check;
	}
	
	public Boolean getCheck() {
		return check.get();
	}
	
	public void setCheck(Boolean check) {
		this.check.set(check);
	}

	public StringProperty conlumnProperty() {
		return conlumn;
	}
	public String getConlumn() {
		return conlumn.get();
	}
	public void setConlumn(String conlumn) {
		this.setPropertyName(conlumn);
		this.conlumn.set(conlumn);
	}
	
	public StringProperty jdbcTypeProperty() {
		return jdbcType;
	}
	
	public String getJdbcType() {
		return jdbcType.get();
	}
	
	public void setJdbcType(String jdbcType) {
		this.jdbcType.set(jdbcType);
	}
	
	public ComboBox<String> getJavaType() {
		return javaType;
	}
	
	public String getJavaTypeValue() {
		return javaType.getValue();
	}
	
	
	public void setJavaType(String javaType) {
		this.javaType.setValue(javaType); 
		
	}
	
	public String getPropertyName() {
		return propertyName.get();
	}
	public void setPropertyName(String propertyName) {
		this.propertyName.set(propertyName);
		
	}

	public AttributeCVF() {
		super();
		javaType.setEditable(true);
		javaType.getItems().addAll("int","double","char","long",
				"java.util.Date","java.sql.Date","java.time.LocalDate","java.time.LocalTime","java.time.LocalDateTime","java.util.List<E>",
				"java.util.Set<E>","java.util.Map<K, V>","String",
				"Double","Integer","Long","Object");
		  
	}

	@Override
	public String toString() {
		return "AttributeCVF [columnItem=" + columnItem + ", check=" + check + ", conlumn=" + conlumn + ", jdbcType="
				+ jdbcType + ", javaType=" + javaType + ", propertyName=" + propertyName + "]";
	}
	
	
	
}
