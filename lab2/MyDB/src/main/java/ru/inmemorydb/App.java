package ru.inmemorydb;

import ru.inmemorydb.core.Database;
import ru.inmemorydb.gui.DatabaseGUI;
import ru.inmemorydb.storage.DatabaseStorage;
import ru.inmemorydb.command.Command;
import ru.inmemorydb.command.CommandParser;

import java.io.File;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                runCommandLineMode(args);
            } else {
                Database db;
                File dbFile = new File("my-database.db");

                if (dbFile.exists()) {
                    try {
                        db = DatabaseStorage.loadDatabase("my-database.db");
                    } catch (Exception e) {
                        int choice = JOptionPane.showConfirmDialog(null,
                                "Не удалось загрузить базу данных. Создать новую?",
                                "Ошибка загрузки",
                                JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            db = new Database();
                        } else {
                            return;
                        }
                    }
                } else {
                    int choice = JOptionPane.showConfirmDialog(null,
                            "База данных не найдена. Создать новую?",
                            "База данных",
                            JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        db = new Database();
                    } else {
                        return;
                    }
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

//java -cp target\classes ru.inmemorydb.App
//javac -d target\classes src\main\java\ru\inmemorydb\core\*.java src\main\java\ru\inmemorydb\\util\*.java src\main\java\ru\inmemorydb\gui\*.java src\main\java\ru\inmemorydb\storage\*.java src\main\java\ru\inmemorydb\command\*.java src\main\java\ru\inmemorydb\App.java//
//java -cp target\classes ru.inmemorydb.App --db "C:\Users\Kirill Zimaltynov\Desktop\Testing\MyDB\my-database.db" -c "CREATE TABLE users (id INT, name STRING)"