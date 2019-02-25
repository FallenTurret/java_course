package hse.hw05.database;

import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DbHandlerTest {

    private static DbHandler db;

    @BeforeAll
    static void initAll() throws SQLException {
        db = DbHandler.getInstance();
    }

    @AfterEach
    void tearDown() {
        db.deleteAllOwnerships();
    }

    @Test
    void addOwnership_shouldAddNewRecordInDatabase() {
        List<Ownership> l = new ArrayList<>();
        l.add(new Ownership("a", "123"));
        db.addOwnership("a", "123");
        assertEquals(l, db.getAllOwnerships());
    }

    @Test
    void phonesByName_shouldReturnListOfPhonesInAnyOrder() {
        List<String> l = new ArrayList<>();
        l.add("123");
        l.add("1234");
        db.addOwnership("a", "1234");
        db.addOwnership("b", "123");
        db.addOwnership("a", "123");
        var phones = db.phonesByName("a");
        phones.sort(Comparator.naturalOrder());
        assertEquals(l, phones);
    }

    @Test
    void namesByPhone_shouldReturnListOfNamesInAnyOrder() {
        List<String> l = new ArrayList<>();
        l.add("a");
        l.add("b");
        db.addOwnership("a", "1234");
        db.addOwnership("b", "123");
        db.addOwnership("a", "123");
        var names = db.namesByPhone("123");
        names.sort(Comparator.naturalOrder());
        assertEquals(l, names);
    }

    @Test
    void deleteOwnership_shouldDeleteAllGivenRecordsInDatabase() {
        db.addOwnership("a", "1234");
        db.addOwnership("b", "123");
        db.addOwnership("a", "123");
        db.addOwnership("a", "1234");
        assertTrue(db.deleteOwnership("a", "1234"));
        assertFalse(db.deleteOwnership("a", "1234"));
        assertTrue(db.deleteOwnership("b", "123"));
        List<Ownership> l = new ArrayList<>();
        l.add(new Ownership("a", "123"));
        assertEquals(l, db.getAllOwnerships());
    }

    @Test
    void changeName_shouldCorrectlyChangeNameInGivenRecords() {
        db.addOwnership("a", "123");
        db.changeName("a", "123", "b");
        List<Ownership> l = new ArrayList<>();
        l.add(new Ownership("b", "123"));
        assertEquals(l, db.getAllOwnerships());
    }

    @Test
    void changePhone_shouldCorrectlyChangePhoneInGivenRecords() {
        db.addOwnership("a", "123");
        db.changePhone("a", "123", "1234");
        List<Ownership> l = new ArrayList<>();
        l.add(new Ownership("a", "1234"));
        assertEquals(l, db.getAllOwnerships());
    }

}