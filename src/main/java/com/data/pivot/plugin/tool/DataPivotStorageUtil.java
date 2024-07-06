package com.data.pivot.plugin.tool;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.intellij.ide.util.PropertiesComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单持久化
 */
public class DataPivotStorageUtil {

    static private PropertiesComponent getPropertiesComponent(){
        return PropertiesComponent.getInstance(ProjectUtils.getCurrProject());
    }

    static public <E> void setData(E value){
        String name = value.getClass().getName();
        PropertiesComponent instance = getPropertiesComponent();
        instance.setValue(name, JSONUtil.toJsonStr(value));
    }
    static public <E> E getData(Class<E> eClass){
        PropertiesComponent instance = getPropertiesComponent();
        String value = instance.getValue(eClass.getName());
        E bean = JSONUtil.toBean(value, eClass);
        return bean;
    }
    static public <E> void setListData(String name,List<E> value){
        PropertiesComponent instance = getPropertiesComponent();
        instance.setValue(name, JSONUtil.toJsonStr(value));
    }
    static public <E> List<E> getListData(String name,Class<E> eClass){
        PropertiesComponent instance = getPropertiesComponent();
        String value = instance.getValue(name);
        List<E> list = JSONUtil.toList(value, eClass);
        return list;
    }

    static public <K,V> void setMapData(String name,Map<K,V> value){
        PropertiesComponent instance = getPropertiesComponent();
        instance.setValue(name, JSONUtil.toJsonStr(value));
    }
    static public <K,V> Map<K,V> getMapData(String name,Class<K> kClass,Class<V> vClass){
        PropertiesComponent instance = getPropertiesComponent();
        String value = instance.getValue(name);
        JSONObject bean = JSONUtil.toBean(value, JSONObject.class);
        HashMap<K, V> rs = new HashMap<>();

        bean.forEach((k,v)->{
            rs.put(BeanUtil.toBean(k,kClass),BeanUtil.toBean(v,vClass));
        });
        return rs;
    }
}
