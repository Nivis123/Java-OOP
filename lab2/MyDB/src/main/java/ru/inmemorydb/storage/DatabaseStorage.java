package ru.inmemorydb.storage;

import ru.inmemorydb.core.Database;
import ru.inmemorydb.core.Table;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import ru.inmemorydb.core.Row;

public class DatabaseStorage {
    public static void saveDatabase(Database db, String path) throws IOException {
        Path dbPath = Paths.get(path);
        if (!Files.exists(dbPath)) {
            Files.createDirectories(dbPath);
        }

        for (Table table : db.getTables()) {
            String tableFile = path + "/" + table.getName() + ".db";
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(tableFile))) {
                oos.writeObject(table);
            }
        }
    }

    public static Database loadDatabase(String path) throws IOException, ClassNotFoundException {
        Database db = new Database(Paths.get(path).getFileName().toString());

        File dbDir = new File(path);
        if (!dbDir.exists() || !dbDir.isDirectory()) {
            return db;
        }

        File[] tableFiles = dbDir.listFiles((dir, name) -> name.endsWith(".db"));
        if (tableFiles == null) {
            return db;
        }

        for (File file : tableFiles) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                Table table = (Table) ois.readObject();
                db.createTable(table.getName(), table.getColumns());
                for (Row row : table.getRows()) {
                    db.insertInto(table.getName(), Collections.singletonList(row));
                }
            }
        }

        return db;
    }
}