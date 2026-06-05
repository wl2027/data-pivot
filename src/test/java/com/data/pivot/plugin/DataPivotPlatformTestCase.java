package com.data.pivot.plugin;

import com.data.pivot.plugin.context.DataPivotApplication;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.testFramework.LoggedErrorProcessor;
import com.intellij.testFramework.ServiceContainerUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class DataPivotPlatformTestCase extends BasePlatformTestCase {
    private static final String GRID_PLUGIN_JAR_FILES_MESSAGE =
            "jarFiles is not set for PluginMainDescriptor(name=Data Editor Support, id=intellij.grid.plugin";
    @SuppressWarnings("unused")
    private static final AccessToken PLATFORM_LOG_FILTER = LoggedErrorProcessor.executeWith(new PlatformLoggedErrorProcessor());

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerDataPivotApplication(getProject());
        Project defaultProject = ProjectManager.getInstance().getDefaultProject();
        if (defaultProject != getProject()) {
            registerDataPivotApplication(defaultProject);
        }
    }

    private void registerDataPivotApplication(Project project) {
        ServiceContainerUtil.registerOrReplaceServiceInstance(
                project,
                DataPivotApplication.class,
                new DataPivotApplication(project),
                getTestRootDisposable()
        );
    }

    @Override
    protected @NotNull ThrowableRunnable<Throwable> wrapTestRunnable(@NotNull ThrowableRunnable<Throwable> testRunnable) {
        ThrowableRunnable<Throwable> wrappedRunnable = super.wrapTestRunnable(testRunnable);
        return () -> LoggedErrorProcessor.executeWith(new PlatformLoggedErrorProcessor(), wrappedRunnable);
    }

    private static class PlatformLoggedErrorProcessor extends LoggedErrorProcessor {
        @Override
        public @NotNull Set<Action> processError(
                @NotNull String category,
                @NotNull String message,
                String @NotNull [] details,
                Throwable t
        ) {
            if (message.startsWith(GRID_PLUGIN_JAR_FILES_MESSAGE)) {
                return Action.NONE;
            }
            return super.processError(category, message, details, t);
        }
    }
}
