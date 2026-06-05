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
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
        this.contentPane = buildContentPane();
        this.initPanel();
        this.initEvent();
        super.init();
        setTitle(DataPivotBundle.message("data.pivot.view.mapping.setting.info.title"));
    }

    private JPanel buildContentPane() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.moduleComboBox = new JComboBox<>();
        this.databaseComboBox = new JComboBox<>();
        this.packageField = new JTextField();
        this.packageChooseButton = new JButton("choose");
        this.typeComboBox = new JComboBox();

        addRow(panel, 0, "module", moduleComboBox, null);
        addRow(panel, 1, "package", packageField, packageChooseButton);
        addRow(panel, 2, "database", databaseComboBox, null);
        addRow(panel, 3, "type", typeComboBox, null);
        return panel;
    }

    private static void addRow(JPanel panel, int row, String labelText, JComponent field, JButton button) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(4, 0, 4, 8);
        panel.add(new JLabel(labelText), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(4, 0, 4, button == null ? 0 : 8);
        panel.add(field, fieldConstraints);

        if (button != null) {
            GridBagConstraints buttonConstraints = new GridBagConstraints();
            buttonConstraints.gridx = 2;
            buttonConstraints.gridy = row;
            buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
            buttonConstraints.insets = new Insets(4, 0, 4, 0);
            panel.add(button, buttonConstraints);
        }
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
        packageChooseButton.addActionListener(e -> {
            PackageChooserDialog dialog = new PackageChooserDialog("Package Chooser", project);
            dialog.show();
            PsiPackage psiPackage = dialog.getSelectedPackage();
            if (psiPackage != null) {
                packageField.setText(psiPackage.getQualifiedName());
            }
        });
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
