package com.data.pivot.plugin.tool;

import com.data.pivot.plugin.entity.DatabaseQueryConfig;
import com.data.pivot.plugin.enums.DBType;
import com.intellij.openapi.ui.Messages;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 查询工具类，支持多种数据库类型的查询（MySQL, PostgreSQL, Oracle, SQL Server, MongoDB）
 */
public class QueryTool {

    // 查询结果的限制条数
    private static final int LIMIT = 20;
    // 查询超时时间
    private static final int QUERY_TIMEOUT = 5;
    // 最大连接数
    private static final int MAX_CONNECTIONS = 10;
    // 连接池集合
    private static final Map<String, BlockingQueue<Connection>> connectionPools = new ConcurrentHashMap<>();
    // MongoDB 客户端集合
    private static final Map<String, MongoClient> mongoClients = new ConcurrentHashMap<>();

    private static Connection getConnection(String dataSourceId, String url, String user, String password, DBType dbType) throws InterruptedException, SQLException {
        BlockingQueue<Connection> pool = getOrCreateConnectionPool(dataSourceId, url, user, password, dbType);
        return pool.poll(QUERY_TIMEOUT, TimeUnit.SECONDS);
    }

    private static BlockingQueue<Connection> getOrCreateConnectionPool(String dataSourceId, String url, String user, String password, DBType dbType) {
        return connectionPools.computeIfAbsent(dataSourceId, id -> {
            BlockingQueue<Connection> pool = new LinkedBlockingQueue<>(MAX_CONNECTIONS);
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                try {
                    Connection connection = null;
                    switch (dbType) {
                        case MYSQL:
                            connection = DriverManager.getConnection(url + "?useUnicode=true&characterEncoding=UTF-8", user, password);
                            break;
                        case POSTGRES:
                            connection = DriverManager.getConnection(url + "?charSet=UTF-8", user, password);
                            break;
                        case MSSQL:
                            connection = DriverManager.getConnection(url + ";sendStringParametersAsUnicode=true", user, password);
                            break;
                        case ORACLE:
                            System.setProperty("oracle.jdbc.defaultNChar", "true");
                            connection = DriverManager.getConnection(url, user, password);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported DB type: " + dbType);
                    }
                    pool.offer(connection);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create a new connection", e);
                }
            }
            return pool;
        });
    }


    // 获取或创建 JDBC 连接池
    private static BlockingQueue<Connection> getOrCreateConnectionPool(String dataSourceId, String url, String user, String password) {
        return connectionPools.computeIfAbsent(dataSourceId, id -> {
            BlockingQueue<Connection> pool = new LinkedBlockingQueue<>(MAX_CONNECTIONS);
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                try {
                    Connection connection = DriverManager.getConnection(url, user, password);
                    pool.offer(connection);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create connection for " + dataSourceId+"\n\n"+e, e);
                }
            }
            return pool;
        });
    }

    // 获取连接
    private static Connection getConnection(String dataSourceId, String url, String user, String password) throws InterruptedException {
        BlockingQueue<Connection> pool = getOrCreateConnectionPool(dataSourceId, url, user, password);
        return pool.poll(QUERY_TIMEOUT, TimeUnit.SECONDS);
    }

    // 释放连接
    private static void releaseConnection(String dataSourceId, Connection connection) {
        BlockingQueue<Connection> pool = connectionPools.get(dataSourceId);
        if (pool != null) {
            pool.offer(connection);
        }
    }

    // 获取或创建 MongoDB 客户端
    private static MongoClient getOrCreateMongoClient(String clientId, String url, String username, String password, String authDatabase) {
        return mongoClients.computeIfAbsent(clientId, id -> {
            String uri;
            if (username != null && password != null) {
                uri = String.format("mongodb://%s:%s@%s/?authSource=%s", username, password, url, authDatabase);
            } else {
                uri = String.format("mongodb://%s", url);
            }
            return new MongoClient(new MongoClientURI(uri));
        });
    }

    // 查询方法，根据配置对象执行查询
    public static @Nullable List<Map<String, Object>> query(DatabaseQueryConfig config) {
        try {
            // 判断是否为直接 SQL 查询
            if (config.isDirectSqlQuery()) {
                return executeSql(config);
            }
            // 判断数据库类型并执行相应的查询
            if (DBType.MONGO.equals(config.getDbType())) {
                // 从 config.getUrl() 中解析出主机和端口
                String url = config.getUrl().replaceFirst("^mongodb://", "").replaceFirst("^//", "");
                MongoClient mongoClient = getOrCreateMongoClient(config.getDataSourceId(), url, config.getUser(), config.getPassword(), "admin");
                return queryMongoDB(mongoClient, config.getDbName(), config.getTableName(), config.getColumns(), config.getConditionField(), config.getLikeValue());
            }
            Connection connection = getConnection(config.getDataSourceId(), config.getUrl(), config.getUser(), config.getPassword(),config.getDbType());
            if (connection == null) {
                throw new SQLException("Unable to obtain connection within timeout");
            }
            try {
                String sql = generateSql(config);
                return executeQuery(connection, sql, config.getLikeValue());
            } finally {
                releaseConnection(config.getDataSourceId(), connection);
            }
        } catch (SQLTimeoutException timeoutException) {
            Messages.showErrorDialog("查询超时:\n\n" + timeoutException.getMessage(), "Query Data Error");
        } catch (SQLException sqlException) {
            Messages.showErrorDialog("SQL错误:\n\n" + sqlException.getMessage(), "Query Data Error");
        } catch (Exception exception) {
            Messages.showErrorDialog("数据库连接错误或未知错误,请检查DataGrip的连接情况:\n\n" + exception.getMessage(), "Query Data Error");
        }
        return Collections.emptyList();
    }

    // 执行直接 SQL 查询
    private static List<Map<String, Object>> executeSql(DatabaseQueryConfig config) throws Exception {
        if (DBType.MONGO.equals(config.getDbType())) {
            throw new IllegalArgumentException("Direct SQL execution is not supported for MongoDB");
        }

        Connection connection = getConnection(config.getDataSourceId(), config.getUrl(), config.getUser(), config.getPassword(),config.getDbType());
        if (connection == null) {
            throw new SQLException("Unable to obtain connection within timeout");
        }
        try {
            return executeQuery(connection, config.getSql(), null);
        } finally {
            releaseConnection(config.getDataSourceId(), connection);
        }
    }

    // 生成 SQL 查询语句
    private static String generateSql(DatabaseQueryConfig config) {
        String columnList = String.join(", ", config.getColumns());
        if (config.getColumns().size() == 1 && config.getColumns().get(0).equals("*")) {
            columnList = "*";
        }

        String conditionClause = "";
        if (config.getLikeValue() != null && !config.getLikeValue().isEmpty()) {
            conditionClause = String.format(" WHERE %s LIKE ?", config.getConditionField());
        }

        String sql;
        switch (config.getDbType()) {
            case MYSQL:
            case POSTGRES:
                sql = String.format("SELECT %s FROM %s.%s%s LIMIT %d",
                        columnList, config.getDbName(), config.getTableName(), conditionClause, LIMIT);
                break;
            case ORACLE:
                sql = String.format("SELECT %s FROM %s.%s%s AND ROWNUM <= %d",
                        columnList, config.getDbName(), config.getTableName(), conditionClause, LIMIT);
                break;
            case MSSQL:
                sql = config.getSchema() != null && !config.getSchema().isEmpty() ?
                        String.format("SELECT TOP %d %s FROM %s.%s.%s%s",
                                LIMIT, columnList, config.getDbName(), config.getSchema(), config.getTableName(), conditionClause) :
                        String.format("SELECT TOP %d %s FROM %s..%s%s",
                                LIMIT, columnList, config.getDbName(), config.getTableName(), conditionClause);
                break;
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + config.getDbType());
        }
        return sql;
    }

    // 执行 SQL 查询并返回结果
    private static List<Map<String, Object>> executeQuery(Connection connection, String sql, String likeValue) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (likeValue != null && !likeValue.isEmpty()) {
                preparedStatement.setString(1, "%" + likeValue + "%");
            }
            preparedStatement.setQueryTimeout(QUERY_TIMEOUT);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
            }
        }
        return results;
    }

    // 查询 MongoDB 数据库并返回结果
    private static List<Map<String, Object>> queryMongoDB(MongoClient mongoClient, String dbName, String tableName, List<String> columns,
                                                          String conditionField, String likeValue) {
        List<Map<String, Object>> results = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase(dbName);
        MongoCollection<Document> collection = database.getCollection(tableName);

        Consumer<Document> processDocument = document -> {
            Map<String, Object> row = new LinkedHashMap<>();
            if (columns.size() == 1 && columns.get(0).equals("*")) {
                row.putAll(document);
            } else {
                for (String column : columns) {
                    row.put(column, document.get(column));
                }
            }
            results.add(row);
        };

        if (likeValue != null && !likeValue.isEmpty()) {
            collection.find(Filters.regex(conditionField, ".*" + likeValue + ".*"))
                    .limit(LIMIT)
                    .forEach(processDocument);
        } else {
            collection.find()
                    .limit(LIMIT)
                    .forEach(processDocument);
        }

        return results;
    }

    // 关闭所有连接
    public static void closeAllConnections() {
        connectionPools.values().forEach(pool -> {
            while (!pool.isEmpty()) {
                try {
                    Connection connection = pool.poll();
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // log error or handle accordingly
                }
            }
        });
        mongoClients.values().forEach(MongoClient::close);
    }

    // 示例调用
    public static void main(String[] args) {
        List<String> columns = Arrays.asList("column1", "column2");

        DatabaseQueryConfig mysqlConfig = new DatabaseQueryConfig("mysql1", DBType.MYSQL, "jdbc:mysql://localhost:3306/dbname", "username", "password",
                "dbname", "", "tableName", columns, "conditionField", "likeValue", null);
        DatabaseQueryConfig postgresConfig = new DatabaseQueryConfig("postgresql1", DBType.POSTGRES, "jdbc:postgresql://localhost:5432/dbname", "username", "password",
                "dbname", "", "tableName", columns, "conditionField", "likeValue", null);
        DatabaseQueryConfig sqlServerConfig = new DatabaseQueryConfig("sqlserver1", DBType.MSSQL, "jdbc:sqlserver://10.24.1.135:1433;databaseName=dbname", "username", "password",
                "dbname", "", "tableName", columns, "conditionField", "likeValue", null);

        List<Map<String, Object>> result = query(mysqlConfig);
        System.out.println("MySQL: " + result);

        result = query(postgresConfig);
        System.out.println("PostgreSQL: " + result);

        result = query(sqlServerConfig);
        System.out.println("SQL Server: " + result);

        // 直接 SQL 查询示例
        String sql = "SELECT * FROM dbname.tableName WHERE conditionField LIKE ? LIMIT 20";
        DatabaseQueryConfig sqlConfig = new DatabaseQueryConfig("mysql1", DBType.MYSQL, "jdbc:mysql://localhost:3306/dbname", "username", "password",
                "dbname", "", "", null, "", "", sql);
        result = query(sqlConfig);
        System.out.println("直接 SQL 查询 (MySQL): " + result);

        closeAllConnections();
    }
}
