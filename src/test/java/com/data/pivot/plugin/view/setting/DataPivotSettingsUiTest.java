package com.data.pivot.plugin.view.setting;

import com.data.pivot.plugin.DataPivotPlatformTestCase;
import com.intellij.testFramework.JUnit38AssumeSupportRunner;
import org.junit.runner.RunWith;

import javax.swing.JComponent;

@RunWith(JUnit38AssumeSupportRunner.class)
public class DataPivotSettingsUiTest extends DataPivotPlatformTestCase {
    public void testMappingSettingConfigurableCreatesMainComponent() {
        DataPivotMappingSettingView view = new DataPivotMappingSettingView();

        JComponent component = view.createComponent();

        assertNotNull(component);
        assertEquals("Data-Pivot Configuration", view.getDisplayName());
    }

    public void testMappingSettingInfoDialogCreatesCenterPanel() {
        DataPivotMappingSettingInfoView view = new DataPivotMappingSettingInfoView();

        JComponent component = view.createCenterPanel();

        assertNotNull(component);
        assertTrue(component.getComponentCount() > 0);
    }
}
