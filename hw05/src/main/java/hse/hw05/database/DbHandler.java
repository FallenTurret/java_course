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

    private static final String CON_STR = "jdbc:sqlite:PhoneBook.db";

    private static DbHandler instance = null;

    private Connection connection = null;

    /**
     * Method to get instance of DbHandler class
     * @return DbHandler instance
     * @throws SQLException acquired from constructor
     */
    public static DbHandler getInstance() throws SQLException {
        if (instance == null) {
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
        try (var statement = connection.createStatement()) {
            statement.execute("INSERT INTO Ownerships VALUES ('" + name + "', '" + phone + "')");
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
        try (var statement = connection.createStatement()) {
            List<String> phones = new ArrayList<>();
            var resultSet = statement.executeQuery(
                    "SELECT Phone FROM Ownerships WHERE Name = '" + name + "'");
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
        try (var statement = connection.createStatement()) {
            List<String> names = new ArrayList<>();
            var resultSet = statement.executeQuery(
                    "SELECT Name FROM Ownerships WHERE Phone = '" + phone + "'");
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
            statement.execute(
                    "DELETE FROM Ownerships WHERE Name = '" + name + "' AND Phone = '" + phone + "'");
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
        try (var statement = connection.createStatement()) {
            statement.execute(
                    "UPDATE Ownerships SET Name = '" + newName + "' " +
                            "WHERE Name = '" + name + "' AND Phone = '" + phone + "'");
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
        try (var statement = connection.createStatement()) {
            statement.execute(
                    "UPDATE Ownerships SET Phone = '" + newPhone +
                            "' WHERE Name = '" + name + "' AND Phone = '" + phone + "'");
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

    public void deleteAllOwnerships() {
        try (var statement = connection.createStatement()) {
            statement.execute("DELETE FROM Ownerships");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}