package com.data.pivot.plugin.tool;

import com.data.pivot.plugin.i18n.DataPivotBundle;
import com.intellij.database.dataSource.DatabaseDriver;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.artifacts.DatabaseArtifactContext;
import com.intellij.database.dataSource.artifacts.DatabaseArtifactDefaultContext;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.util.ui.classpath.SimpleClasspathElement;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class DataSourceDriverUtil {
    private static final ConcurrentMap<String, RegisteredDriver> REGISTERED_DRIVERS = new ConcurrentHashMap<>();

    private DataSourceDriverUtil() {
    }

    public static List<String> getDriverClassRootUrls(LocalDataSource dataSource) {
        if (dataSource == null || dataSource.getDatabaseDriver() == null) {
            return Collections.emptyList();
        }

        DatabaseDriver driver = dataSource.getDatabaseDriver();
        List<String> roots = new ArrayList<>();
        addClassRootUrls(roots, driver.getAdditionalClasspathElements());

        try {
            DatabaseArtifactContext context = new DatabaseArtifactDefaultContext().createContextForDataSource(dataSource);
            for (DatabaseDriver.ArtifactRef artifact : driver.getArtifacts()) {
                if (artifact.hasElements(context)) {
                    addClassRootUrls(roots, artifact.getElements(context));
                }
            }
        } catch (Exception ignored) {
            // Database Tools may not have downloaded a driver artifact yet; callers will show a connection error.
        }

        return roots.stream()
                .filter(root -> root != null && !root.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public static void ensureDriverRegistered(String dataSourceId, String driverClassName, List<String> classRootUrls) {
        if (driverClassName == null || driverClassName.isBlank()) {
            return;
        }

        List<String> roots = classRootUrls == null ? Collections.emptyList() : classRootUrls;
        String key = driverClassName + "@" + String.join("|", roots);
        REGISTERED_DRIVERS.computeIfAbsent(key, ignored -> registerDriver(driverClassName, roots));
    }

    private static RegisteredDriver registerDriver(String driverClassName, List<String> classRootUrls) {
        URLClassLoader classLoader = null;
        try {
            Driver driver;
            if (classRootUrls.isEmpty()) {
                driver = (Driver) Class.forName(driverClassName).getDeclaredConstructor().newInstance();
            } else {
                classLoader = createClassLoader(classRootUrls);
                driver = (Driver) Class.forName(driverClassName, true, classLoader).getDeclaredConstructor().newInstance();
            }
            Driver shim = new DriverShim(driver);
            DriverManager.registerDriver(shim);
            return new RegisteredDriver(shim, classLoader);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // 驱动 jar 尚未下载到本地(或 jar 内不含该驱动类),引导用户先在 DataGrip 数据源里下载该驱动。
            closeQuietly(classLoader);
            throw new IllegalStateException(
                    DataPivotBundle.message("data.pivot.driver.not.downloaded", driverClassName), e);
        } catch (Exception e) {
            closeQuietly(classLoader);
            throw new IllegalStateException(
                    DataPivotBundle.message("data.pivot.driver.register.fail", driverClassName, String.valueOf(e.getMessage())), e);
        }
    }

    private static URLClassLoader createClassLoader(List<String> classRootUrls) throws Exception {
        List<URL> urls = new ArrayList<>();
        for (String rootUrl : classRootUrls) {
            urls.add(toUrl(rootUrl));
        }
        return new URLClassLoader(urls.toArray(new URL[0]), DataSourceDriverUtil.class.getClassLoader());
    }

    /**
     * 释放本工具注册过的驱动 shim 及其 {@link URLClassLoader}。应在项目/插件 dispose 时调用,
     * 避免 DriverManager 中的 shim 与类加载器长期驻留;下次查询会按需重新注册。
     */
    public static void deregisterAllDrivers() {
        for (RegisteredDriver registered : REGISTERED_DRIVERS.values()) {
            try {
                DriverManager.deregisterDriver(registered.shim());
            } catch (SQLException ignored) {
                // best-effort cleanup on dispose
            }
            closeQuietly(registered.classLoader());
        }
        REGISTERED_DRIVERS.clear();
    }

    private static void closeQuietly(URLClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }
        try {
            classLoader.close();
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }

    private record RegisteredDriver(Driver shim, URLClassLoader classLoader) {
    }

    private static URL toUrl(String rootUrl) throws Exception {
        String path = rootUrl;
        if (path.startsWith("jar://")) {
            path = path.substring("jar://".length());
        } else if (path.startsWith("jar:file://")) {
            path = path.substring("jar:file://".length());
        } else if (path.startsWith("file://")) {
            path = VfsUtilCore.urlToPath(path);
        }

        int jarSeparator = path.indexOf("!/");
        if (jarSeparator >= 0) {
            path = path.substring(0, jarSeparator);
        }

        return Path.of(path).toUri().toURL();
    }

    private static void addClassRootUrls(List<String> roots, List<? extends SimpleClasspathElement> elements) {
        if (elements == null) {
            return;
        }
        for (SimpleClasspathElement element : elements) {
            roots.addAll(element.getClassesRootUrls());
        }
    }

    private static final class DriverShim implements Driver {
        private final Driver delegate;

        private DriverShim(Driver delegate) {
            this.delegate = delegate;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return delegate.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return delegate.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return delegate.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return delegate.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return delegate.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return delegate.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return delegate.getParentLogger();
        }
    }
}
