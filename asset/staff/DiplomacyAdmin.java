package asset.staff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import asset.currency.Book;
import asset.currency.Record;
import asset.currency.StudentInf;
import asset.facility.BookShelf;
import asset.facility.DataBase;
import asset.facility.Library;
import asset.facility.LibrarySystem;

public class DiplomacyAdmin implements Server {

    private Library library;
    // returned book
    private ArrayList<Book> blockedBooks = new ArrayList<>();
    private ArrayList<Book> returnGetBuffer = new ArrayList<>();
    // interSchool return Books(wait to return to another library)
    private ArrayList<Book> returnSendBuffer = new ArrayList<>();

    public DiplomacyAdmin(Library library) {
        this.library = library;
    }

    public void purchaseNewBook() {
        LinkedHashMap<Book, DataBase.Num> wishMap = library.getDataBase().getWishBooks();
        BookShelf bookShelf = library.getBookShelf();
        wishMap.forEach((book, num) -> {
            for (int i = num.getValue(); i > 0; i--) {
                blockedBooks.add(book);
            }
            System.out.println(String.format(
                    "[%s] %s-%s got purchased by purchasing department in %s",
                    LibrarySystem.getClock(), library, book, library));
            LibrarySystem.addInterSchoolBookInf(book);
            bookShelf.addNewBook(book, num.getValue(), true);
        });
        wishMap.clear();
    }

    public void infReceivedBook() {
        for (Book book : returnGetBuffer) {
            System.out.println(String.format(
                    "[%s] %s-%s got received by purchasing department in %s",
                    LibrarySystem.getClock(), library, book, library));
            System.out.println(String.format(
                    "(State)[%s] %s transfers from Blocked to Available",
                    LibrarySystem.getClock(), book));
        }
        blockedBooks.addAll(returnGetBuffer);
        returnGetBuffer.clear();
        LinkedHashSet<Record> interSchoolBorrowRecvSet;
        interSchoolBorrowRecvSet = library.getDataBase().getInterSchoolBorrowRecvSet();
        for (Record record : interSchoolBorrowRecvSet) {
            Book receiveBook = record.getBook();
            System.out.println(String.format(
                    "[%s] %s-%s got received by purchasing department in %s",
                    LibrarySystem.getClock(),
                    receiveBook.getOriginLibrary(),
                    receiveBook,
                    library));
            System.out.println(String.format(
                    "(State)[%s] %s transfers from Blocked to Available",
                    LibrarySystem.getClock(), record.getBook()));
        }
    }

    public void borrow() {
        LinkedHashSet<Record> interSchoolBorrowRecvSet;
        interSchoolBorrowRecvSet = library.getDataBase().getInterSchoolBorrowRecvSet();
        DataBase dataBase = library.getDataBase();
        interSchoolBorrowRecvSet.forEach((record) -> {
            StudentInf student = record.getStudent();
            Book book = record.getBook();
            dataBase.addBorrowRecord(student, book);
            dataBase.removeSubscribeRecord(student, book);
            System.out.println(String.format(
                    "[%s] purchasing department lent %s-%s to %s",
                    LibrarySystem.getClock(), book.getOriginLibrary(), book, student));
            System.out.println(String.format("(State)[%s] %s transfers from Available to Borrowed",
                    LibrarySystem.getClock(), book));
            System.out.println(
                    String.format("(Sequence) [%s] <Library> sends a message to <LibrarySystem>",
                            LibrarySystem.getClock()));
            System.out.println(
                    String.format("[%s] %s borrowed %s-%s from purchasing department",
                            LibrarySystem.getClock(), student, book.getOriginLibrary(), book));
        });
        interSchoolBorrowRecvSet.clear();
    }

    public void deliver() {
        returnSendBuffer.forEach((book) -> {
            book.getOriginLibrary().receiveInterSchoolReturnedBook(book);
            System.out.println(String.format(
                    "[%s] %s-%s got transported by purchasing department in %s",
                    LibrarySystem.getClock(), book.getOriginLibrary(), book, library));
            System.out.println(String.format(
                    "(State)[%s] %s transfers from Available to Blocked",
                    LibrarySystem.getClock(), book));
        });
        returnSendBuffer.clear();

        LinkedHashSet<Record> interSchoolBorrowSendSet;
        interSchoolBorrowSendSet = library.getDataBase().getInterSchoolBorrowSendSet();
        interSchoolBorrowSendSet.forEach((record) -> {
            record.getStudent().getLibrary().getDataBase().addInterSchoolRecvRecord(record);
            System.out.println(String.format(
                    "[%s] %s-%s got transported by purchasing department in %s",
                    LibrarySystem.getClock(), library, record.getBook(), library));
            System.out.println(String.format(
                    "(State)[%s] %s transfers from Available to Blocked",
                    LibrarySystem.getClock(), record.getBook()));
        });
        interSchoolBorrowSendSet.clear();
    }

    public boolean order(StudentInf studentInf, Book book) {
        if (library.getBookShelf().hasBook(book)) {
            if (studentInf.getLibrary().getDataBase().getBorrowRecord(studentInf, book) != null ||
                    !LibrarySystem.addPreBorrow(studentInf, book)) {
                return true;
            }
            book.setOriginLibrary(library);
            if (library.getDataBase().addInterSchoolSendRecord(studentInf, book)) {
                library.getBookShelf().getBook(book);
            }
            return true;
        } else {
            return false;
        }
    }

    public void giveBackInterSchoolBook(Book book) {
        returnSendBuffer.add(book);
    }

    public void receiveInterSchoolReturnedBook(Book book) {
        returnGetBuffer.add(book);
    }

    @Override
    public ArrayList<Book> getBlockedBooks() {
        return blockedBooks;
    }
}
