package ru.inmemorydb.command;

import ru.inmemorydb.core.*;
import java.util.*;
import java.util.regex.*;
import java.util.function.Predicate;
import ru.inmemorydb.util.DateUtils;

public class CommandParser {
    public static Command parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty command");
        }

        String cmd = command.trim().toLowerCase();
        if (cmd.startsWith("create table")) {
            return parseCreateTable(command);
        } else if (cmd.startsWith("insert into")) {
            return parseInsert(command);
        } else if (cmd.startsWith("drop table")) {
            return parseDropTable(command);
        } else if (cmd.startsWith("delete from")) {
            return parseDelete(command);
        } else {
            throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static CreateTableCommand parseCreateTable(String command) {
        Pattern pattern = Pattern.compile(
                "(?i)CREATE TABLE\\s+(\\w+)\\s*\\((.+)\\)",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid CREATE TABLE syntax");
        }

        String tableName = matcher.group(1);
        String columnsDef = matcher.group(2).trim();

        String[] columnParts = columnsDef.split(";");
        List<Column> columns = new ArrayList<>();

        for (String part : columnParts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            Pattern colPattern = Pattern.compile(
                    "(\\w+)\\s+(\\w+)(?:\\s+(unique|not-null))?");
            Matcher colMatcher = colPattern.matcher(part);
            if (!colMatcher.find()) {
                throw new IllegalArgumentException("Invalid column definition: " + part);
            }

            String colName = colMatcher.group(1);
            DataType colType = DataType.fromString(colMatcher.group(2));
            Constraint constraint = Constraint.fromString(colMatcher.group(3));

            columns.add(new Column(colName, colType, constraint));
        }

        return new CreateTableCommand(tableName, columns);
    }

    private static InsertCommand parseInsert(String command) {
        Pattern pattern = Pattern.compile(
                "(?i)INSERT INTO\\s+(\\w+)\\s*\\((.+)\\)",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid INSERT syntax");
        }

        String tableName = matcher.group(1);
        String valuesDef = matcher.group(2).trim();

        List<Row> rows = new ArrayList<>();

        if (valuesDef.startsWith("(") && valuesDef.endsWith(")")) {
            valuesDef = valuesDef.substring(1, valuesDef.length() - 1);
        }

        String[] tuples = valuesDef.split("\\),\\s*\\(");

        for (String tuple : tuples) {
            tuple = tuple.trim();
            if (tuple.startsWith("(")) tuple = tuple.substring(1);
            if (tuple.endsWith(")")) tuple = tuple.substring(0, tuple.length() - 1);

            String[] values = tuple.split(",\\s*");
            Row row = new Row();

            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                Object parsedValue = parseValue(value);
                row.setValue("column" + (i + 1), parsedValue);
            }

            rows.add(row);
        }

        return new InsertCommand(tableName, rows);
    }

    private static Object parseValue(String value) {
        if (value.equalsIgnoreCase("null")) return null;
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        if (value.matches("\\d+")) {
            return Integer.parseInt(value);
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        try {
            return DateUtils.parseDate(value);
        } catch (Exception e) {
        }
        return value;
    }

    private static DropTableCommand parseDropTable(String command) {
        Pattern pattern = Pattern.compile("(?i)DROP TABLE\\s+(\\w+)");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid DROP TABLE syntax");
        }
        return new DropTableCommand(matcher.group(1));
    }

    private static DeleteCommand parseDelete(String command) {
        Pattern pattern = Pattern.compile(
                "(?i)DELETE FROM\\s+(\\w+)(?:\\s+WHERE\\s+(.+))?");
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid DELETE syntax");
        }

        String tableName = matcher.group(1);
        String conditionStr = matcher.group(2);

        Predicate<Row> condition = row -> true;

        if (conditionStr != null && !conditionStr.trim().isEmpty()) {
            condition = parseCondition(conditionStr);
        }

        return new DeleteCommand(tableName, condition);
    }

    private static Predicate<Row> parseCondition(String conditionStr) {
        String[] parts = conditionStr.split("(?i)\\s+AND\\s+");
        List<Predicate<Row>> predicates = new ArrayList<>();

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            Pattern condPattern = Pattern.compile("(\\w+)\\s*=\\s*(.+)");
            Matcher condMatcher = condPattern.matcher(part);
            if (!condMatcher.find()) {
                throw new IllegalArgumentException("Invalid condition: " + part);
            }

            String columnName = condMatcher.group(1);
            String valueStr = condMatcher.group(2);
            Object value = parseValue(valueStr);

            predicates.add(row -> {
                Object rowValue = row.getValue(columnName);
                if (rowValue == null) return value == null;
                return rowValue.equals(value);
            });
        }

        return row -> predicates.stream().allMatch(p -> p.test(row));
    }
}