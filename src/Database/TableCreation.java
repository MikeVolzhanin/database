package Database;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TableCreation {
    static final String dataDirectory = "server/" + "data" + Database.COUNT + "/";

    static void createFile(String tableName, List<String> columnNames, List<String> columnTypes) {
        if (tableName == null) {
            System.out.println("Ошибка в синтаксисе команды.");
            return;
        }
        File directory = new File(dataDirectory);
        if (!directory.exists()) {
            if (!directory.mkdirs()) System.out.println("Ошибка при создании директории.");
        }

        try {
            File file = new File(dataDirectory + tableName + ".txt");
            // Создаем файл, если он не существует
            if (file.createNewFile()) {
                System.out.println("Таблица успешно создана.");
            } else {
                System.out.println("Таблица уже существует.");
            }
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String header = IntStream.range(0, columnNames.size())
                    .mapToObj(i -> columnNames.get(i) + ":" + columnTypes.get(i))
                    .collect(Collectors.joining(","));

            bufferedWriter.write(header);
            bufferedWriter.newLine();

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Ошибка при создании файла");
            throw new RuntimeException(e);
        }
    }

    static void insertFile(String tableName, String data) {
        File file = new File(dataDirectory + tableName + ".txt");
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            String[] header = bufferedReader.readLine().split(",");

            //Проверяем наличие идентичной строчки в таблице
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().equals(data)) {
                    System.out.println("Такая строчка уже существует!");
                    return;
                }
            }

            List<String> types = new ArrayList<>();
            for (int i = 0; i < header.length; i++) {
                String[] temp = header[i].split(":");
                types.add(temp[1].trim());
            }

            String[] temp1 = data.split(",");

            for (int i = 0; i < types.size(); i++) {
                if (isNumber(temp1[i])) {
                    if (!types.get(i).equals("int")) {
                        System.out.println("Ошибка в типе даныых");
                        return;
                    }
                }
                if (!isNumber(temp1[i])) {
                    if (types.get(i).equals("int")) {
                        System.out.println("Ошибка в типе даныых");
                        return;
                    }
                }
            }

            bufferedWriter.write(data + "\n");

            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void autoFill(String fileName, String tableName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            List<List<String>> table = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] temp = line.split(" ");
                String newStr = String.join(",", temp).trim();
                insertFile(tableName, newStr);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void select(String tableName, List<String> columnNames) {
        try {
            // Открываем файл для чтения
            BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + tableName + ".txt"));
            String line;

            // Считываем первую строку, которая содержит названия и типы столбцов
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");

            // Создаем маппинг для индексов столбцов по их названиям
            Map<String, Integer> columnIndexMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                String[] columnInfo = headers[i].split(":");
                String columnName = columnInfo[0];
                columnIndexMap.put(columnName, i);
            }

            // Определяем индексы выбранных столбцов
            List<Integer> selectedColumnIndices = new ArrayList<>();
            if (columnNames.get(0).equals("*")) {
                for (int i = 0; i < headers.length; i++) {
                    String[] columnInfo = headers[i].split(":");
                    String columnName = columnInfo[0];
                    selectedColumnIndices.add(i);
                }
            } else {
                for (String columnName : columnNames) {
                    Integer columnIndex = columnIndexMap.get(columnName);
                    if (columnIndex != null) {
                        selectedColumnIndices.add(columnIndex);
                    }
                }
            }

            // Инициализируем массив для хранения максимальной ширины каждого столбца (включая заголовки)
            int[] columnWidths = new int[selectedColumnIndices.size()];

            // Учитываем ширину заголовков столбцов при определении ширины
            for (int i = 0; i < selectedColumnIndices.size(); i++) {
                int columnIndex = selectedColumnIndices.get(i);
                if (columnIndex >= 0 && columnIndex < headers.length) {
                    columnWidths[i] = headers[columnIndex].length();
                }
            }

            // Читаем данные и находим максимальную ширину для каждого столбца
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                for (int i = 0; i < selectedColumnIndices.size(); i++) {
                    int columnIndex = selectedColumnIndices.get(i);
                    if (columnIndex >= 0 && columnIndex < columns.length) {
                        columnWidths[i] = Math.max(columnWidths[i], columns[columnIndex].length());
                    }
                }
            }

            // Выводим названия выбранных столбцов
            for (int i = 0; i < selectedColumnIndices.size(); i++) {
                int columnIndex = selectedColumnIndices.get(i);
                if (columnIndex >= 0 && columnIndex < headers.length) {
                    String header = headers[columnIndex].split(":")[0];
                    int columnWidth = columnWidths[i];
                    System.out.print(String.format("%-" + columnWidth + "s", header) + " ");
                }
            }
            System.out.println();

            // Выводим выбранные столбцы в виде таблицы
            reader.close();
            reader = new BufferedReader(new FileReader(dataDirectory + tableName + ".txt"));
            reader.readLine(); // Пропускаем первую строку с заголовками
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                for (int i = 0; i < selectedColumnIndices.size(); i++) {
                    int columnIndex = selectedColumnIndices.get(i);
                    if (columnIndex >= 0 && columnIndex < columns.length) {
                        String value = columns[columnIndex];
                        int columnWidth = columnWidths[i];
                        System.out.print(String.format("%-" + columnWidth + "s", value) + " ");
                    }
                }
                System.out.println();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void innerJoin(String tableName1, String tableName2, String joinColumn) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + tableName1 + ".txt"));
            String line;
            // Считываем первую строку, которая содержит названия и типы столбцов таблицы 1
            String headerLine = reader.readLine();

            List<String> columnNames1 = new ArrayList<>();
            List<String> rows1 = new ArrayList<>();

            String[] headers = headerLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                columnNames1.add(headers[i].split(":")[0]);
            }

            while ((line = reader.readLine()) != null) {
                rows1.add(line);
            }

            // Считываем первую строку, которая содержит названия и типы столбцов таблицы 2
            BufferedReader reader1 = new BufferedReader(new FileReader(dataDirectory + tableName2 + ".txt"));
            String headerLine1 = reader1.readLine();

            List<String> columnNames2 = new ArrayList<>();
            List<String> rows2 = new ArrayList<>();

            String[] headers1 = headerLine1.split(",");
            for (int i = 0; i < headers1.length; i++) {
                columnNames2.add(headers1[i].split(":")[0]);
            }

            while ((line = reader1.readLine()) != null) {
                rows2.add(line);
            }

            // Находим индексы столбцов для объединения в обеих таблицах
            int joinIndex1 = columnNames1.indexOf(joinColumn);

            int joinIndex2 = columnNames2.indexOf(joinColumn);

            if (joinIndex1 == -1 || joinIndex2 == -1) {
                throw new IllegalArgumentException("Join column not found in one or both tables.");
            }

            List<Map<String, String>> table1Mapping = rows1.stream()
                    .map(row -> {
                        String[] parts = row.split(",");
                        Map<String, String> map = new HashMap<>();
                        for (int i = 0; i < columnNames1.size(); i++) {
                            map.put(columnNames1.get(i), parts[i]);
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            List<Map<String, String>> table2Mapping = rows2.stream()
                    .map(row -> {
                        String[] parts = row.split(",");
                        Map<String, String> map = new HashMap<>();
                        for (int i = 0; i < columnNames2.size(); i++) {
                            map.put(columnNames2.get(i), parts[i]);
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            // INNER JOIN между таблицами с учетом дубликатов
            List<Map<String, String>> result = new ArrayList<>();
            for (Map<String, String> row1 : table1Mapping) {
                for (Map<String, String> row2 : table2Mapping) {
                    if (row1.get(joinColumn).equals(row2.get(joinColumn))) {
                        Map<String, String> joinedRow = new HashMap<>(row1);
                        joinedRow.putAll(row2);
                        result.add(joinedRow);
                    }
                }
            }

            String header = String.join("\t", columnNames1) + "\t" + String.join("\t", columnNames2);
            System.out.println(header);

            // Значения
            for (Map<String, String> row : result) {
                List<String> values = new ArrayList<>();
                for (String columnName : columnNames1) {
                    values.add(row.get(columnName));
                }
                for (String columnName : columnNames2) {
                    values.add(row.get(columnName));
                }
                String rowString = String.join("\t", values);
                System.out.println(rowString);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void join(String tableName1, String tableName2){
        try {
            Map<String, String> students = new HashMap<>();
            Map<String, String> variants = new HashMap<>();
            generateTestingTable(tableName1, tableName2);
            BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + tableName1 +".txt"));
            BufferedReader reader1 = new BufferedReader(new FileReader(dataDirectory + tableName2 + ".txt"));
            BufferedReader reader2 = new BufferedReader(new FileReader(dataDirectory + "testing_table.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataDirectory + "output.txt"));
            String line;
            line = reader.readLine();

            while((line = reader.readLine()) != null){
                String[] temp = line.split(",");
                students.put(temp[0], temp[1] + " " + temp[2] + " " + temp[3]);
            }
            while((line = reader1.readLine()) != null){
                String[] temp = line.split(",");
                variants.put(temp[0], temp[1]);
            }
            line = reader2.readLine();
            writer.write("full_name:varchar,path_to_file:varchar\n");
            while((line = reader2.readLine()) != null){
                String[] temp = line.split(",");
                String full_name = students.get(temp[0]);
                String path_to_file = variants.get(temp[1]);
                writer.write(full_name + "," + path_to_file);
                writer.newLine();
            }
            reader.close();
            reader1.close();
            reader2.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void generateTestingTable(String tableName, String tableName1){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataDirectory + tableName + ".txt"));
            BufferedReader reader1 = new BufferedReader(new FileReader(dataDirectory + tableName1 + ".txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(dataDirectory + "testing_table.txt"));
            String line;
            //id студентов
            List<String> tableId1 = new ArrayList<>();
            line = reader.readLine();
            while((line = reader.readLine()) != null){;
                tableId1.add(line.split(",")[0]);
            }
            //id вариантов
            line = reader1.readLine();
            List<String> tableId2 = new ArrayList<>();
            while((line = reader1.readLine()) != null){;
                tableId2.add(line.split(",")[0]);
            }
            List<List<String>> newTable = getRandomAssignments(tableId1, tableId2);
            writer.write("people_id:int,variant_id:int");
            writer.newLine();
            for(List<String> res : newTable){
                String result = res.get(0) + "," + res.get(1);
                writer.write(result);
                writer.newLine();
            }
            reader.close();
            reader1.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<List<String>> getRandomAssignments(List<String> students, List<String> variants) {
        List<List<String>> assignments = new ArrayList<>();
        Random random = new Random();

        for (String student : students) {
            String variant = variants.get(random.nextInt(variants.size()));
            assignments.add(List.of(student, variant));
        }

        return assignments;
    }

    static void add(String tableName, String columnName) {
        String filePath = dataDirectory + tableName + ".txt"; // Путь к исходному файлу
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Прочитаем заголовок
            String header = bufferedReader.readLine();
            String[] headerColumns = header.split(",");
            String newHeader = header + "," + columnName; // Добавляем новую колонку к заголовку

            // Создадим временный файл, в который будем записывать результат
            File tempFile = new File(dataDirectory + tableName + ".txt");
            FileWriter fileWriter = new FileWriter(tempFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Запишем новый заголовок
            bufferedWriter.write(newHeader);
            bufferedWriter.newLine(); // Добавляем перевод строки

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                // Прочитаем каждую строку и добавим новое значение к ней
                String newLine = line + ",null"; // Значение для новой колонки
                bufferedWriter.write(newLine);
                bufferedWriter.newLine(); // Добавляем перевод строки
            }

            bufferedReader.close();
            bufferedWriter.close();

            // Заменяем исходный файл временным файлом с новыми данными
            tempFile.renameTo(file);

            System.out.println("Новая колонка добавлена к файлу '" + filePath + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void update(String tableName, String columnName, String value, String setValue, String columnName1) {
        try {
            File file = new File(dataDirectory + tableName + ".txt");
            File tempFile = new File(dataDirectory + tableName + "_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            String headerLine = reader.readLine();
            writer.write(headerLine);
            writer.newLine();

            String[] headers = headerLine.split(",");
            String[] types = new String[headers.length];
            int columnIndex = -1;
            int columnIndex1 = -1;

            // Находим индекс выбранных столбцов
            for (int i = 0; i < headers.length; i++) {
                String[] columnInfo = headers[i].split(":");
                String columnNameHeader = columnInfo[0];
                types[i] = columnInfo[1];
                if (columnNameHeader.equals(columnName)) {
                    columnIndex = i;
                } else if (columnNameHeader.equals(columnName1)) {
                    columnIndex1 = i;
                }
            }

            if (columnIndex == -1 || columnIndex1 == -1) {
                System.out.println("Один или оба столбца не найдены.");
                return;
            }

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                String columnValue = columns[columnIndex1].trim();

                // Сравниваем значение в выбранном столбце с заданным значением
                if (columnValue.equals(value)) {
                    if (types[columnIndex].equals("int") && isNumber(setValue)) {
                        columns[columnIndex] = setValue;
                    } else if (types[columnIndex].equals("varchar") && !isNumber(setValue)) {
                        columns[columnIndex] = setValue;
                    } else {
                        System.out.println("Ошибка в типе данных");
                    }
                }

                String updatedLine = String.join(",", columns);
                writer.write(updatedLine);
                writer.newLine();
            }

            reader.close();
            writer.close();


            // Заменяем исходный файл обновленным временным файлом
            if (file.delete()) {
                if (!tempFile.renameTo(file)) {
                    System.out.println("Не удалось переименовать временный файл.");
                }
            } else {
                System.out.println("Не удалось удалить исходный файл.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delete(String tableName, String columnName, String columnValueName) {
        try {
            File file = new File(dataDirectory + tableName + ".txt");
            File tempFile = new File(dataDirectory + tableName + "_temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            String headerLine = reader.readLine();
            writer.write(headerLine);
            writer.newLine();

            String[] headers = headerLine.split(",");
            int columnIndex = -1;

            // Находим индекс выбранного столбца
            for (int i = 0; i < headers.length; i++) {
                String[] columnInfo = headers[i].split(":");
                String columnNameHeader = columnInfo[0];
                if (columnNameHeader.equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                System.out.println("Столбец с именем " + columnName + " не найден.");
                return;
            }

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                String columnValue = columns[columnIndex].trim();

                // Если значение в выбранном столбце не совпадает с заданным, записываем строку в новый файл
                if (!columnValue.equals(columnValueName)) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            reader.close();
            writer.close();

            // Заменяем исходный файл обновленным временным файлом
            if (file.delete()) {
                if (!tempFile.renameTo(file)) {
                    System.out.println("Не удалось переименовать временный файл.");
                }
            } else {
                System.out.println("Не удалось удалить исходный файл.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }
}

