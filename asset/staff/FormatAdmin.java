package asset.staff;

import java.util.ArrayList;
import asset.currency.Book;
import asset.facility.BookShelf;
import asset.facility.Library;

public class FormatAdmin {
    private Library library;

    public FormatAdmin(Library library) {
        this.library = library;
    }

    public boolean deliver() {
        ArrayList<Book> diplomacyBooks = library.getDiplomacyBooks();
        ArrayList<Book> subscribeBooks = library.getSubscribeBooks();
        ArrayList<Book> debitBooks = library.getDebitBooks();
        ArrayList<Book> logisticsBooks = library.getLogisticsBooks();
        ArrayList<Book> serveMachineBooks = library.getServeMachineBooks();
        if ((debitBooks.size() + logisticsBooks.size() +
                serveMachineBooks.size() + diplomacyBooks.size()) == 0) {
            return false;
        }
        subscribeBooks.addAll(diplomacyBooks);
        subscribeBooks.addAll(serveMachineBooks);
        subscribeBooks.addAll(debitBooks);
        subscribeBooks.addAll(logisticsBooks);
        diplomacyBooks.clear();
        serveMachineBooks.clear();
        debitBooks.clear();
        logisticsBooks.clear();
        return true;
    }

    public void backShelf() {
        BookShelf bookShelf = library.getBookShelf();
        ArrayList<Book> subscribeBooks = library.getSubscribeBooks();
        subscribeBooks.forEach((book) -> {
            bookShelf.addBook(book);
        });
        subscribeBooks.clear();
    }
}
