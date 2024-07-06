package com.data.pivot.plugin.config;

import cn.hutool.core.collection.ListUtil;
import com.data.pivot.plugin.entity.custom.DataPivotCustomAnnotationInfo;
import com.data.pivot.plugin.entity.custom.DataPivotCustomScriptInfo;
import com.data.pivot.plugin.entity.custom.DataPivotCustomSqlInfo;
import com.data.pivot.plugin.entity.custom.DataPivotMapperInfo;
import com.data.pivot.plugin.entity.custom.DataPivotMapperMethodInfo;
import com.data.pivot.plugin.entity.custom.DataPivotStrategyInfo;
import com.data.pivot.plugin.constants.DataPivotConstants;
import com.data.pivot.plugin.enums.DefaultStrategyType;
import com.data.pivot.plugin.enums.JPAAnnotation;
import com.data.pivot.plugin.enums.MPAnnotation;
import com.data.pivot.plugin.enums.MapperMethodType;

public class DataPivotDefaultInitializer {
    static public DataPivotCustomSqlInfo getDefaultSql(){
        DataPivotCustomSqlInfo dataPivotCustomSqlInfo = new DataPivotCustomSqlInfo();
        dataPivotCustomSqlInfo.setCode(DataPivotConstants.DEFAULT_SQL_CODE);
        dataPivotCustomSqlInfo.setContent(DataPivotConstants.DEFAULT_SQL_CONTENT);
        return dataPivotCustomSqlInfo;
    }
    static public DataPivotStrategyInfo getHUStrategy() {
        DataPivotStrategyInfo huStrategy = new DataPivotStrategyInfo();
        huStrategy.setCode(DefaultStrategyType.HUMP_UNDERLINE.getCode());
        huStrategy.setName(DefaultStrategyType.HUMP_UNDERLINE.getCode());

        DataPivotMapperInfo ormMapperInfo = new DataPivotMapperInfo();
        DataPivotMapperMethodInfo toUnderScoreMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotCustomScriptInfo toUnderScoreScriptInfo = getToUnderScoreScriptInfo();
        toUnderScoreMethodInfo.setType(MapperMethodType.SCRIPT.getName());
        toUnderScoreMethodInfo.setCustomScript(toUnderScoreScriptInfo);


        DataPivotMapperInfo romMapperInfo = new DataPivotMapperInfo();
        DataPivotMapperMethodInfo toCamelCaseMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotMapperMethodInfo toBigCamelCaseMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotCustomScriptInfo toCamelCase = getToCamelCaseScriptInfo();
        DataPivotCustomScriptInfo toBigCamelCase = getToBigCamelCaseScriptInfo();
        toCamelCaseMethodInfo.setType(MapperMethodType.SCRIPT.getName());
        toCamelCaseMethodInfo.setCustomScript(toCamelCase);
        toBigCamelCaseMethodInfo.setType(MapperMethodType.SCRIPT.getName());
        toBigCamelCaseMethodInfo.setCustomScript(toBigCamelCase);

        ormMapperInfo.setStructMethod(toUnderScoreMethodInfo);
        ormMapperInfo.setElementMethod(toUnderScoreMethodInfo);
        romMapperInfo.setStructMethod(toBigCamelCaseMethodInfo);
        romMapperInfo.setElementMethod(toCamelCaseMethodInfo);

        huStrategy.setOrmMapper(ormMapperInfo);
        huStrategy.setRomMapper(romMapperInfo);
        return huStrategy;
    }

    static public DataPivotCustomScriptInfo getToBigCamelCaseScriptInfo() {
        DataPivotCustomScriptInfo toCamelCase = new DataPivotCustomScriptInfo();
        toCamelCase.setContent(DataPivotConstants.TO_BIG_CAMEL_CASE_SCRIPT);
        return toCamelCase;
    }
    static public DataPivotCustomScriptInfo getToCamelCaseScriptInfo() {
        DataPivotCustomScriptInfo toCamelCase = new DataPivotCustomScriptInfo();
        toCamelCase.setContent(DataPivotConstants.TO_CAMEL_CASE_SCRIPT);
        return toCamelCase;
    }

    static public DataPivotCustomScriptInfo getToUnderScoreScriptInfo() {
        DataPivotCustomScriptInfo toUnderScore = new DataPivotCustomScriptInfo();
        toUnderScore.setContent(DataPivotConstants.TO_UNDER_SCORE_SCRIPT);
        return toUnderScore;
    }

    static public DataPivotStrategyInfo getMPStrategy() {
        DataPivotStrategyInfo mpStrategy = new DataPivotStrategyInfo();
        mpStrategy.setCode(DefaultStrategyType.MPAnnotation.getCode());
        mpStrategy.setName(DefaultStrategyType.MPAnnotation.getCode());
        //mp-table
        DataPivotMapperInfo ormMapperInfo = new DataPivotMapperInfo();
        DataPivotMapperInfo romMapperInfo = new DataPivotMapperInfo();
        DataPivotMapperMethodInfo ormStructMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotMapperMethodInfo romStructMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotCustomAnnotationInfo tableAnnotationInfo = new DataPivotCustomAnnotationInfo();
        tableAnnotationInfo.setAnnotationQualifiedName(MPAnnotation.Table.TABLE_NAME.getQualifiedName());
        tableAnnotationInfo.setAnnotationParameters(ListUtil.toList(MPAnnotation.Table.TABLE_NAME.getParameterList()));
        romStructMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        romStructMethodInfo.setCustomAnnotation(ListUtil.toList(tableAnnotationInfo));
        ormStructMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        ormStructMethodInfo.setCustomAnnotation(ListUtil.toList(tableAnnotationInfo));
        //mp-column
        DataPivotMapperMethodInfo ormElementMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotMapperMethodInfo romElementMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotCustomAnnotationInfo idAnnotationInfo = new DataPivotCustomAnnotationInfo();
        idAnnotationInfo.setAnnotationQualifiedName(MPAnnotation.Column.TABLE_ID.getQualifiedName());
        idAnnotationInfo.setAnnotationParameters(ListUtil.toList(MPAnnotation.Column.TABLE_ID.getParameterList()));
        DataPivotCustomAnnotationInfo columnAnnotationInfo = new DataPivotCustomAnnotationInfo();
        columnAnnotationInfo.setAnnotationQualifiedName(MPAnnotation.Column.TABLE_FIELD.getQualifiedName());
        columnAnnotationInfo.setAnnotationParameters(ListUtil.toList(MPAnnotation.Column.TABLE_FIELD.getParameterList()));

        romElementMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        romElementMethodInfo.setCustomAnnotation(ListUtil.toList(idAnnotationInfo,columnAnnotationInfo));
        romElementMethodInfo.setDefaultMethod(getToCamelCaseScriptInfo());

        ormElementMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        ormElementMethodInfo.setCustomAnnotation(ListUtil.toList(idAnnotationInfo,columnAnnotationInfo));
        ormElementMethodInfo.setDefaultMethod(getToUnderScoreScriptInfo());

        DataPivotCustomScriptInfo toBigCamelCase = getToBigCamelCaseScriptInfo();
        DataPivotCustomScriptInfo toUnderScoreScriptInfo = getToUnderScoreScriptInfo();
        romStructMethodInfo.setDefaultMethod(toBigCamelCase);
        ormStructMethodInfo.setDefaultMethod(toUnderScoreScriptInfo);

        ormMapperInfo.setStructMethod(ormStructMethodInfo);
        ormMapperInfo.setElementMethod(ormElementMethodInfo);

        romMapperInfo.setStructMethod(romStructMethodInfo);
        romMapperInfo.setElementMethod(romElementMethodInfo);


        mpStrategy.setOrmMapper(ormMapperInfo);
        mpStrategy.setRomMapper(romMapperInfo);
        return mpStrategy;
    }

    static public DataPivotStrategyInfo getJPAStrategy() {
        DataPivotStrategyInfo jpaStrategy = new DataPivotStrategyInfo();
        jpaStrategy.setCode(DefaultStrategyType.JPAAnnotation.getCode());
        jpaStrategy.setName(DefaultStrategyType.JPAAnnotation.getCode());
        //jpa-table
        DataPivotMapperInfo ormMapperInfo = new DataPivotMapperInfo();
        DataPivotMapperInfo romMapperInfo = new DataPivotMapperInfo();
        DataPivotMapperMethodInfo romStructMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotMapperMethodInfo ormStructMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotCustomAnnotationInfo jpaTableAnnotationInfo = new DataPivotCustomAnnotationInfo();
        jpaTableAnnotationInfo.setAnnotationQualifiedName(JPAAnnotation.Table.TABLE.getQualifiedName());
        jpaTableAnnotationInfo.setAnnotationParameters(ListUtil.toList(JPAAnnotation.Table.TABLE.getParameterList()));
        DataPivotCustomAnnotationInfo jpaEntityAnnotationInfo = new DataPivotCustomAnnotationInfo();
        jpaEntityAnnotationInfo.setAnnotationQualifiedName(JPAAnnotation.Table.ENTITY.getQualifiedName());
        jpaEntityAnnotationInfo.setAnnotationParameters(ListUtil.toList(JPAAnnotation.Table.ENTITY.getParameterList()));
        romStructMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        romStructMethodInfo.setCustomAnnotation(ListUtil.toList(jpaTableAnnotationInfo,jpaEntityAnnotationInfo));
        ormStructMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        ormStructMethodInfo.setCustomAnnotation(ListUtil.toList(jpaTableAnnotationInfo,jpaEntityAnnotationInfo));
        //jpa-column
        DataPivotMapperMethodInfo ormElementMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotMapperMethodInfo romElementMethodInfo = new DataPivotMapperMethodInfo();
        DataPivotCustomAnnotationInfo jpaColumnAnnotationInfo = new DataPivotCustomAnnotationInfo();
        jpaColumnAnnotationInfo.setAnnotationQualifiedName(JPAAnnotation.Column.COLUMN.getQualifiedName());
        jpaColumnAnnotationInfo.setAnnotationParameters(ListUtil.toList(JPAAnnotation.Column.COLUMN.getParameterList()));
        DataPivotCustomAnnotationInfo jpaIdAnnotationInfo = new DataPivotCustomAnnotationInfo();
        jpaIdAnnotationInfo.setAnnotationQualifiedName(JPAAnnotation.Column.ID.getQualifiedName());
        jpaIdAnnotationInfo.setAnnotationParameters(ListUtil.toList(JPAAnnotation.Column.ID.getParameterList()));
        
        ormElementMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        ormElementMethodInfo.setCustomAnnotation(ListUtil.toList(jpaIdAnnotationInfo,jpaColumnAnnotationInfo));
        ormElementMethodInfo.setDefaultMethod(getToUnderScoreScriptInfo());
        romElementMethodInfo.setType(MapperMethodType.ANNOTATION.getName());
        romElementMethodInfo.setCustomAnnotation(ListUtil.toList(jpaIdAnnotationInfo,jpaColumnAnnotationInfo));
        romElementMethodInfo.setDefaultMethod(getToCamelCaseScriptInfo());

        DataPivotCustomScriptInfo toBigCamelCase = getToBigCamelCaseScriptInfo();
        DataPivotCustomScriptInfo toUnderScoreScriptInfo = getToUnderScoreScriptInfo();
        romStructMethodInfo.setDefaultMethod(toBigCamelCase);
        ormStructMethodInfo.setDefaultMethod(toUnderScoreScriptInfo);

        ormMapperInfo.setStructMethod(ormStructMethodInfo);
        ormMapperInfo.setElementMethod(ormElementMethodInfo);


        romMapperInfo.setStructMethod(romStructMethodInfo);
        romMapperInfo.setElementMethod(romElementMethodInfo);


        jpaStrategy.setOrmMapper(ormMapperInfo);
        jpaStrategy.setRomMapper(romMapperInfo);
        return jpaStrategy;
    }
}
