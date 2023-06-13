package asset.staff;

import java.util.ArrayList;

import asset.currency.Book;
import asset.currency.Record;
import asset.currency.StudentInf;
import asset.facility.BookShelf;
import asset.facility.DataBase;
import asset.facility.Library;
import asset.facility.LibrarySystem;

public class ServeMachine implements Server {
    private Library library;
    private ArrayList<Book> blockedBooks = new ArrayList<>();

    public ServeMachine(Library library) {
        this.library = library;
    }

    public ArrayList<Book> getBlockedBooks() {
        return blockedBooks;
    }

    public boolean hasBook(StudentInf student, Book book) {
        BookShelf bookShelf = library.getBookShelf();
        System.out.println(
                String.format(
                        "[%s] %s queried %s from self-service machine",
                        LibrarySystem.getClock(), student, book));
        System.out.println(
                String.format("[%s] self-service machine provided information of %s",
                        LibrarySystem.getClock(), book));
        return bookShelf.hasBook(book);
    }

    public boolean borrow(StudentInf student, Book book) {
        DataBase dataBase = library.getDataBase();
        Record record = dataBase.getBorrowRecord(student, book);
        if (record != null) {
            blockedBooks.add(book);
            System.out.println(String.format(
                    "[%s] self-service machine refused lending %s-%s to %s",
                    LibrarySystem.getClock(), library, book, student));
            System.out.println(String.format("(State)[%s] %s transfers from Available to Available",
                    LibrarySystem.getClock(), book));
            return false;
        }
        dataBase.addBorrowRecord(student, book);
        dataBase.removeSubscribeRecord(student, book);
        System.out.println(String.format(
                "[%s] self-service machine lent %s-%s to %s",
                LibrarySystem.getClock(), library, book, student));
        System.out.println(String.format("(State)[%s] %s transfers from Available to Borrowed",
                LibrarySystem.getClock(), book));
        System.out.println(
                String.format("[%s] %s borrowed %s-%s from self-service machine",
                        LibrarySystem.getClock(), student, library, book));
        return true;
    }

    public boolean giveBack(StudentInf student, Book simpleBook) {
        Record record = library.getDataBase().removeBorrowRecord(student, simpleBook);
        if (record != null) {
            Book book = record.getBook();
            if (!record.getBookState()) {
                library.punish(student);
                System.out.println(String.format(
                        "[%s] %s returned %s-%s to self-service machine",
                        LibrarySystem.getClock(),
                        student,
                        book.getOriginLibrary() == null ? library : book.getOriginLibrary(),
                        book));
                System.out.println(String.format(
                        "[%s] self-service machine collected %s-%s from %s",
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
                if (LibrarySystem.timespan(record.getTimeString()) > 60) {
                    library.punish(student);
                }
                System.out.println(String.format(
                        "[%s] %s returned %s-%s to self-service machine",
                        LibrarySystem.getClock(),
                        student,
                        book.getOriginLibrary() == null ? library : book.getOriginLibrary(),
                        book));
                System.out.println(String.format(
                        "[%s] self-service machine collected %s-%s from %s",
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
}