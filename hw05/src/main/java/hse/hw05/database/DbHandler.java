package hse.hw05.database;

import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singletone class for work with phone book using sqlite
 * Stores name and phones with length less than 256 symbols
 * Works with table in database PhoneBook.db
 */
public class DbHandler {

    private static String CON_STR;
    private static DbHandler instance = null;

    private Connection connection = null;

    /**
     * Method to get instance of DbHandler class with connection to database with given URL
     * @param url database URL
     * @return DbHandler instance
     * @throws SQLException acquired from constructor
     */
    public static DbHandler getInstance(String url) throws SQLException {
        if (instance == null) {
            CON_STR = url;
            instance = new DbHandler();
        }
        return instance;
    }

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        connection = DriverManager.getConnection(CON_STR);
        var res = connection.getMetaData()
                .getTables(null, null, "Ownerships", null);
        if (!res.next()) {
            var statement = connection.createStatement();
            statement.execute("CREATE TABLE Ownerships (" +
                    "Name varchar(255)," +
                    "Phone varchar(255))");
        }
    }

    /**
     * Adds new record about ownership
     * @param name person's name
     * @param phone person's phone
     */
    public void addOwnership(@NotNull String name, @NotNull String phone) {
        try (var statement = connection.prepareStatement("INSERT INTO Ownerships VALUES (?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds all phones owned by specific person
     * @param name person's name
     * @return list of phones
     */
    public List<String> phonesByName(@NotNull String name) {
        try (var statement = connection.prepareStatement("SELECT Phone FROM Ownerships WHERE Name = ?")) {
            List<String> phones = new ArrayList<>();
            statement.setString(1, name);
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                phones.add(resultSet.getString("Phone"));
            }
            return phones;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Finds all owners of specific phone
     * @param phone number
     * @return list of names
     */
    public List<String> namesByPhone(@NotNull String phone) {
        try (var statement = connection.prepareStatement("SELECT Name FROM Ownerships WHERE Phone = ?")) {
            List<String> names = new ArrayList<>();
            statement.setString(1, phone);
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                names.add(resultSet.getString("Name"));
            }
            return names;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * deletes all records of ownership with give name and phone
     * @param name person's name
     * @param phone person's phone
     * @return true if something was deleted, false if these records are missing
     */
    public boolean deleteOwnership(@NotNull String name, @NotNull String phone) {
        try (var statement = connection.createStatement()) {
            var size = statement.executeQuery("SELECT COUNT(*) FROM Ownerships").getInt(1);
            try (var prepared = connection.prepareStatement(
                    "DELETE FROM Ownerships WHERE Name = ? AND Phone = ?")) {
                prepared.setString(1, name);
                prepared.setString(2, phone);
                prepared.execute();
            }
            var newSize = statement.executeQuery("SELECT COUNT(*) FROM Ownerships").getInt(1);
            return size > newSize;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Changes name in each record with given name and phone
     * @param name person's name
     * @param phone person's phone
     * @param newName person's new name
     */
    public void changeName(@NotNull String name, @NotNull String phone, @NotNull String newName) {
        try (var statement = connection.prepareStatement(
                "UPDATE Ownerships SET Name = ? WHERE Name = ? AND Phone = ?")) {
            statement.setString(1, newName);
            statement.setString(2, name);
            statement.setString(3, phone);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes phone in each record with given name and phone
     * @param name person's name
     * @param phone person's phone
     * @param newPhone person's new phone
     */
    public void changePhone(@NotNull String name, @NotNull String phone, @NotNull String newPhone) {
        try (var statement = connection.prepareStatement(
                "UPDATE Ownerships SET Phone = ? WHERE Name = ? AND Phone = ?")) {
            statement.setString(1, newPhone);
            statement.setString(2, name);
            statement.setString(3, phone);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gives all records in the table
     * @return list of ownerships
     */
    public List<Ownership> getAllOwnerships() {
        try (var statement = connection.createStatement()) {
            List<Ownership> ownerships = new ArrayList<>();
            var resultSet = statement.executeQuery(
                    "SELECT * FROM Ownerships");
            while (resultSet.next()) {
                ownerships.add(new Ownership(
                        resultSet.getString("Name"),
                        resultSet.getString("Phone")
                ));
            }
            return ownerships;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * deletes all records from table "Ownerships"
     */
    public void deleteAllOwnerships() {
        try (var statement = connection.createStatement()) {
            statement.execute("DELETE FROM Ownerships");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}