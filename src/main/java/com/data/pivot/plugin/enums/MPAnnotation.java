package com.data.pivot.plugin.enums;

import com.data.pivot.plugin.constants.DataPivotConstants;

public enum MPAnnotation {
    ;
    public enum Table{
        TABLE_NAME("com.baomidou.mybatisplus.annotation.TableName","TableName", new String[]{DataPivotConstants.MP_VALUE}),
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
        TABLE_ID("com.baomidou.mybatisplus.annotation.TableId","TableId",new String[]{DataPivotConstants.MP_VALUE}),
        TABLE_FIELD("com.baomidou.mybatisplus.annotation.TableField","TableField",new String[]{DataPivotConstants.MP_VALUE}),
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
