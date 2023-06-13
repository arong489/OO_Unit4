import java.util.Scanner;

import asset.currency.Book;
import asset.currency.Order;
import asset.facility.BookShelf;
import asset.facility.Library;
import asset.facility.LibrarySystem;

public class MainClass {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibrarySystem librarySystem = new LibrarySystem();

        int n = scanner.nextInt();
        String input;
        String[] inputs;
        String schoolName;
        int bookline;
        BookShelf bookShelf;
        Library library;
        for (int i = 0; i < n; i++) {
            do {
                input = scanner.nextLine();
            } while (input.equals(""));
            inputs = input.trim().split(" ");
            schoolName = inputs[0];
            bookline = Integer.parseInt(inputs[1]);
            bookShelf = new BookShelf();
            library = librarySystem.addLibrary(schoolName, bookShelf);
            for (int j = 0; j < bookline; j++) {
                do {
                    input = scanner.nextLine();
                } while (input.equals(""));
                inputs = input.trim().split(" ");
                Book book = new Book(inputs[0], library);
                int bookNumber = Integer.parseInt(inputs[1]);
                bookShelf.initailBook(book, bookNumber, inputs[2].equals("Y"));
            }
        }
        n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            do {
                input = scanner.nextLine();
            } while (input.equals(""));
            librarySystem.excute(new Order(input));
        }
        scanner.close();
        librarySystem.closeDoor();
        // librarySystem.excute(new Order("[2024-01-01] null-114514 halt A-1919810"));
    }
}
