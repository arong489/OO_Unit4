package asset.staff;

import java.util.ArrayList;

import asset.currency.Book;
import asset.currency.Record;
import asset.currency.StudentInf;
import asset.facility.DataBase;
import asset.facility.Library;
import asset.facility.LibrarySystem;

public class DebitAdmin implements Server {
    private Library library;
    private ArrayList<Book> blockedBooks = new ArrayList<>();

    public DebitAdmin(Library library) {
        this.library = library;
    }

    public ArrayList<Book> getBlockedBooks() {
        return blockedBooks;
    }

    public boolean borrow(StudentInf student, Book book) {
        DataBase dataBase = library.getDataBase();
        Record record = dataBase.getBorrowRecord(student, book);
        if (record != null) {
            blockedBooks.add(book);
            System.out.println(String.format(
                    "[%s] borrowing and returning librarian refused lending %s-%s to %s",
                    LibrarySystem.getClock(), library, book, student));
            System.out.println(String.format("(State)[%s] %s transfers from Available to Available",
                    LibrarySystem.getClock(), book));
            System.out.println(
                    String.format("(Sequence) [%s] <Library> sends a message to <LibrarySystem>",
                            LibrarySystem.getClock()));
            return false;
        }
        dataBase.addBorrowRecord(student, book);
        dataBase.removeSubscribeRecord(student, book);
        System.out.println(String.format(
                "[%s] borrowing and returning librarian lent %s-%s to %s",
                LibrarySystem.getClock(), library, book, student));
        System.out.println(String.format("(State)[%s] %s transfers from Available to Borrowed",
                LibrarySystem.getClock(), book));
        System.out.println(
                String.format("[%s] %s borrowed %s-%s from borrowing and returning librarian",
                        LibrarySystem.getClock(), student, library, book));
        return true;
    }

    public boolean giveBack(StudentInf student, Book simpleBook) {
        DataBase dataBase = library.getDataBase();
        Record record = dataBase.removeBorrowRecord(student, simpleBook);
        if (record != null) {
            Book book = record.getBook();
            if (!record.getBookState()) {
                punish(student);
                System.out.println(String.format(
                        "[%s] %s returned %s-%s to borrowing and returning librarian",
                        LibrarySystem.getClock(), student,
                        book.getOriginLibrary() == null ? library : book.getOriginLibrary(),
                        book));
                System.out.println(String.format(
                        "[%s] borrowing and returning librarian collected %s-%s from %s",
                        LibrarySystem.getClock(),
                        book.getOriginLibrary() == null ? library : book.getOriginLibrary(),
                        book, student));
                System.out.println(String.format(
                        "(State)[%s] %s transfers from Borrowed to Available",
                        LibrarySystem.getClock(), book));
                library.repair(book);
            } else {
                if (library.equals(book.getOriginLibrary())) {
                    blockedBooks.add(book);
                } else {
                    library.giveBackInterSchoolBook(book);
                }
                if (LibrarySystem.timespan(record.getTimeString()) > 30) {
                    punish(student);
                }
                System.out.println(String.format(
                        "[%s] %s returned %s-%s to borrowing and returning librarian",
                        LibrarySystem.getClock(), student,
                        book.getOriginLibrary() == null ? library : book.getOriginLibrary(),
                        book));
                System.out.println(String.format(
                        "[%s] borrowing and returning librarian collected %s-%s from %s",
                        LibrarySystem.getClock(),
                        book.getOriginLibrary() == null ? library : book.getOriginLibrary(),
                        book, student));
                System.out.println(String.format(
                        "(State)[%s] %s transfers from Borrowed to Available",
                        LibrarySystem.getClock(), book));
            }
            return true;
        }
        return false;
    }

    public void punish(StudentInf student) {
        System.out.println(String.format(
                "[%s] %s got punished by borrowing and returning librarian",
                LibrarySystem.getClock(), student));
        System.out.println(String.format(
                "[%s] borrowing and returning librarian received %s's fine",
                LibrarySystem.getClock(), student));
    }
}
