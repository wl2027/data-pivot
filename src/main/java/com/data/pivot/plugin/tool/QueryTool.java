package com.data.pivot.plugin.tool;

import com.data.pivot.plugin.entity.DatabaseQueryConfig;
import com.data.pivot.plugin.enums.DBType;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 查询工具类，复用 IntelliJ IDEA Database Tools 中已配置的数据源驱动。
 */
public class QueryTool {

    private static final int LIMIT = 20;
    private static final int QUERY_TIMEOUT = 5;
    private static final int MAX_CONNECTIONS = 10;
    private static final ConcurrentMap<String, BlockingQueue<Connection>> connectionPools = new ConcurrentHashMap<>();

    private static Connection getConnection(DatabaseQueryConfig config) throws InterruptedException, SQLException {
        if (DBType.MONGO.equals(config.getDbType())) {
            throw new SQLException("MongoDB query is not supported without the bundled MongoDB driver. Use Database Tools query files for MongoDB.");
        }

        DataSourceDriverUtil.ensureDriverRegistered(
                config.getDataSourceId(),
                config.getDriverClassName(),
                config.getDriverClassRootUrls()
        );

        String poolKey = config.getDataSourceId() + "@" + config.getUrl();
        BlockingQueue<Connection> pool = getOrCreateConnectionPool(poolKey, config);
        return pool.poll(QUERY_TIMEOUT, TimeUnit.SECONDS);
    }

    private static BlockingQueue<Connection> getOrCreateConnectionPool(String poolKey, DatabaseQueryConfig config) {
        return connectionPools.computeIfAbsent(poolKey, id -> {
            BlockingQueue<Connection> pool = new LinkedBlockingQueue<>(MAX_CONNECTIONS);
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                try {
                    pool.offer(createConnection(config));
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create a new connection", e);
                }
            }
            return pool;
        });
    }

    private static Connection createConnection(DatabaseQueryConfig config) throws SQLException {
        DBType dbType = config.getDbType();
        if (dbType == null) {
            throw new SQLException("Unsupported database type");
        }

        String url = config.getUrl();
        switch (dbType) {
            case MYSQL:
                url = addUrlParameters(url, "useUnicode=true&characterEncoding=UTF-8");
                break;
            case POSTGRES:
                url = addUrlParameters(url, "charSet=UTF-8");
                break;
            case MSSQL:
                url = url + (url.endsWith(";") ? "" : ";") + "sendStringParametersAsUnicode=true";
                break;
            case ORACLE:
                System.setProperty("oracle.jdbc.defaultNChar", "true");
                break;
            default:
                throw new SQLException("Unsupported DB type: " + dbType);
        }
        return DriverManager.getConnection(url, config.getUser(), config.getPassword());
    }

    static String addUrlParameters(String url, String parameters) {
        if (url.contains("?")) {
            return url + "&" + parameters;
        }
        return url + "?" + parameters;
    }

    private static void releaseConnection(DatabaseQueryConfig config, Connection connection) {
        String poolKey = config.getDataSourceId() + "@" + config.getUrl();
        BlockingQueue<Connection> pool = connectionPools.get(poolKey);
        if (pool != null) {
            pool.offer(connection);
        }
    }

    public static @Nullable List<Map<String, Object>> query(DatabaseQueryConfig config) {
        try {
            if (config.isDirectSqlQuery()) {
                return executeSql(config);
            }

            Connection connection = getConnection(config);
            if (connection == null) {
                throw new SQLException("Unable to obtain connection within timeout");
            }
            try {
                String sql = generateSql(config);
                return executeQuery(connection, sql, config.getLikeValue());
            } finally {
                releaseConnection(config, connection);
            }
        } catch (SQLTimeoutException timeoutException) {
            Messages.showErrorDialog("查询超时:\n\n" + timeoutException.getMessage(), "Query Data Error");
        } catch (SQLException sqlException) {
            Messages.showErrorDialog("SQL错误:\n\n" + sqlException.getMessage(), "Query Data Error");
        } catch (Exception exception) {
            Messages.showErrorDialog("数据库连接错误或未知错误,请检查 IDEA 数据源连接情况:\n\n" + exception.getMessage(), "Query Data Error");
        }
        return Collections.emptyList();
    }

    private static List<Map<String, Object>> executeSql(DatabaseQueryConfig config) throws Exception {
        Connection connection = getConnection(config);
        if (connection == null) {
            throw new SQLException("Unable to obtain connection within timeout");
        }
        try {
            return executeQuery(connection, config.getSql(), null);
        } finally {
            releaseConnection(config, connection);
        }
    }

    static String generateSql(DatabaseQueryConfig config) {
        String columnList = String.join(", ", config.getColumns());
        if (config.getColumns().size() == 1 && config.getColumns().get(0).equals("*")) {
            columnList = "*";
        }

        String conditionClause = "";
        if (config.getLikeValue() != null && !config.getLikeValue().isEmpty()) {
            conditionClause = String.format(" WHERE %s LIKE ?", config.getConditionField());
        }

        switch (config.getDbType()) {
            case MYSQL:
            case POSTGRES:
                return String.format("SELECT %s FROM %s.%s%s LIMIT %d",
                        columnList, config.getDbName(), config.getTableName(), conditionClause, LIMIT);
            case ORACLE:
                String oracleLimit = conditionClause.isEmpty() ? " WHERE ROWNUM <= " : " AND ROWNUM <= ";
                return String.format("SELECT %s FROM %s.%s%s%s%d",
                        columnList, config.getDbName(), config.getTableName(), conditionClause, oracleLimit, LIMIT);
            case MSSQL:
                if (config.getSchema() != null && !config.getSchema().isEmpty()) {
                    return String.format("SELECT TOP %d %s FROM %s.%s.%s%s",
                            LIMIT, columnList, config.getDbName(), config.getSchema(), config.getTableName(), conditionClause);
                }
                return String.format("SELECT TOP %d %s FROM %s..%s%s",
                        LIMIT, columnList, config.getDbName(), config.getTableName(), conditionClause);
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + config.getDbType());
        }
    }

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

    public static void closeAllConnections() {
        connectionPools.values().forEach(pool -> {
            while (!pool.isEmpty()) {
                try {
                    Connection connection = pool.poll();
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ignored) {
                    // Ignore close failures while disposing pooled connections.
                }
            }
        });
        connectionPools.clear();
    }
}
