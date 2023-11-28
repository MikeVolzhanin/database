package Database;

import java.util.List;

public class Database {
    static int COUNT;
    private String command;
    public Database(int count){
        COUNT = count;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    void create(){
        Parser parser = new Parser(command);
        parser.createTable();
        List<String> columnNames = parser.getColumnNames();
        List<String> columnTypes = parser.getColumnTypes();
        String tableName = parser.getTableName();
        TableCreation.createFile(tableName, columnNames, columnTypes);
    }

    void insert(){
        Parser parser = new Parser(command);
        String insert = parser.insertRow();
        String tableName = parser.getTableName();
        TableCreation.insertFile(tableName, insert);
    }

    void select(){
        Parser parser = new Parser(command);
        parser.parseSelectCommand();
        List<String> columnNames = parser.getColumnNames();
        String tableName = parser.getTableName();
        TableCreation.select(tableName, columnNames);
     }

     void innerJoin(){
        Parser parser = new Parser(command);
        parser.parseJoin();
        String tableName1 = parser.getTableName1();
        String tableName2 = parser.getTableName2();
        String joinColumn = parser.getJoinColumn();
        TableCreation.innerJoin(tableName1, tableName2, joinColumn);
     }

     void join(){
        Parser parser = new Parser(command);
        parser.parseJoinTwo();
        String tableName1 = parser.getTableName1();
        String tableName2 = parser.getTableName2();
        TableCreation.join(tableName1, tableName2);
     }

     void add(){
        Parser parser = new Parser(command);
        parser.parseAdd();
        String columnName = parser.getJoinColumn2();
        String tableName = parser.getTableName();
        TableCreation.add(tableName, columnName);
     }

     void update(){
        Parser parser = new Parser(command);
        parser.updateParse();
        TableCreation.update(parser.getTableName(), parser.getColumnName(),parser.getValue(), parser.getSetValue(), parser.getColumnName1());
     }

     void delete(){
        Parser parser = new Parser(command);
        parser.parseDelete();
        TableCreation.delete(parser.getTableName(), parser.getColumnName(), parser.getColumnName1());
     }
}
