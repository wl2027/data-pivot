package com.data.pivot.plugin.tool;

import com.data.pivot.plugin.entity.custom.DataPivotCustomSqlInfo;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DatabaseUtilTest {
    @Test
    public void buildConnectionStringAppendsDatabaseAndPropertiesInOrder() {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("useSSL", "false");
        properties.put("serverTimezone", "UTC");

        String url = DatabaseUtil.buildConnectionString("jdbc:mysql://localhost:3306", properties, "demo");

        assertEquals("jdbc:mysql://localhost:3306/demo?useSSL=false&serverTimezone=UTC", url);
    }

    @Test
    public void buildConnectionStringWithoutPropertiesReturnsOriginalUrl() {
        String url = DatabaseUtil.buildConnectionString("jdbc:postgresql://localhost:5432/demo", Map.of());

        assertEquals("jdbc:postgresql://localhost:5432/demo", url);
    }

    @Test
    public void createQuerySqlReplacesTableAndColumnPlaceholders() {
        DataPivotCustomSqlInfo sqlInfo = new DataPivotCustomSqlInfo();
        sqlInfo.setContent("select * from {tableName} where {columnName} is not null");

        String sql = DatabaseUtil.createQuerySql(sqlInfo, "user_account", "user_id");

        assertEquals("select * from user_account where user_id is not null", sql);
    }
}
