package com.data.pivot.plugin.view.query;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.data.pivot.plugin.entity.DatabaseQueryConfig;
import com.data.pivot.plugin.tool.QueryTool;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.IconManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class QueryTableComponent extends JDialog implements Disposable {
    private final Icon icon = IconManager.getInstance().getIcon("/icons/query.svg", QueryTableComponent.class);

    private DatabaseQueryConfig databaseQueryConfig; // 查询配置
    private final JBTable queryResultTable; // 显示查询结果的表格
    private final ResultTableModel resultTableModel; // 表格模型
    private List<QueryTableRow> queryTableRows; // 所有查询结果的数据行
    private JTextField localSearchTextField; // 本地搜索输入框
    private JTextField remoteSearchTextField; // 远程搜索输入框
    private Timer remoteDebounceTimer; // 远程搜索防抖动计时器
    private Timer localDebounceTimer; // 本地搜索防抖动计时器
    private QueryTableRow selectedQueryTableRow; // 选中的行数据

    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0); // 高亮颜色
    private static final int DEFAULT_ROW_HEIGHT = 20; // 默认行高
    private static final String LOCAL_SEARCH_PLACEHOLDER = "本地搜索";
    private static final String REMOTE_SEARCH_PLACEHOLDER = "远程搜索";

    private List<Pair<Integer, Integer>> highlightedCells = new ArrayList<>();

    public QueryTableComponent(Project project, DatabaseQueryConfig databaseQueryConfig, @NotNull List<Map<String, Object>> queryResults) {
        super((Dialog) null, false); // 不影响其他操作
        toFront(); // 设置为悬浮在最前面
        this.databaseQueryConfig = databaseQueryConfig;
        setTitle("Data-Pivot Query: 映射数据表: " + databaseQueryConfig.getTableName() + ", 条件字段: " + databaseQueryConfig.getConditionField());
        setSize(new Dimension(850, 450));
        setLocationRelativeTo(null);
        Set<String> columnNamesSet = queryResults.get(0).keySet();
        String[] columnNames = columnNamesSet.toArray(new String[0]);
        queryTableRows = queryResults.stream()
                .map(result -> new QueryTableRow(result, columnNamesSet))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        resultTableModel = new ResultTableModel(queryTableRows, columnNames);
        queryResultTable = new JBTable(resultTableModel);
        queryResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queryResultTable.setFillsViewportHeight(true);
        queryResultTable.setRowHeight(DEFAULT_ROW_HEIGHT); // 设置每行的高度
        queryResultTable.setPreferredScrollableViewportSize(new Dimension(queryResultTable.getPreferredSize().width, queryResultTable.getRowHeight() * 20)); // 设置表格高度为20行

        // 允许双击复制表格内容
        queryResultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = queryResultTable.getSelectedRow();
                    if (selectedRow != -1) {
                        selectedQueryTableRow = resultTableModel.getQueryTableRow(selectedRow);
                        handleRowSelection(selectedQueryTableRow);
                    }
                }
            }
        });

        // 设置表格默认宽度并支持横向滚动
        queryResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < queryResultTable.getColumnModel().getColumnCount(); i++) {
            queryResultTable.getColumnModel().getColumn(i).setPreferredWidth(100); // 默认宽度
        }

        // 设置单元格渲染器
        queryResultTable.setDefaultRenderer(Object.class, new HighlightRenderer());
        initComponents();
    }

    private void initComponents() {
        localSearchTextField = new JTextField();
        remoteSearchTextField = new JTextField();
        localSearchTextField.setToolTipText("本地搜索: 搜索表格数据");
        remoteSearchTextField.setToolTipText("远程搜索: 搜索数据库数据");
        addSearchFieldListeners(localSearchTextField, LOCAL_SEARCH_PLACEHOLDER);
        addSearchFieldListeners(remoteSearchTextField, REMOTE_SEARCH_PLACEHOLDER);
        localSearchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleLocalSearchInputWithDebounce();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleLocalSearchInputWithDebounce();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleLocalSearchInputWithDebounce();
            }
        });

        remoteSearchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleRemoteSearchInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleRemoteSearchInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleRemoteSearchInput();
            }
        });

        JPanel searchPanel = new JPanel(new GridLayout(2, 1));
        searchPanel.add(remoteSearchTextField);
        searchPanel.add(localSearchTextField);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(new JBScrollPane(queryResultTable), BorderLayout.CENTER);

        getContentPane().add(mainPanel);
    }

    private void addSearchFieldListeners(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(JBColor.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(JBColor.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                clearHighlights();
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(JBColor.GRAY);
                }
            }
        });
    }

    public static @Nullable QueryTableComponent getInstance(Project project, DatabaseQueryConfig databaseQueryConfig) {
        List<Map<String, Object>> queryResults = QueryTool.query(databaseQueryConfig);
        if (queryResults == null) {
            return null;
        }
        if (queryResults.isEmpty()){
            Messages.showMessageDialog("查询数据为空", "Query Data Result",null);
            return null;
        }
        return new QueryTableComponent(project, databaseQueryConfig, queryResults);
    }

    private void handleLocalSearchInputWithDebounce() {
        if (localDebounceTimer != null) {
            localDebounceTimer.cancel();
        }
        localDebounceTimer = new Timer();
        localDebounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(QueryTableComponent.this::handleLocalSearchInput);
            }
        }, 300); // 300毫秒延迟
    }

    private void handleLocalSearchInput() {
        String searchFieldText = localSearchTextField.getText();
        if (StrUtil.isEmpty(searchFieldText) || LOCAL_SEARCH_PLACEHOLDER.equals(searchFieldText)) {
            clearHighlights();
            return;
        }
        String query = localSearchTextField.getText();
        clearHighlights();
        highlightedCells.clear();
        // 定位到匹配的单元格并高亮
        for (int col = 0; col < queryResultTable.getColumnCount(); col++) {
            for (int row = 0; row < queryResultTable.getRowCount(); row++) {
                Object value = queryResultTable.getValueAt(row, col);
                String valueStr = String.valueOf(value);
                if (valueStr.contains(query)) {
                    highlightedCells.add(new Pair<>(row, col));
                }
            }
        }
        queryResultTable.repaint();
    }

    private void handleRemoteSearchInput() {
        if (REMOTE_SEARCH_PLACEHOLDER.equals(remoteSearchTextField.getText())) {
            return;
        }
        if (remoteDebounceTimer != null) {
            remoteDebounceTimer.cancel();
        }
        remoteDebounceTimer = new Timer();
        remoteDebounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateTable(remoteSearchTextField.getText()));
            }
        }, 300); // 300毫秒延迟
    }

    private void updateTable(String query) {
        if (StrUtil.isEmpty(query)) {
            databaseQueryConfig.setLikeValue(null);
        } else {
            databaseQueryConfig.setLikeValue(query);
        }
        List<Map<String, Object>> updatedResults = QueryTool.query(databaseQueryConfig);
        if (updatedResults != null && !updatedResults.isEmpty()) {
            Set<String> columnNamesSet = updatedResults.get(0).keySet();
            String[] columnNames = columnNamesSet.toArray(new String[0]);
            List<QueryTableRow> filteredItems = updatedResults.stream()
                    .map(result -> new QueryTableRow(result, columnNamesSet))
                    .collect(Collectors.toList());
            resultTableModel.updateData(filteredItems, columnNames);
        } else {
            resultTableModel.updateData(List.of(), new String[0]);
        }
    }

    private void handleRowSelection(QueryTableRow rowData) {
        String jsonStr = JSONUtil.toJsonStr(rowData.getData());
        copyToClipboard(jsonStr);
    }

    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        Messages.showInfoMessage("已复制到剪贴板", "信息");
    }

    private static class ResultTableModel extends AbstractTableModel {
        private List<QueryTableRow> data;
        private String[] columnNames;

        public ResultTableModel(List<QueryTableRow> data, String[] columnNames) {
            this.data = data;
            this.columnNames = columnNames;
        }

        public void updateData(List<QueryTableRow> newData, String[] columnNames) {
            this.data = newData;
            this.columnNames = columnNames;
            fireTableDataChanged();
        }

        public QueryTableRow getQueryTableRow(int rowIndex) {
            return data.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            QueryTableRow rowData = data.get(rowIndex);
            String columnName = columnNames[columnIndex];
            return rowData.getData().get(columnName);
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    private static class QueryTableRow {
        private final Map<String, Object> data;
        private final Set<String> columns;

        public QueryTableRow(Map<String, Object> data, Set<String> columns) {
            this.data = data;
            this.columns = columns;
        }

        public Map<String, Object> getData() {
            return data;
        }
    }

    private class HighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setBackground(isHighlighted(row, column) ? HIGHLIGHT_COLOR : table.getBackground());
            return cell;
        }

        private boolean isHighlighted(int row, int column) {
            for (Pair<Integer, Integer> cell : highlightedCells) {
                if (cell.getFirst() == row && cell.getSecond() == column) {
                    return true;
                } else if (cell.getFirst() == -1 && cell.getSecond() == column) {
                    return true;
                }
            }
            return false;
        }
    }

    private void clearHighlights() {
        highlightedCells.clear();
        queryResultTable.repaint();
    }

    @Override
    public void dispose() {
        // 清理资源
        if (remoteDebounceTimer != null) {
            remoteDebounceTimer.cancel();
        }
        if (localDebounceTimer != null) {
            localDebounceTimer.cancel();
        }
    }
}
