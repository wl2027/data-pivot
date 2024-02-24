package com.datapivot.plugin.tool;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;

import java.util.ArrayList;
import java.util.List;

public final class ModuleUtils {

    public static List<Module> getModuleList(){
        List<Module> moduleList = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(ProjectUtils.getCurrProject()).getModules()) {
           moduleList.add(module);
        }
        return moduleList;
    }
}
