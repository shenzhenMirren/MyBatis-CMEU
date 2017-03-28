package pers.cmeu.models;

public enum DBType {
	Oracle("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s:%s:%s", "oracleJDBC.jar"),
    MySQL("com.mysql.jdbc.Driver", "jdbc:mysql://%s:%s/%s?useUnicode=true&useSSL=false&characterEncoding=%s", "mysqlJDBC.jar"),
    SqlServer("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://%s:%s;databaseName=%s","sqlserverJDBC.jar"),
    PostgreSQL("org.postgresql.Driver", "jdbc:postgresql://%s:%s/%s", "postgresqlJDBC.jar");
    private final String driverClass;
    private final String connectionUrlPattern;
    private final String connectorJarFile;

    DBType(String driverClass, String connectionUrlPattern, String connectorJarFile) {
        this.driverClass = driverClass;
        this.connectionUrlPattern = connectionUrlPattern;
        this.connectorJarFile = connectorJarFile;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getConnectionUrlPattern() {
        return connectionUrlPattern;
    }

    public String getConnectorJarFile() {
        return connectorJarFile;
    }
    
}