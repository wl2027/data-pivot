package com.data.pivot.plugin.enums;

import com.intellij.database.Dbms;

public enum DBType {
    MYSQL(Dbms.MYSQL.getName()),
    POSTGRES(Dbms.POSTGRES.getName()),
    ORACLE(Dbms.ORACLE.getName()),
    MSSQL(Dbms.MSSQL.getName()),
    MONGO(Dbms.MONGO.getName()),
    ;
    private String name;

    public String getName() {
        return name;
    }

    public static DBType getByName(String name) {
        for (DBType value : values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    DBType(String name) {
        this.name = name;
    }
}
