package com.datapivot.plugin.i18n;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public class DataPivotBundle extends DynamicBundle {

    private static final String BUNDLE = "messages.DataPivotBundle";
    private static final DataPivotBundle INSTANCE = new DataPivotBundle();

    public DataPivotBundle() {
        super(BUNDLE);
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        if (key == null) return "";
        if (params == null) return "";
        if (INSTANCE.getMessage(key, params) == null) return "";
        return INSTANCE.getMessage(key, params);
    }

    public static Supplier<String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        if (key == null) return null;
        if (params == null) return null;
        if (INSTANCE.getLazyMessage(key, params) == null) return null;
        return INSTANCE.getLazyMessage(key, params);
    }
}
