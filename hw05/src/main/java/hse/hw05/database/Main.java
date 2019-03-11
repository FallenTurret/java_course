package hse.hw05.database;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Class with single method main, which process keyboard input to work with database by DbHandler methods
 */
public class Main {
    public static void main(String[] args) throws SQLException {
        var db = DbHandler.getInstance("jdbc:sqlite:PhoneBook.db");
        var in = new Scanner(System.in);
        System.out.println("Type your commands one per string. Enter 8 to display list of commands");
        while (true) {
            int n = Integer.parseInt(in.next());
            switch (n) {
                case 0:
                    return;
                case 1: {
                    String name = in.next();
                    String phone = in.next();
                    db.addOwnership(name, phone);
                    break;
                }
                case 2: {
                    String name = in.next();
                    System.out.println(db.phonesByName(name));
                    break;
                }
                case 3: {
                    String phone = in.next();
                    System.out.println(db.namesByPhone(phone));
                    break;
                }
                case 4: {
                    String name = in.next();
                    String phone = in.next();
                    db.deleteOwnership(name, phone);
                    break;
                }
                case 5: {
                    String name = in.next();
                    String phone = in.next();
                    var newName = in.next();
                    db.changeName(name, phone, newName);
                    break;
                }
                case 6: {
                    String name = in.next();
                    String phone = in.next();
                    var newPhone = in.next();
                    db.changePhone(name, phone, newPhone);
                    break;
                }
                case 7: {
                    System.out.println(db.getAllOwnerships());
                    break;
                }
                case 8: {
                    System.out.println("List of commands:");
                    System.out.println("0 - exit");
                    System.out.println("1 name phone - add new record to table");
                    System.out.println("2 name - print all phones of given person");
                    System.out.println("3 phone - print all owners of given phone");
                    System.out.println("4 phone name - delete record from database");
                    System.out.println("5 name phone newName - change names in records with given name and phone");
                    System.out.println("6 name phone newPhone - change phones in records with given name and phone");
                    System.out.println("7 - print all records");
                    System.out.println("8 - show this message");
                    break;
                }
            }
        }
    }
}