package asset.staff;

import java.util.ArrayList;

import asset.currency.Book;

interface Server {
    public ArrayList<Book> getBlockedBooks();
}
