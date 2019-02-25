package hse.hw05.database;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        var db = DbHandler.getInstance();
        var in = new Scanner(System.in);
        String name, phone;
        while (true) {
            int n = Integer.parseInt(in.next());
            switch (n) {
                case 0:
                    return;
                case 1: {
                    name = in.next();
                    phone = in.next();
                    db.addOwnership(name, phone);
                }
                case 2: {
                    name = in.next();
                    System.out.println(db.phonesByName(name));
                }
                case 3: {
                    phone = in.next();
                    System.out.println(db.namesByPhone(phone));
                }
                case 4: {
                    name = in.next();
                    phone = in.next();
                    db.deleteOwnership(name, phone);
                }
                case 5: {
                    name = in.next();
                    phone = in.next();
                    var newName = in.next();
                    db.changeName(name, phone, newName);
                }
                case 6: {
                    name = in.next();
                    phone = in.next();
                    var newPhone = in.next();
                    db.changePhone(name, phone, newPhone);
                }
                case 7: {
                    System.out.println(db.getAllOwnerships());
                }
            }
        }
    }
}