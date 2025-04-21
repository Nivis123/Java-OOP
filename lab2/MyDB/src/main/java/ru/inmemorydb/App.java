package ru.inmemorydb;

import ru.inmemorydb.core.Database;
import ru.inmemorydb.gui.DatabaseGUI;
import ru.inmemorydb.storage.DatabaseStorage;
import ru.inmemorydb.command.Command;
import ru.inmemorydb.command.CommandParser;

import java.io.File;

public class App {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                runCommandLineMode(args);
            } else {
                Database db = new Database();
                try {
                    db = DatabaseStorage.loadDatabase("my-database.db");
                } catch (Exception e) {
                    System.out.println("Не удалось загрузить базу данных: " + e.getMessage());
                }

                DatabaseGUI gui = new DatabaseGUI(db);
                gui.setVisible(true);

                Database finalDb = db;
                gui.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        try {
                            new File("my-database.db").mkdirs();
                            DatabaseStorage.saveDatabase(finalDb, "my-database.db");
                        } catch (Exception e) {
                            System.err.println("Ошибка при сохранении базы данных: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runCommandLineMode(String[] args) throws Exception {
        String dbPath = "my-database.db";
        String command = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--db":
                    dbPath = args[++i];
                    break;
                case "-c":
                    command = args[++i];
                    break;
            }
        }

        if (command == null) {
            System.out.println("Использование: java -jar database.jar --db <путь> -c \"команда\"");
            return;
        }

        Database db;
        try {
            db = DatabaseStorage.loadDatabase(dbPath);
        } catch (Exception e) {
            System.out.println("Создана новая база данных");
            db = new Database(new File(dbPath).getName());
        }

        Command cmd = CommandParser.parse(command);
        cmd.execute(db);

        DatabaseStorage.saveDatabase(db, dbPath);
        System.out.println("Команда выполнена успешно");
    }
}