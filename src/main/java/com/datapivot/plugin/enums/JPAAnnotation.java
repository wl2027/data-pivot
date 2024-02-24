package com.datapivot.plugin.enums;

import cn.hutool.core.util.StrUtil;
import com.datapivot.plugin.constants.DataPivotConstants;

public enum JPAAnnotation {
    ;
    public enum Table{
        ENTITY("javax.persistence.Entity","Entity", new String[]{}),
        TABLE("javax.persistence.Table","Table",new String[]{DataPivotConstants.JPA_VALUE}),
        ;
        private String qualifiedName ;
        private String shortName ;
        private String[] parameterList ;

        public String getQualifiedName() {
            return qualifiedName;
        }

        public String getShortName() {
            return shortName;
        }

        public String[] getParameterList() {
            return parameterList;
        }

        Table(String qualifiedName,String shortName, String[] parameterList) {
            this.qualifiedName = qualifiedName;
            this.shortName = shortName;
            this.parameterList = parameterList;
        }
    }
    public enum Column{
        TRANSIENT("javax.persistence.Transient","Transient",new String[]{}),
        COLUMN("javax.persistence.Column","Column",new String[]{DataPivotConstants.JPA_VALUE}),
        ID("javax.persistence.Id","Id",new String[]{}),
        ;
        private String qualifiedName ;
        private String shortName ;
        private String[] parameterList ;

        public String getQualifiedName() {
            return qualifiedName;
        }

        public String getShortName() {
            return shortName;
        }

        public String[] getParameterList() {
            return parameterList;
        }

        Column(String qualifiedName,String shortName, String[] parameterList) {
            this.qualifiedName = qualifiedName;
            this.shortName = shortName;
            this.parameterList = parameterList;
        }
    }

}
