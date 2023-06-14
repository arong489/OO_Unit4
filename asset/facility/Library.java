package asset.facility;

import java.util.ArrayList;

import asset.currency.Book;
import asset.currency.Order;
import asset.currency.StudentInf;
import asset.staff.DebitAdmin;
import asset.staff.DiplomacyAdmin;
import asset.staff.FormatAdmin;
import asset.staff.LogisticsDivision;
import asset.staff.ServeMachine;
import asset.staff.SubscribeAdmin;

public class Library {
    private String name;
    // two device
    private BookShelf bookShelf;
    private DataBase dataBase = new DataBase();
    // staff
    private DebitAdmin debitAdmin = new DebitAdmin(this);
    private ServeMachine serveMachine = new ServeMachine(this);
    private SubscribeAdmin subscribeAdmin = new SubscribeAdmin(this);
    private FormatAdmin formatAdmin = new FormatAdmin(this);
    private LogisticsDivision logisticsDivision = new LogisticsDivision(this);
    private DiplomacyAdmin diplomacyAdmin = new DiplomacyAdmin(this);

    public Library(String name, BookShelf bookShelf) {
        this.name = name;
        this.bookShelf = bookShelf;
    }

    /**
     * check borrow
     * @param order
     * @return true refers to success; false refers to wait; null refers to 
    */
    private boolean tryBorrow(Order order) {
        StudentInf student = order.getStudent();
        Book book = order.getBook();
        if (student.getLibrary().equals(this)) {
            if (serveMachine.hasBook(student, book)) {
                bookShelf.getBook(book);
                if (book.getType().equals("B")) {
                    debitAdmin.borrow(student, book);
                } else if (book.getType().equals("C")) {
                    serveMachine.borrow(student, book);
                }
                return true;
            }
        }
        return false;
    }

    private void smeared(Order order) {
        dataBase.changeBorrowRecord(order.getStudent(), order.getBook(), false);
    }

    private void lost(Order order) {
        dataBase.removeBorrowRecord(order.getStudent(), order.getBook());
        bookShelf.lostBook(order.getBook());
        debitAdmin.punish(order.getStudent());
    }

    private void giveBack(Order order) {
        Book book = order.getBook();
        if (book.getType().equals("B")) {
            debitAdmin.giveBack(order.getStudent(), book);
        } else if (book.getType().equals("C")) {
            serveMachine.giveBack(order.getStudent(), book);
        }
    }

    public boolean tryExcute(Order order) {
        if (!order.getLibrary().equals(this)) {
            return false;
        }
        if (order.getOperation().equals("borrowed")) {
            return tryBorrow(order);
        } else if (order.getOperation().equals("smeared")) {
            smeared(order);
            return true;
        } else if (order.getOperation().equals("lost")) {
            lost(order);
            return true;
        } else if (order.getOperation().equals("returned")) {
            giveBack(order);
            return true;
        }
        return false;
    }

    public void receiveBook() {
        diplomacyAdmin.infReceivedBook();
    }

    public void interBorrow() {
        diplomacyAdmin.borrow();
    }

    public void deliverBook() {
        diplomacyAdmin.deliver();
    }

    public void purchaseNewBook() {
        diplomacyAdmin.purchaseNewBook();
    }

    public void formatBook() {
        formatAdmin.deliver();
    }

    public boolean orderBorrow() {
        return subscribeAdmin.borrow();
    }

    public void backShelf() {
        formatAdmin.backShelf();
    }

    public Boolean reExcute(Order order) {
        if (order.getLibrary().equals(this) ||
                !order.getOperation().equals("borrowed")) {
            return null;
        }
        return diplomacyAdmin.order(order.getStudent(), order.getBook());
    }

    public void orderNewBook() {
    }

    //============================== facility interface ================================

    public BookShelf getBookShelf() {
        return bookShelf;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    // LogisticsDivision
    public void repair(Book book) {
        logisticsDivision.repair(book);
    }

    // Debit Administrator
    public void punish(StudentInf student) {
        debitAdmin.punish(student);
    }

    // Diplomacy Administrator
    public void giveBackInterSchoolBook(Book book) {
        diplomacyAdmin.giveBackInterSchoolBook(book);
    }

    public void receiveInterSchoolReturnedBook(Book book) {
        diplomacyAdmin.receiveInterSchoolReturnedBook(book);
    }

    // Subscribe Administrator
    public void localOrder(StudentInf student, Book book) {
        subscribeAdmin.order(student, book);
    }

    public ArrayList<Book> getDebitBooks() {
        return debitAdmin.getBlockedBooks();
    }

    public ArrayList<Book> getServeMachineBooks() {
        return serveMachine.getBlockedBooks();
    }

    public ArrayList<Book> getLogisticsBooks() {
        return logisticsDivision.getBlockedBooks();
    }

    public ArrayList<Book> getSubscribeBooks() {
        return subscribeAdmin.getBlockedBooks();
    }

    public ArrayList<Book> getDiplomacyBooks() {
        return diplomacyAdmin.getBlockedBooks();
    }

    //======================== 23-OO-Unit4 is a cur =========================

    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj || obj == null) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Library other = (Library) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
