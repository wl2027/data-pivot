package com.data.pivot.plugin.view.setting;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.data.pivot.plugin.config.DataPivotInitializer;
import com.data.pivot.plugin.constants.DataPivotConstants;
import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.entity.DataPivotMappingSettingInfo;
import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.data.pivot.plugin.tool.MessageUtil;
import com.data.pivot.plugin.tool.ProjectUtils;
import com.data.pivot.plugin.view.DataPivotTableColumn;
import com.data.pivot.plugin.view.DataPivotTableView;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataPivotMappingSettingView implements Configurable {
    private final JPanel mainPanel;
    private DataPivotTableView<DataPivotMappingSettingInfo> tableComponent;

    public DataPivotMappingSettingView() {
        this.mainPanel = new JPanel(new BorderLayout());
    }
    private void initTable() {
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton jButton = new JButton();
        jButton.setText(DataPivotBundle.message("data.pivot.view.mapping.setting.refresh.title"));
        jButton.addActionListener((e)->{
            DataPivotInitializer.initDataPivotDatabaseInfo(ProjectUtils.getCurrProject());
            DataPivotInitializer.initDataPivotRelation(ProjectUtils.getCurrProject());
            MessageUtil.Dialog.info(DataPivotBundle.message("data.pivot.view.mapping.setting.refresh.content"));
        });
        jButton.setEnabled(true);
        jButton.setVisible(true);
        jPanel.add(jButton);
        //可能由于版本问题无法监听刷新,所以提供手动刷新
        this.mainPanel.add(jPanel,BorderLayout.NORTH);
        List<DataPivotMappingSettingInfo> dataPivotMappingSettingInfoList =  new ArrayList<>();
        dataPivotMappingSettingInfoList.addAll(DataPivotApplication.getInstance().CACHE.DP_MAPPING_SETTING_INFO_LIST_CACHE.get());
        this.tableComponent = new DataPivotTableView<>(
                ListUtil.of(
                        new DataPivotTableColumn<>(DataPivotBundle.message("data.pivot.dialog.setting.module"), DataPivotMappingSettingInfo::getModelName),
                        new DataPivotTableColumn<>(DataPivotBundle.message("data.pivot.dialog.setting.package"), DataPivotMappingSettingInfo::getPackageName),
                        new DataPivotTableColumn<>(DataPivotBundle.message("data.pivot.dialog.setting.database"), DataPivotMappingSettingInfo::getDatabasePath),
                        new DataPivotTableColumn<>(DataPivotBundle.message("data.pivot.dialog.setting.strategy"), DataPivotMappingSettingInfo::getStrategyCode)
                ),
                dataPivotMappingSettingInfoList,
                DataPivotMappingSettingInfoView::new,
                DataPivotMappingSettingInfo.class);
        this.tableComponent.setCheckAddRow((dataList,data)->{
            //允许一个数据库对应多个packageReference(取第一个),不允许一个packageReference对应多个db
            if (StrUtil.isEmpty(data.getModelName())) {
                MessageUtil.Dialog.info(DataPivotBundle.message("data.pivot.dialog.setting.module.null"));
                return false;
            }
            if (StrUtil.isEmpty(data.getPackageName())) {
                MessageUtil.Dialog.info(DataPivotBundle.message("data.pivot.dialog.setting.package.null"));
                return false;
            }
            if (StrUtil.isEmpty(data.getDatabasePath())) {
                MessageUtil.Dialog.info(DataPivotBundle.message("data.pivot.dialog.setting.database.null"));
                return false;
            }
            if (StrUtil.isEmpty(data.getStrategyCode())) {
                MessageUtil.Dialog.info(DataPivotBundle.message("data.pivot.dialog.setting.strategy.null"));
                return false;
            }
            String packageReference = data.getPackageReference();
            for (DataPivotMappingSettingInfo dataPivotMappingSettingInfo : dataList) {
                if (dataPivotMappingSettingInfo.getPackageReference().equals(packageReference)) {
                    MessageUtil.Dialog.info(DataPivotBundle.message("data.pivot.dialog.setting.repeat",packageReference));
                    return false;
                }
            }
            return true;
        });
        this.mainPanel.add(this.tableComponent.createPanel(), BorderLayout.CENTER);
    }

    private void initPanel() {
        // 初始化表格
        this.initTable();
    }

    @Override
    public String getDisplayName() {
        return DataPivotConstants.DATA_PIVOT_MAIN_SETTING;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getDisplayName();
    }

    public @Nullable JComponent createComponent() {
        this.initPanel();
        return mainPanel;
    }
    public boolean isModified() {
        return true;
    }

    public void apply() {
        new DataPivotMappingSettingInfo().save(this.tableComponent.getDataList());
        DataPivotApplication.getInstance().CACHE.DP_MAPPING_SETTING_INFO_LIST_CACHE.update(this.tableComponent.getDataList());
    }


}
