package com.data.pivot.plugin.config;

import com.data.pivot.plugin.DataPivotPlatformTestCase;
import com.data.pivot.plugin.context.DataPivotApplication;
import com.data.pivot.plugin.enums.DefaultStrategyType;
import com.intellij.testFramework.JUnit38AssumeSupportRunner;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@RunWith(JUnit38AssumeSupportRunner.class)
public class DataPivotStartupIntegrationTest extends DataPivotPlatformTestCase {
    public void testProjectServiceCanBeResolvedInPlatformProject() {
        DataPivotApplication application = DataPivotApplication.getInstance(getProject());

        assertNotNull(application);
        assertSame(getProject(), application.getProject());
    }

    public void testDefaultStrategiesAreInitializedForProjectApplication() {
        DataPivotApplication application = DataPivotApplication.getInstance(getProject());
        application.MAPPER.DEFAULT_STRATEGY_MAPPER.clear();

        DataPivotInitializer.initDefaultStrategy(application);

        assertTrue(application.MAPPER.DEFAULT_STRATEGY_MAPPER.containsKey(DefaultStrategyType.JPAAnnotation.getCode()));
        assertTrue(application.MAPPER.DEFAULT_STRATEGY_MAPPER.containsKey(DefaultStrategyType.MPAnnotation.getCode()));
        assertTrue(application.MAPPER.DEFAULT_STRATEGY_MAPPER.containsKey(DefaultStrategyType.HUMP_UNDERLINE.getCode()));
    }

    public void testPluginDescriptorDeclaresDatabaseAndStartupContracts() throws Exception {
        Document pluginXml = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File("src/main/resources/META-INF/plugin.xml"));

        assertElementText(pluginXml, "depends", "com.intellij.database");
        assertAttribute(pluginXml, "postStartupActivity", "implementation",
                "com.data.pivot.plugin.config.DataPivotInitializer");
        assertAttribute(pluginXml, "projectService", "serviceImplementation",
                "com.data.pivot.plugin.context.DataPivotApplication");
    }

    private static void assertElementText(Document document, String tagName, String expectedText) {
        NodeList elements = document.getElementsByTagName(tagName);
        for (int i = 0; i < elements.getLength(); i++) {
            if (expectedText.equals(elements.item(i).getTextContent().trim())) {
                return;
            }
        }
        fail("Missing <" + tagName + "> text: " + expectedText);
    }

    private static void assertAttribute(Document document, String tagName, String attributeName, String expectedValue) {
        NodeList elements = document.getElementsByTagName(tagName);
        for (int i = 0; i < elements.getLength(); i++) {
            if (expectedValue.equals(elements.item(i).getAttributes().getNamedItem(attributeName).getNodeValue())) {
                return;
            }
        }
        fail("Missing <" + tagName + "> " + attributeName + ": " + expectedValue);
    }
}
