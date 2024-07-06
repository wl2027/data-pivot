package com.data.pivot.plugin.view.setting;

import cn.hutool.core.util.StrUtil;
import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.entity.DataPivotDatabaseInfo;
import com.data.pivot.plugin.entity.DataPivotMappingSettingInfo;
import com.data.pivot.plugin.enums.DefaultStrategyType;
import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.data.pivot.plugin.tool.DataPivotUtil;
import com.data.pivot.plugin.tool.ProjectUtils;
import com.data.pivot.plugin.view.DataPivotTableRowView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.ExceptionUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DataPivotMappingSettingInfoView  extends DataPivotTableRowView<DataPivotMappingSettingInfo> {

    /**
     * 主面板
     */
    private JPanel contentPane;
    /**
     * 模型下拉框
     */
    private JComboBox<String> moduleComboBox;
    /**
     * 模型下拉框
     */
    private JComboBox<String> databaseComboBox;
    /**
     * 包字段
     */
    private JTextField packageField;
    /**
     * 包选择按钮
     */
    private JButton packageChooseButton;
    private JComboBox typeComboBox;
    /**
     * 项目对象
     */
    private Project project;
    /**
     * 当前项目中的module
     */
    private List<Module> moduleList;
    private List<DataPivotDatabaseInfo> databaseList;
    private List<DefaultStrategyType> typeList;


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.contentPane;
    }

    /**
     * 构造方法
     */
    public DataPivotMappingSettingInfoView() {
        super(ProjectUtils.getCurrProject());
        this.project = ProjectUtils.getCurrProject();
        this.initPanel();
        this.initEvent();
        super.init();
        setTitle(DataPivotBundle.message("data.pivot.view.mapping.setting.info.title"));
    }

    public void initDatabaseComponent(){
        this.databaseList = DataPivotApplication.getInstance().CACHE.DP_DB_INFO_LIST_CACHE.get().stream().collect(Collectors.toList());
        databaseComboBox.removeAllItems();
        for (DataPivotDatabaseInfo dataPivotDatabaseInfo : databaseList) {
            databaseComboBox.addItem(dataPivotDatabaseInfo.getDatabasePath());
        }
    }

    private void initEvent() {
        //监听module选择事件
        databaseComboBox.addActionListener(e -> {
            // 刷新路径
            //refreshItem();
        });
        try {
            Class<?> cls = Class.forName("com.intellij.ide.util.PackageChooserDialog");
            //添加包选择事件
            packageChooseButton.addActionListener(e -> {
                try {
                    Constructor<?> constructor = cls.getConstructor(String.class, Project.class);
                    Object dialog = constructor.newInstance("Package Chooser", project);
                    // 显示窗口
                    Method showMethod = cls.getMethod("show");
                    showMethod.invoke(dialog);
                    // 获取选中的包名
                    Method getSelectedPackageMethod = cls.getMethod("getSelectedPackage");
                    Object psiPackage = getSelectedPackageMethod.invoke(dialog);
                    if (psiPackage != null) {
                        Method getQualifiedNameMethod = psiPackage.getClass().getMethod("getQualifiedName");
                        String packageName = (String) getQualifiedNameMethod.invoke(psiPackage);
                        packageField.setText(packageName);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
                    ExceptionUtil.rethrow(e1);
                }
            });
        } catch (ClassNotFoundException e) {
            // 没有PackageChooserDialog，并非支持Java的IDE，禁用相关UI组件
            packageField.setEnabled(false);
            packageChooseButton.setEnabled(false);
        }
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    public DataPivotMappingSettingInfo getValue() {
        DataPivotMappingSettingInfo dataPivotMappingSettingInfo = new DataPivotMappingSettingInfo();
        Module selectModule = getSelectModule();
        dataPivotMappingSettingInfo.setModelName(selectModule.getName());
        dataPivotMappingSettingInfo.setPackageName(packageField.getText());
        DataPivotDatabaseInfo selectDatabase = getSelectDatabase();
        dataPivotMappingSettingInfo.setDataSourceName(selectDatabase.getDataSourceName());
        dataPivotMappingSettingInfo.setDatabaseName(selectDatabase.getDatabaseName());
        dataPivotMappingSettingInfo.setDatabasePath(selectDatabase.getDatabasePath());
        dataPivotMappingSettingInfo.setStrategyCode(getSelectType().getCode());
        dataPivotMappingSettingInfo.setDatabaseReference(selectDatabase.getDatabaseReference());
        dataPivotMappingSettingInfo.setPackageReference(DataPivotUtil.createPackageReference(selectModule.getName(),packageField.getText()));
        return dataPivotMappingSettingInfo;
    }

    /**
     * 初始化方法
     */
    private void initPanel() {
        // 初始化module，存在资源路径的排前面
        this.moduleList = new LinkedList<>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            this.moduleList.add(module);
        }
        //初始化Module选择
        for (Module module : this.moduleList) {
            moduleComboBox.addItem(module.getName());
        }
        initDatabaseComponent();
        this.typeList = List.of(DefaultStrategyType.values());
        for (DefaultStrategyType defaultStrategyType : typeList) {
            typeComboBox.addItem(defaultStrategyType.getCode());
        }
    }

    /**
     * 获取选中的Module
     *
     * @return 选中的Module
     */
    private Module getSelectModule() {
        String name = (String) moduleComboBox.getSelectedItem();
        if (StrUtil.isEmpty(name)) {
            return null;
        }
        return ModuleManager.getInstance(project).findModuleByName(name);
    }
    private DataPivotDatabaseInfo getSelectDatabase() {
        String name = (String) databaseComboBox.getSelectedItem();
        if (StrUtil.isEmpty(name)) {
            return null;
        }
        List<DataPivotDatabaseInfo> collect = databaseList.stream().filter(bean -> bean.getDatabasePath().equals(name)).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(0);
    }
    private DefaultStrategyType getSelectType() {
        String name = (String) typeComboBox.getSelectedItem();
        if (StrUtil.isEmpty(name)) {
            return null;
        }
        List<DefaultStrategyType> collect = typeList.stream().filter(bean -> bean.getCode().equals(name)).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(0);
    }

}
