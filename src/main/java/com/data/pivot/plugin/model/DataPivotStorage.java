package com.data.pivot.plugin.model;

import java.io.Serializable;

public interface DataPivotStorage<E> extends Serializable {
    E query();
    void save(E e);
}
