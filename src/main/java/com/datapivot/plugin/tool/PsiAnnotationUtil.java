package com.datapivot.plugin.tool;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PsiAnnotationUtil {
    @NotNull
    public static List<String> getAnnotationAttributeValues(PsiAnnotation annotation, String attr) {
        PsiAnnotationMemberValue value = annotation.findDeclaredAttributeValue(attr);
        List<String> values = new ArrayList<>();
        if (value instanceof PsiReferenceExpression) {
            PsiReferenceExpression expression = (PsiReferenceExpression) value;
            values.add(expression.getText());
        } else if (value instanceof PsiLiteralExpression) {
            values.add(((PsiLiteralExpression) value).getValue().toString());
        } else if (value instanceof PsiArrayInitializerMemberValue) {
            PsiAnnotationMemberValue[] initializers = ((PsiArrayInitializerMemberValue) value).getInitializers();
            for (PsiAnnotationMemberValue initializer : initializers) {
                values.add(initializer.getText().replaceAll("\\\"", ""));
            }
        }
        return values;
    }

    public static String getAnnotationAttributeValue(PsiAnnotation annotation, String attr) {
        List<String> values = getAnnotationAttributeValues(annotation, attr);
        if (!values.isEmpty()) {
            return values.get(0);
        }
        return null;
    }

    public static String getAnnotationValue(PsiAnnotation psiAnnotation, String attrName) {
        if (psiAnnotation == null) {
            return null;
        }
        PsiAnnotationParameterList parameterList = psiAnnotation.getParameterList();
        for (PsiNameValuePair attribute : parameterList.getAttributes()) {
            if (attrName.equals(attribute.getName()) ||(StrUtil.isEmpty(attribute.getName())&&attribute.getValue()!=null)) {
                PsiAnnotationMemberValue value = attribute.getValue();
                String text = value.getText();
                return text;
            }
        }
        return null;
    }
    public static String getAnnotationValue(PsiAnnotation psiAnnotation, List<String> attrNames) {
        if (psiAnnotation == null) {
            return null;
        }
        PsiAnnotationParameterList parameterList = psiAnnotation.getParameterList();
        for (PsiNameValuePair attribute : parameterList.getAttributes()) {
            if (attrNames.contains(attribute.getName()) ||(StrUtil.isEmpty(attribute.getName())&&attribute.getValue()!=null)) {
                PsiAnnotationMemberValue value = attribute.getValue();
                String text = value.getText();
                return text;
            }
        }
        return null;
    }

    public static String removeDoubleQuotes(String annotationValue) {
        return StrUtil.removeAll(annotationValue, CharUtil.DOUBLE_QUOTES);
    }
}
