package com.obsidian.knight_api.utils.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.obsidian.knight_api.KnightPluginApi.sendMessage;

public class SqlManager {
    private final SqlPoolManager poolManager;

    public SqlManager(String u_url, String u_username, String u_password) throws SQLException {
        poolManager = new SqlPoolManager(u_url, u_username, u_password);
    }

    public void executeStatement(String query) throws SQLException, InterruptedException {
        try (Connection connection = poolManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException, InterruptedException {
        Connection connection = poolManager.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public void insertData(String table, String[] columns, Object[] values) throws SQLException, InterruptedException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(table).append(" (");

        // Appending column names
        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]);
            if (i != columns.length - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(") VALUES (");

        // Appending values
        for (int i = 0; i < values.length; i++) {
            queryBuilder.append("?");
            if (i != values.length - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(") ON DUPLICATE KEY UPDATE ");

// Appending update statements
        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append("=VALUES(").append(columns[i]).append(")");
            if (i != columns.length - 1) {
                queryBuilder.append(", ");
            }
        }

            try (Connection connection = poolManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            // Setting parameter values
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


 public boolean dataExists(String table, String column, Object value) throws SQLException, InterruptedException {
     StringBuilder queryBuilder = new StringBuilder();
     queryBuilder.append("SELECT * FROM ").append(table).append(" WHERE ").append(column).append(" = ?");

     try (Connection connection = poolManager.getConnection();
          PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
         preparedStatement.setObject(1, value);
         ResultSet resultSet = preparedStatement.executeQuery();
         return resultSet.next();
          }catch (SQLException e){
              e.printStackTrace();
              return false;
          }
 }

    public void updateData(String table, String[] updateColumns, Object[] updateValues, String conditionColumn, Object conditionValue) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(table).append(" SET ");

        List<Object> nonNullValues = new ArrayList<>();

        // Appending update statements using StringBuilder
        for (int i = 0; i < updateColumns.length; i++) {
            Object value = updateValues[i];
            if (value != null) {
                queryBuilder.append("`").append(updateColumns[i]).append("` = ?");
                nonNullValues.add(value);

                if (i < updateColumns.length - 1) {

                    queryBuilder.append(", ");
                }
            }
        }

        queryBuilder.append(" WHERE `").append(conditionColumn).append("` = ?");
        try (Connection connection = poolManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {

            // Setting parameter values for update columns using explicit data type handling
            for (int i = 0; i < nonNullValues.size(); i++) {
                Object value = nonNullValues.get(i);
                // Handle other data types as needed
                preparedStatement.setObject(i + 1, value);
            }

            // Setting parameter value for the condition
            // Handle other data types as needed
            preparedStatement.setObject(nonNullValues.size() + 1, conditionValue);
            preparedStatement.executeUpdate();
        } catch (SQLException | InterruptedException e) {
            // Handle exceptions more gracefully, log the error, or throw a custom exception
            e.printStackTrace();
        }
    }


    public void updateData(String table, String updateColumn, Object updateValue, String conditionColumn, Object conditionValue) throws SQLException, InterruptedException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(table)
                .append(" SET `").append(updateColumn).append("` = ? WHERE `").append(conditionColumn).append("` = ?");

        try (Connection connection = poolManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            preparedStatement.setObject(1, updateValue);
            preparedStatement.setObject(2, conditionValue);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String table, String[] columns, String[] columnTypes) throws SQLException, InterruptedException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");

        // Adding the 'id' field with auto-increment
        queryBuilder.append("id INT AUTO_INCREMENT PRIMARY KEY, ");

        // Appending other column names and types
        for (int i = 0; i < columns.length; i++) {
            queryBuilder.append(columns[i]).append(" ").append(columnTypes[i]);
            if (i != columns.length - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");

        try (Connection connection = poolManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(String table, String conditionColumn, Object conditionValue) throws SQLException, InterruptedException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(table).append(" WHERE ").append(conditionColumn).append(" = ?");

        try (Connection connection = poolManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {
            preparedStatement.setObject(1, conditionValue);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws SQLException, InterruptedException {
        return poolManager.getConnection();
    }
    public void closeConnection() throws SQLException {
        poolManager.closeConnections();

    }

    public static void main(String[] args) {
        try {
            SqlManager sqlManager = new SqlManager("jdbc:mysql://localhost:3306/test", "root", "");
            sqlManager.createTable("users", new String[]{"name", "age"}, new String[]{"VARCHAR(255)", "INT"});
            sqlManager.insertData("users", new String[]{"name", "age"}, new Object[]{"John", 25});
            String [] updateColumns = {"name", "age"};
            Object [] updateValues = {"John", 242234234};
           sqlManager.updateData("users", updateColumns, updateValues, "name", "John");
            sqlManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
