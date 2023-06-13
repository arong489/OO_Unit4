package asset.facility;

import java.util.HashMap;

import asset.currency.Book;

public class BookShelf {

    private class BookInf {
        private int maxNumber;
        private int curNumber = 0;
        private boolean acceptInterSchoolBorrow;

        public BookInf(int maxNumber, boolean acceptInterSchoolBorrow) {
            this.maxNumber = maxNumber;
            this.acceptInterSchoolBorrow = acceptInterSchoolBorrow;
        }
    }

    private HashMap<Book, BookInf> bookMap = new HashMap<>();

    public boolean initailBook(Book book, int maxNumber, boolean permission) {
        if (bookMap.containsKey(book)) {
            return false;
        }
        BookInf bookInf = new BookInf(maxNumber, permission);
        bookMap.put(book, bookInf);
        bookInf.curNumber += maxNumber;
        if (permission) {
            LibrarySystem.addInterSchoolBookInf(book);
        }
        return true;
    }

    public boolean addNewBook(Book book, int maxNumber, boolean permission) {
        if (bookMap.containsKey(book)) {
            return false;
        }
        bookMap.put(book, new BookInf(maxNumber, permission));
        if (permission) {
            LibrarySystem.addInterSchoolBookInf(book);
        }
        return true;
    }

    public boolean lostBook(Book book) {
        BookInf bookInf = bookMap.get(book);
        if (bookInf != null) {
            if (bookInf.maxNumber == 1) {
                bookMap.remove(book);
                if (bookInf.acceptInterSchoolBorrow) {
                    LibrarySystem.removeInterSchoolBookInf(book);
                }
            } else {
                bookInf.maxNumber--;
            }
            return true;
        }
        return false;
    }

    public boolean hasBook(Book book) {
        BookInf bookInf = bookMap.get(book);
        if (bookInf == null) {
            return false;
        } else {
            return bookInf.curNumber > 0;
        }
    }

    public boolean checkNewBook(Book book) {
        return bookMap.get(book) == null;
    }

    public boolean addBook(Book book) {
        BookInf bookInf = bookMap.get(book);
        if (bookInf != null) {
            bookInf.curNumber++;
            return true;
        }
        return false;
    }

    public Book getBook(Book book) {
        BookInf bookInf = bookMap.get(book);
        if (bookInf != null && bookInf.curNumber > 0) {
            bookInf.curNumber--;
            return book;
        }
        return null;
    }

    public boolean ifAcceptInterSchoolBorrow(Book book) {
        BookInf bookInf = bookMap.get(book);
        if (bookInf != null) {
            return bookInf.acceptInterSchoolBorrow;
        }
        return false;
    }
}