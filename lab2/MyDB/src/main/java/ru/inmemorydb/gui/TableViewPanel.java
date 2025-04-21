package ru.inmemorydb.gui;

import ru.inmemorydb.core.Table;
import ru.inmemorydb.core.Column;
import ru.inmemorydb.core.Row;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TableViewPanel extends JPanel {
    private Table table;
    private JTable dataTable;
    private DefaultTableModel tableModel;

    public TableViewPanel(Table table) {
        this.table = table;
        initializeUI();
        refresh();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);

        add(new JScrollPane(dataTable), BorderLayout.CENTER);
    }

    public void refresh() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (Column column : table.getColumns()) {
            tableModel.addColumn(column.getName());
        }

        for (Row row : table.getRows()) {
            Object[] rowData = new Object[table.getColumns().size()];
            for (int i = 0; i < table.getColumns().size(); i++) {
                rowData[i] = row.getValue(table.getColumns().get(i).getName());
            }
            tableModel.addRow(rowData);
        }
    }
}