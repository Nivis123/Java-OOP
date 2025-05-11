package ru.inmemorydb.gui;

import ru.inmemorydb.core.*;
import ru.inmemorydb.util.DateUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class DatabaseGUI extends JFrame {
    private Database database;
    private JList<String> tableList;
    private DefaultListModel<String> tableListModel;
    private JTabbedPane tabbedPane;

    public DatabaseGUI(Database database) {
        this.database = database;
        initializeUI();
        loadTables();
    }

    private void initializeUI() {
        setTitle("In-Memory Database GUI");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableListModel = new DefaultListModel<>();
        tableList = new JList<>(tableListModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTable = tableList.getSelectedValue();
                if (selectedTable != null) {
                    showTable(selectedTable);
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(tableList);
        tableScrollPane.setPreferredSize(new Dimension(200, getHeight()));

        tabbedPane = new JTabbedPane();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                tableScrollPane,
                tabbedPane);
        splitPane.setDividerLocation(200);

        add(splitPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();

        JButton createTableBtn = new JButton("Создать таблицу");
        createTableBtn.addActionListener(e -> createTable());
        toolBar.add(createTableBtn);

        JButton dropTableBtn = new JButton("Удалить таблицу");
        dropTableBtn.addActionListener(e -> dropTable());
        toolBar.add(dropTableBtn);

        JButton addColumnBtn = new JButton("Добавить столбец");
        addColumnBtn.addActionListener(e -> addColumn());
        toolBar.add(addColumnBtn);

        JButton removeColumnBtn = new JButton("Удалить столбец");
        removeColumnBtn.addActionListener(e -> removeColumn());
        toolBar.add(removeColumnBtn);

        JButton insertDataBtn = new JButton("Добавить данные");
        insertDataBtn.addActionListener(e -> insertData());
        toolBar.add(insertDataBtn);

        add(toolBar, BorderLayout.NORTH);
    }

    private void loadTables() {
        tableListModel.clear();
        database.getTables().stream()
                .map(Table::getName)
                .forEach(tableListModel::addElement);
    }

    private void showTable(String tableName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tableName)) {
                tabbedPane.setSelectedIndex(i);
                return;
            }
        }

        Table table = database.getTable(tableName);
        if (table != null) {
            TableViewPanel tablePanel = new TableViewPanel(table);
            tabbedPane.addTab(tableName, tablePanel);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
    }

    private void createTable() {
        String tableName = JOptionPane.showInputDialog(this, "Введите имя таблицы:");
        if (tableName == null || tableName.trim().isEmpty()) return;

        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", DataType.INT, Constraint.UNIQUE));

        try {
            database.createTable(tableName, columns);
            tableListModel.addElement(tableName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка при создании таблицы: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropTable() {
        String selectedTable = tableList.getSelectedValue();
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this,
                    "Выберите таблицу для удаления",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить таблицу " + selectedTable + "?",
                "Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                database.dropTable(selectedTable);
                tableListModel.removeElement(selectedTable);

                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals(selectedTable)) {
                        tabbedPane.remove(i);
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при удалении таблицы: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addColumn() {
        String selectedTable = tableList.getSelectedValue();
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this,
                    "Выберите таблицу для добавления столбца",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ColumnDialog dialog = new ColumnDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                database.addColumn(selectedTable, dialog.getColumn());

                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals(selectedTable)) {
                        TableViewPanel panel = (TableViewPanel) tabbedPane.getComponentAt(i);
                        panel.refresh();
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при добавлении столбца: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeColumn() {
        String selectedTable = tableList.getSelectedValue();
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this,
                    "Выберите таблицу для удаления столбца",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Table table = database.getTable(selectedTable);
        List<String> columnNames = table.getColumns().stream()
                .map(Column::getName)
                .collect(Collectors.toList());

        String columnName = (String) JOptionPane.showInputDialog(this,
                "Выберите столбец для удаления:", "Удаление столбца",
                JOptionPane.QUESTION_MESSAGE, null,
                columnNames.toArray(), columnNames.get(0));

        if (columnName != null) {
            try {
                database.removeColumn(selectedTable, columnName);

                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals(selectedTable)) {
                        TableViewPanel panel = (TableViewPanel) tabbedPane.getComponentAt(i);
                        panel.refresh();
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при удалении столбца: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void insertData() {
        String selectedTable = tableList.getSelectedValue();
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this,
                    "Выберите таблицу для добавления данных",
                    "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Table table = database.getTable(selectedTable);
        List<Column> columns = table.getColumns();

        JPanel inputPanel = new JPanel(new GridLayout(columns.size(), 2));
        List<JTextField> fields = new ArrayList<>();

        for (Column column : columns) {
            inputPanel.add(new JLabel(column.getName() + " (" + column.getType() + "):"));
            JTextField field = new JTextField();
            inputPanel.add(field);
            fields.add(field);
        }

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Добавление данных в таблицу " + selectedTable,
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Row row = new Row();
                for (int i = 0; i < columns.size(); i++) {
                    String value = fields.get(i).getText().trim();
                    Object parsedValue = parseValue(value, columns.get(i).getType());
                    row.setValue(columns.get(i).getName(), parsedValue);
                }

                database.insertInto(selectedTable, Collections.singletonList(row));

                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getTitleAt(i).equals(selectedTable)) {
                        TableViewPanel tableViewPanel = (TableViewPanel) tabbedPane.getComponentAt(i);
                        tableViewPanel.refresh();
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при добавлении данных: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Object parseValue(String value, DataType type) {
        if (value.equalsIgnoreCase("null")) return null;

        try {
            switch (type) {
                case INT:
                    return Integer.parseInt(value);
                case STRING:
                    return value.startsWith("\"") && value.endsWith("\"") ?
                            value.substring(1, value.length() - 1) : value;
                case DATE:
                    return DateUtils.parseDate(value);
                case BOOLEAN:
                    return Boolean.parseBoolean(value);
                default:
                    return value;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат для типа " + type + ": " + value);
        }
    }
}