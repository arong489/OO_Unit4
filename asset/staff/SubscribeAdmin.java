package asset.staff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import asset.currency.Book;
import asset.currency.Record;
import asset.currency.StudentInf;
import asset.facility.DataBase;
import asset.facility.Library;
import asset.facility.LibrarySystem;

public class SubscribeAdmin implements Server {
    private Library library;
    private ArrayList<Book> blockedBooks = new ArrayList<>();

    public SubscribeAdmin(Library library) {
        this.library = library;
    }

    public ArrayList<Book> getBlockedBooks() {
        return blockedBooks;
    }

    public boolean borrow() {
        DataBase dataBase = library.getDataBase();
        LinkedHashMap<Record, String> recordsMap = dataBase.getSubscribeMap();
        Iterator<Record> iterator = recordsMap.keySet().iterator();
        Iterator<Book> bookIterator;
        Record record;
        while (iterator.hasNext()) {
            record = iterator.next();
            Book needBook = record.getBook();
            if (dataBase.getBorrowRecord(record.getStudent(), needBook) != null) {
                iterator.remove();
                continue;
            }
            bookIterator = blockedBooks.iterator();
            Book realBook;
            while (bookIterator.hasNext()) {
                realBook = bookIterator.next();
                if (realBook.equals(needBook)) {
                    bookIterator.remove();
                    System.out.println(String.format(
                            "[%s] ordering librarian lent %s-%s to %s",
                            LibrarySystem.getClock(), library, realBook, record.getStudent()));
                    System.out.println(String.format(
                            "(State)[%s] %s transfers from Available to Borrowed",
                            LibrarySystem.getClock(), realBook));
                    System.out.println(String.format(
                            "(Sequence) [%s] <Library> sends a message to <LibrarySystem>",
                            LibrarySystem.getClock()));
                    System.out.println(
                            String.format("[%s] %s borrowed %s-%s from ordering librarian",
                                    LibrarySystem.getClock(),
                                    record.getStudent(),
                                    library, realBook));
                    dataBase.addBorrowRecord(record.getStudent(), record.getBook());
                    iterator.remove();
                    break;
                }
            }
        }
        return recordsMap.size() == 0;
    }

    public boolean order(StudentInf student, Book book) {
        DataBase dataBase = library.getDataBase();
        int frequency = dataBase.getSubscribeNumber(student, LibrarySystem.getClock());
        if (frequency >= 3) {
            return false;
        }
        if (dataBase.getBorrowRecord(student, book) != null) {
            return false;
        }
        if (dataBase.getSubscribeRecord(student, book) != null) {
            return false;
        }
        System.out.println(String.format(
                "[%s] %s ordered %s-%s from ordering librarian",
                LibrarySystem.getClock(), student, library, book));
        System.out.println(String.format(
                "[%s] ordering librarian recorded %s's order of %s-%s",
                LibrarySystem.getClock(), student, library, book));
        System.out.println(
                String.format("(Sequence) [%s] <LibrarySystem> sends a message to <Library>",
                        LibrarySystem.getClock()));
        if (library.getBookShelf().checkNewBook(book)) {
            book.setOriginLibrary(library);
            dataBase.addWishBook(book);
        }
        dataBase.addSubscribeRecord(student, book);
        return true;
    }
}