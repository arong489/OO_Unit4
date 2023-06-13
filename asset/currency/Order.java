package asset.currency;

import asset.facility.Library;

public class Order {
    private String time;
    private StudentInf student;
    private String operation;
    private Book book;

    public Order(String input) {
        String[] inputs = input.trim().split(" ");
        this.time = inputs[0].substring(1, inputs[0].length() - 1);
        this.student = new StudentInf(inputs[1]);
        this.operation = inputs[2];
        this.book = new Book(inputs[3]);
    }

    public String getTime() {
        return time;
    }

    public StudentInf getStudent() {
        return student;
    }

    public String getOperation() {
        return operation;
    }

    public Book getBook() {
        return book;
    }

    public Library getLibrary() {
        return student.getLibrary();
    }
}
