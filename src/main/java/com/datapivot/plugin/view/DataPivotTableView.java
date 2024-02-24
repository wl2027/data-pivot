package com.datapivot.plugin.view;

import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ReflectUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.EditableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DataPivotTableView<T> extends DefaultTableModel implements EditableModel {

    private List<DataPivotTableColumn<T>> tableColumns;

    /**
     * 表信息
     */
    private List<T> dataList;
    private BiFunction<List<T>,T,Boolean> checkAddRow;

    /**
     * 行编辑器
     */
    private Supplier<DataPivotTableRowView<T>> dataPivotTableRowViewSupplier;

    /**
     * 表格
     */
    private JBTable table;

    /**
     * 类型
     */
    private Class<T> tClass;

    public DataPivotTableView(List<DataPivotTableColumn<T>> tableColumns, List<T> dataList, Supplier<DataPivotTableRowView<T>> dataPivotTableRowViewSupplier, Class<T> tClass) {
        this.tClass=tClass;
        this.dataPivotTableRowViewSupplier=dataPivotTableRowViewSupplier;
        this.initTableColumns(tableColumns);
        this.initTable();
        this.initDataList(dataList);
    }
    public void removeAllRow() {
        int rowCount = getRowCount();
        for (int i = 0; i < rowCount; i++) {
            super.removeRow(0);
        }
    }
    private Vector<String> toRow(T data) {
        Vector<String> vector = new Vector<>();
        this.tableColumns.stream().map(tableColumn -> (String)ReflectUtil.getFieldValue(data,LambdaUtil.getFieldName(tableColumn.getFieldFun()))).forEach(vector::add);
        return vector;
    }

    private void initTableColumns(List<DataPivotTableColumn<T>> tableColumns) {
        this.tableColumns = tableColumns;
        for (DataPivotTableColumn<T> tableColumn : tableColumns) {
            addColumn(tableColumn.getName());
        }
    }
    private void initTable() {
        this.table = new JBTable(this);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    private void initDataList(List<T> dataList) {
        this.dataList = dataList;
        // 清空数据
        removeAllRow();
        for (T data : this.dataList) {
            addRowData(data);
        }
    }

    private void addRowData(T dataData) {
        addRow(toRow(dataData));
    }


    public JComponent createPanel() {
        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.table);
        return decorator.createPanel();
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (row < this.dataList.size()) {
            super.setValueAt(value, row, column);
            T obj = this.dataList.get(row);
            ReflectUtil.setFieldValue(obj, LambdaUtil.getFieldName(tableColumns.get(column).getFieldFun()),value);
        }
    }
    @Override
    public void removeRow(int row) {
        super.removeRow(row);
        this.dataList.remove(row);
    }
    @Override
    public void addRow() {
        T entity = ReflectUtil.newInstance(tClass);
        DataPivotTableRowView<T> dataPivotTableRowView = dataPivotTableRowViewSupplier.get();
        if (dataPivotTableRowView.showAndGet()) {
            //entity = dataPivotTableRowView.getValue();
            entity = (T) dataPivotTableRowView.getValue();
        }
        if (checkAddRow==null||(checkAddRow!=null&&checkAddRow.apply(dataList,entity))) {
            this.dataList.add(entity);
            addRowData(entity);
        }
    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        super.moveRow(oldIndex, oldIndex, newIndex);
        T remove = this.dataList.remove(oldIndex);
        this.dataList.add(newIndex, remove);
    }

    @Override
    public boolean canExchangeRows(int oldIndex, int newIndex) {
        return true;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
    public JBTable getTable() {
        return table;
    }

    public void setTable(JBTable table) {
        this.table = table;
    }

    public Class<T> gettClass() {
        return tClass;
    }

    public void settClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    public BiFunction<List<T>, T, Boolean> getCheckAddRow() {
        return checkAddRow;
    }

    public void setCheckAddRow(BiFunction<List<T>, T, Boolean> checkAddRow) {
        this.checkAddRow = checkAddRow;
    }
}
