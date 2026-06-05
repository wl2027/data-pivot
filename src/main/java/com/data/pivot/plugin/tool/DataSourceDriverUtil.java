package com.data.pivot.plugin.tool;

import com.intellij.database.dataSource.DatabaseDriver;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.artifacts.DatabaseArtifactContext;
import com.intellij.database.dataSource.artifacts.DatabaseArtifactDefaultContext;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.util.ui.classpath.SimpleClasspathElement;

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
    private static final ConcurrentMap<String, Driver> REGISTERED_DRIVERS = new ConcurrentHashMap<>();

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
        REGISTERED_DRIVERS.computeIfAbsent(key, ignored -> registerDriver(dataSourceId, driverClassName, roots));
    }

    private static Driver registerDriver(String dataSourceId, String driverClassName, List<String> classRootUrls) {
        try {
            Driver driver = createDriver(driverClassName, classRootUrls);
            Driver shim = new DriverShim(driver);
            DriverManager.registerDriver(shim);
            return shim;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to register database driver for " + dataSourceId + ": " + driverClassName, e);
        }
    }

    private static Driver createDriver(String driverClassName, List<String> classRootUrls) throws Exception {
        if (classRootUrls.isEmpty()) {
            return (Driver) Class.forName(driverClassName).getDeclaredConstructor().newInstance();
        }

        List<URL> urls = new ArrayList<>();
        for (String rootUrl : classRootUrls) {
            urls.add(toUrl(rootUrl));
        }

        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), DataSourceDriverUtil.class.getClassLoader());
        return (Driver) Class.forName(driverClassName, true, classLoader).getDeclaredConstructor().newInstance();
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
