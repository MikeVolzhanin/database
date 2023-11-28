package Database;

import BackUp.BackUpSystem;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        System.out.println("База данных:\n1) Запустить существующую базу данных" +
                "\n2) Создать новую базу данных\n3) Создать backup-системы\n4) Загрузить backup-системы\n5) Автозаполнение таблицы");
        int input = scanner.nextInt();
        if(input == 1){
            System.out.println("Какой номер базы данных?");
            count = scanner.nextInt();
        }
        if(input == 2){
            System.out.println("Введите номер будущей базы данных:");
            count = scanner.nextInt();
            String Directory = "data" + count + "/";
            File directory = new File(Directory);
            if (directory.exists()) {
                System.out.println("База данных уже существует.");
                return;
            }
            System.out.println("Хорошо, новая база данных будет создана после создания первой таблицы.");
        }
        if(input == 3){
            BackUpSystem backUpSystem = new BackUpSystem();
            backUpSystem.createBackUp();
            return;
        }
        if(input == 4){
            System.out.println("Введите имя backup'a системы:");
            String backUpName = scanner.next();

            BackUpSystem backUpSystem = new BackUpSystem(backUpName);
            backUpSystem.loadBackUp();
            return;
        }
        if(input == 5){
            String tableName, fileName;
            System.out.println("Какой номер БД?");
            count = scanner.nextInt();
            Database.COUNT = count;
            System.out.println("Введите имя таблицы:");
            tableName = scanner.next();
            System.out.println("Введите имя файла:");
            fileName = scanner.next();
            TableCreation.autoFill(fileName, tableName);
            System.out.println("Таблица заполнена");
        }

        Database database = new Database(count);
        String command = "";

        while(!command.equals("exit")){
            command = scanner.nextLine();
            System.out.println("СУБД работает, введите команду...");
            // create имя_таблицы (значение тип,... , значениеN типN)
            if(command.contains("create")){
                database.setCommand(command);
                database.create();
                continue;
            }
            // insert into имя_таблицы values (значение, ... , значениеN) *пофискил проверку типа* *пофиксил добавление повторных строк*
            if(command.contains("insert")){
                database.setCommand(command);
                database.insert();
                continue;
            }
            // select колонка1, ... , колонкаN from имя таблицы || select * from имя_таблицы
            if(command.contains("select")){
                database.setCommand(command);
                database.select();
                continue;
            }
            // inner join имя_таблицы1 from имя_таблицы2 on имя_колонки
            if(command.contains("inner join")){
                database.setCommand(command);
                database.innerJoin();
                continue;
            }
            //join two имя_таблицы1, имя_таблицы2
            //join two students,variants
            if(command.contains("join two")){
                database.setCommand(command);
                database.join();
                continue;
            }
            //add into имя_таблицы column название_колонки тип_колонки
            if(command.contains("add into")){
                database.setCommand(command);
                database.add();
                continue;
            }
            //update имя_таблицы set имя_колонки=значение where имя_колонки=значение
            if(command.contains("update")){
                database.setCommand(command);
                database.update();
                continue;
            }
            //delete from имя_таблицы where имя_колнки=значение_колонки
            if(command.contains("delete from")){
                database.setCommand(command);
                database.delete();
                continue;
            }
        }
    }
}
