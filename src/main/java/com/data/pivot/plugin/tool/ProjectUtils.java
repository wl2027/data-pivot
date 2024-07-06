package com.data.pivot.plugin.tool;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;

import java.awt.*;


public class ProjectUtils {

    public static Project getCurrProject() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
        if (openProjects.length == 0) {
            // 在未打开任何项目，进入到设置页面时会出现
            return projectManager.getDefaultProject();
        } else if (openProjects.length == 1) {
            // 只存在一个打开的项目则使用打开的项目
            return openProjects[0];
        }
        //如果有项目窗口处于激活状态
        try {
            WindowManager wm = WindowManager.getInstance();
            for (Project project : openProjects) {
                Window window = wm.suggestParentWindow(project);
                if (window != null && window.isActive()) {
                    return project;
                }
            }
        } catch (Exception ignored) {
        }
        //否则使用默认项目
        return projectManager.getDefaultProject();
    }

}
