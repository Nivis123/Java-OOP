package ru.inmemorydb.gui;

import ru.inmemorydb.core.Column;
import ru.inmemorydb.core.DataType;
import ru.inmemorydb.core.Constraint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ColumnDialog extends JDialog {
    private boolean confirmed = false;
    private Column column;

    private JTextField nameField;
    private JComboBox<DataType> typeCombo;
    private JComboBox<Constraint> constraintCombo;

    public ColumnDialog(JFrame parent) {
        super(parent, "Добавить столбец", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(300, 200);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Имя столбца:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Тип данных:"));
        typeCombo = new JComboBox<>(DataType.values());
        add(typeCombo);

        add(new JLabel("Ограничение:"));
        constraintCombo = new JComboBox<>(Constraint.values());
        add(constraintCombo);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            confirmed = true;
            column = new Column(
                    nameField.getText(),
                    (DataType) typeCombo.getSelectedItem(),
                    (Constraint) constraintCombo.getSelectedItem());
            dispose();
        });
        add(okButton);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Column getColumn() {
        return column;
    }
}