package asset.facility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import asset.currency.Book;
import asset.currency.Order;
import asset.currency.StudentInf;

public class LibrarySystem {
    private static LinkedHashMap<String, Library> libraryMap = new LinkedHashMap<>();
    private static String clock = "2023-01-01";
    private static HashMap<Book, HashSet<Library>> globalBookInf = new HashMap<>();
    private static HashMap<StudentInf, HashSet<Book>> globalPreBorrow = new HashMap<>();

    private LocalDate preDate = LocalDate.parse("2023-01-01");
    private LinkedHashMap<Library, ArrayList<Order>> waitMap = new LinkedHashMap<>();

    public LibrarySystem() {
    }

    public void closeDoor() {
        for (ArrayList<Order> waitList : waitMap.values()) {
            for (Order waitOrder : waitList) {
                boolean localborrow = true;
                Library library = waitOrder.getLibrary();
                HashSet<Library> borrowLibraries = globalBookInf.get(waitOrder.getBook());
                if (borrowLibraries != null) {
                    for (Library borrowLibrary : borrowLibraries) {
                        if (!borrowLibrary.equals(library)) {
                            Boolean interSchoolAsk = borrowLibrary.reExcute(waitOrder);
                            if (interSchoolAsk != null && interSchoolAsk) {
                                localborrow = false;
                                break;
                            }
                        }
                    }
                }
                if (localborrow) {
                    library.localOrder(waitOrder.getStudent(), waitOrder.getBook());
                }
            }
            waitList.clear();
        }
        globalPreBorrow.clear();
        for (Library library : libraryMap.values()) {
            library.deliverBook();
        }
    }

    private void tryCloseDoor(String curClock) {
        if (!curClock.equals(clock)) {
            closeDoor();
            clock = getTomorrow();
            for (Library library : libraryMap.values()) {
                library.receiveBook();
            }
            for (Library library : libraryMap.values()) {
                library.interBorrow();
            }
        }
    }

    private String getTomorrow() {
        return LocalDate.parse(clock).plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private boolean checkFormatDay(String curClock) {
        LocalDate curDate = null;
        curDate = LocalDate.parse(curClock);
        long days = preDate.until(curDate, ChronoUnit.DAYS);
        if (days >= 0) {
            LibrarySystem.clock = preDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.preDate = preDate.plusDays(3);
            return true;
        } else {
            clock = curClock;
            return false;
        }
    }

    private void tryFormatDay(String curTime) {
        if (checkFormatDay(clock) || checkFormatDay(curTime)) {
            for (Library library : libraryMap.values()) {
                library.purchaseNewBook();
            }
            System.out.println(String.format(
                    "[%s] arranging librarian arranged all the books",
                    clock));
            for (Library library : libraryMap.values()) {
                library.formatBook();
                library.orderBorrow();
                library.backShelf();
            }
            while (checkFormatDay(curTime)) {
                System.out.println(String.format(
                        "[%s] arranging librarian arranged all the books",
                        clock));
            }
        }
    }

    public void excute(Order order) {
        tryCloseDoor(order.getTime());
        tryFormatDay(order.getTime());
        if (!order.getLibrary().tryExcute(order)) {
            waitMap.get(order.getLibrary()).add(order);
        }
    }

    //====================== facility ===========================

    public static Library getLibrary(String school) {
        return libraryMap.get(school);
    }

    public static String getClock() {
        return clock;
    }

    //=========================/_\ break line /_\============================

    public Library addLibrary(String name, BookShelf bookShelf) {
        if (libraryMap.containsKey(name)) {
            return null;
        } else {
            Library library = new Library(name, bookShelf);
            waitMap.put(library, new ArrayList<>());
            libraryMap.put(name, library);
            return library;
        }
    }

    public static boolean addInterSchoolBookInf(Book book) {
        HashSet<Library> libraries = globalBookInf.get(book);
        if (libraries == null) {
            libraries = new HashSet<>();
            globalBookInf.put(book, libraries);
            libraries.add(book.getOriginLibrary());
            return true;
        }
        return libraries.add(book.getOriginLibrary());
    }

    public static boolean removeInterSchoolBookInf(Book book) {
        HashSet<Library> libraries = globalBookInf.get(book);
        if (libraries != null) {
            return libraries.remove(book.getOriginLibrary());
        }
        return false;
    }

    public static boolean addPreBorrow(StudentInf student, Book book) {
        HashSet<Book> books = globalPreBorrow.get(student);
        if (books == null) {
            books = new HashSet<>();
            books.add(book);
            globalPreBorrow.put(student, books);
            return true;
        } else {
            if (book.getType().equals("B")) {
                for (Book book2 : books) {
                    if (book2.getType().equals("B")) {
                        return false;
                    }
                }
                books.add(book);
                return true;
            } else {
                return books.add(book);
            }
        }
    }

    public static long timespan(String time) {
        return LocalDate.parse(time).until(LocalDate.parse(clock), ChronoUnit.DAYS);
    }
}