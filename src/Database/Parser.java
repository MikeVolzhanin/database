package Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private List<String> columnNames;
    private List<String> columnTypes;
    private final String command;
    private String tableName;

    private String tableName1;
    private String tableName2;
    private String setValue;
    private String columnName;
    private String columnName1;
    private String value;

    public String getSetValue() {
        return setValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getValue() {
        return value;
    }

    public String getTableName3() {
        return tableName3;
    }

    public String getJoinColumn1() {
        return joinColumn1;
    }

    public String getJoinColumn2() {
        return joinColumn2;
    }

    private String tableName3;
    private String joinColumn1;
    private String joinColumn2;
    private String joinColumn;

    public Parser(String command) {
        this.command = command;
    }
    public String getJoinColumn() {
        return joinColumn;
    }
    public String getTableName1() {
        return tableName1;
    }

    public String getTableName2() {
        return tableName2;
    }
    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<String> getColumnTypes() {
        return columnTypes;
    }

    public String getTableName() {
        return tableName;
    }

    //Парсим значение из команды create имя_таблицы (agr1 type, arg2 type2, ... , argN typeN) и возвращаем columnNames, columnTypes
    public void createTable(){
        Pattern pattern = Pattern.compile("create\\s+(\\w+)\\s*\\((.*?)\\)");
        Matcher matcher = pattern.matcher(command);

        columnNames = new ArrayList<>();
        columnTypes = new ArrayList<>();

        if(matcher.find()){
            tableName = matcher.group(1); // Получаем имя таблицы
            if(tableName == null){
                System.out.println("Ошибка в синтаксисе -> tableName is null");
                return;
            }
            String columnDefinitions = matcher.group(2);
            String[] columns = columnDefinitions.split(",");

            for (String column : columns) {
                String[] parts = column.trim().split(" ");
                if (parts.length == 2) {
                    columnNames.add(parts[0].trim());
                    columnTypes.add(parts[1].trim());
                }
            }
        }
    }

    //Парсим значения из команды insert into имя_таблицы values (arg1, arg2, ... , argN)
    public String insertRow() {
        // Регулярное выражение для извлечения имени таблицы и списка аргументов
        Pattern pattern = Pattern.compile("insert\\s+into\\s+(\\w+)\\s+values\\s*\\((.*?)\\)");
        Matcher matcher = pattern.matcher(command);
        String result = null;

        if (matcher.find()) {
            tableName = matcher.group(1).trim(); // Имя таблицы
            result = matcher.group(2).trim(); // Список аргументов

        } else {
            System.out.println("Входная строка не соответствует ожидаемому формату.");
        }

        return result;
    }

    public  void parseSelectCommand() {
        Pattern pattern = Pattern.compile("select\\s+(\\*|\\w+(?:,\\s*\\w+)*)\\s+from\\s+(\\w+)");
        Matcher matcher = pattern.matcher(command);

        if (matcher.find()) {
            String columnsPart = matcher.group(1).trim();
            tableName = matcher.group(2).trim();

            String[] columnArray = columnsPart.split(",");
            columnNames = Arrays.asList(columnArray);
        } else {
            throw new IllegalArgumentException("Invalid SELECT command: " + command);
        }
    }

    public void parseJoin() {
        // Паттерн для извлечения информации о INNER JOIN
        Pattern pattern = Pattern.compile("inner join\\s+(\\w+)\\s+from\\s+(\\w+)\\s+on\\s+(\\w+)");
        Matcher matcher = pattern.matcher(command);

        if (matcher.find()) {
            tableName1 = matcher.group(1).trim();
            tableName2 = matcher.group(2).trim();
            joinColumn = matcher.group(3).trim();
        } else {
            throw new IllegalArgumentException("Invalid INNER JOIN string: " + command);
        }
    }

    public void parseJoinTwo() {
        // Регулярное выражение для парсинга строки
        String regex = "join two (\\w+),(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);

        if (matcher.find()) {
            tableName1 = matcher.group(1).trim();
            tableName2 = matcher.group(2).trim();
        }else {
            throw new IllegalArgumentException("Invalid INNER JOIN string: " + command);
        }
    }

    public void parseAdd(){
        // Определение шаблона регулярного выражения для парсинга
        String regex = "add into (\\S+) column (\\S+) (\\S+)";

        // Создание объекта Pattern
        Pattern pattern = Pattern.compile(regex);

        // Создание объекта Matcher
        Matcher matcher = pattern.matcher(command);

        // Поиск соответствий
        if (matcher.find()) {
            // Получение значений из групп
            tableName = matcher.group(1).trim();
            joinColumn = matcher.group(2).trim();
            joinColumn1 = matcher.group(3).trim();
            joinColumn2 = joinColumn + ":" + joinColumn1;
        } else {
            System.out.println("Строка не соответствует шаблону");
        }
    }

    public String getColumnName1() {
        return columnName1;
    }

    public void updateParse(){
        String regex = "update\\s+(\\S+)\\s+set\\s+(\\S+)=(\\S+)\\s+where\\s+(\\S+)=(\\S+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);

        if (matcher.find()) {
            tableName = matcher.group(1).trim();
            columnName = matcher.group(2).trim();
            setValue = matcher.group(3).trim();
            columnName1 = matcher.group(4).trim();
            value = matcher.group(5).trim();
        }else{
            System.out.println("Ошибка в UpdateParse");
        }
    }

    public void parseDelete() {
        Pattern pattern = Pattern.compile("delete from (\\S+) where (\\S+)=(.+)");
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            tableName = matcher.group(1);
            columnName = matcher.group(2);
            columnName1 = matcher.group(3);
            System.out.println(tableName + " " + columnName + " " + columnName1);
        } else {
            System.out.println("Запрос не соответствует ожидаемому формату.");
        }
    }
}
