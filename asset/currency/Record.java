package asset.currency;

public class Record {
    private StudentInf student;
    private Book book;
    private String timeString;
    // false为损坏, true为完好
    private boolean bookState;

    public Record(StudentInf student, Book book, String timeString) {
        this.student = student;
        this.book = book;
        this.timeString = timeString;
        this.bookState = true;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    /**
     * set book state
     * @param bookState "true" refers to well while "false" refers to damaged
    */
    public void setBookState(boolean bookState) {
        this.bookState = bookState;
    }

    public StudentInf getStudent() {
        return student;
    }

    public Book getBook() {
        return book;
    }

    public String getTimeString() {
        return timeString;
    }

    public boolean getBookState() {
        return bookState;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((student == null) ? 0 : student.hashCode());
        result = prime * result + ((book == null) ? 0 : book.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Record other = (Record) obj;
        if (student == null) {
            if (other.student != null) {
                return false;
            }
        } else if (!student.equals(other.student)) {
            return false;
        }
        if (book == null) {
            if (other.book != null) {
                return false;
            }
        } else if (!book.equals(other.book)) {
            return false;
        }
        return true;
    }
}
