package asset.staff;

import java.util.ArrayList;

import asset.currency.Book;
import asset.facility.Library;
import asset.facility.LibrarySystem;

public class LogisticsDivision implements Server {
    private Library library;
    private ArrayList<Book> blockedBooks = new ArrayList<>();

    public LogisticsDivision(Library library) {
        this.library = library;
    }

    public void repair(Book book) {
        System.out.println(String.format(
                "[%s] %s-%s got repaired by logistics division in %s", LibrarySystem.getClock(),
                book.getOriginLibrary() != null ? book.getOriginLibrary() : library,
                book, library));
        System.out.println(String.format(
                "(State)[%s] %s transfers from Available to Available",
                LibrarySystem.getClock(), book));
        if (library.equals(book.getOriginLibrary())) {
            blockedBooks.add(book);
        } else {
            library.giveBackInterSchoolBook(book);
        }
    }

    public ArrayList<Book> getBlockedBooks() {
        return blockedBooks;
    }
}
