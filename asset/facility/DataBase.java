package asset.facility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import asset.currency.Book;
import asset.currency.Record;
import asset.currency.StudentInf;

public class DataBase {
    private class SubscribeTitle {
        private StudentInf student;
        private String date;

        public SubscribeTitle(StudentInf student, String date) {
            this.student = student;
            this.date = date;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((student == null) ? 0 : student.hashCode());
            result = prime * result + ((date == null) ? 0 : date.hashCode());
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
            SubscribeTitle other = (SubscribeTitle) obj;
            if (student == null) {
                if (other.student != null) {
                    return false;
                }
            } else if (!student.equals(other.student)) {
                return false;
            }
            if (date == null) {
                if (other.date != null) {
                    return false;
                }
            } else if (!date.equals(other.date)) {
                return false;
            }
            return true;
        }
    }

    public class Num {
        private int value;

        public int getValue() {
            return value < 3 ? 3 : value;
        }

        public Num(int value) {
            this.value = value;
        }
    }

    private HashMap<StudentInf, Record> btypeBorrowMap = new HashMap<>();
    private HashMap<StudentInf, HashMap<Book, Record>> ctypeBorrowMap = new HashMap<>();

    private LinkedHashMap<Record, String> subscribeMap = new LinkedHashMap<>();
    private HashMap<SubscribeTitle, Num> subscribeTimeMap = new HashMap<>();
    private LinkedHashMap<Book, Num> wishMap = new LinkedHashMap<>();

    // interSchool borrow Books(send to another library)
    private LinkedHashSet<Record> interSchoolBorrowSendSet = new LinkedHashSet<>();
    // interSchool borrow Books(receive from another library)
    private LinkedHashSet<Record> interSchoolBorrowRecvSet = new LinkedHashSet<>();

    public LinkedHashMap<Record, String> getSubscribeMap() {
        return subscribeMap;
    }

    public Record getBorrowRecord(StudentInf student, Book book) {
        if (book.getType().equals("B")) {
            return btypeBorrowMap.get(student);
        } else {
            HashMap<Book, Record> personalData = ctypeBorrowMap.get(student);
            if (personalData != null) {
                return personalData.get(book);
            }
            return null;
        }
    }

    public boolean addBorrowRecord(StudentInf student, Book book) {
        if (book.getType().equals("B")) {
            if (btypeBorrowMap.containsKey(student)) {
                return false;
            }
            return btypeBorrowMap.put(
                    student, new Record(student, book, LibrarySystem.getClock())) == null;
        } else {
            HashMap<Book, Record> personalData = ctypeBorrowMap.get(student);
            if (personalData == null) {
                personalData = new HashMap<>();
                personalData.put(book, new Record(student, book, LibrarySystem.getClock()));
                ctypeBorrowMap.put(student, personalData);
                return true;
            } else {
                if (personalData.containsKey(book)) {
                    return false;
                }
                return personalData.put(
                        book, new Record(student, book, LibrarySystem.getClock())) == null;
            }
        }
    }

    public Record removeBorrowRecord(StudentInf student, Book book) {
        if (book.getType().equals("B")) {
            return btypeBorrowMap.remove(student);
        } else {
            HashMap<Book, Record> personalData = ctypeBorrowMap.get(student);
            if (personalData != null) {
                return personalData.remove(book);
            }
            return null;
        }
    }

    public boolean changeBorrowRecord(StudentInf student, Book book, boolean state) {
        Record temp = getBorrowRecord(student, book);
        if (temp != null) {
            temp.setBookState(state);
            return true;
        }
        return false;
    }

    public Record getSubscribeRecord(StudentInf student, Book book) {
        Record tempRecord = new Record(student, book, null);
        String timeString = subscribeMap.get(tempRecord);
        if (timeString != null) {
            tempRecord.setTimeString(timeString);
            return tempRecord;
        }
        return null;
    }

    public int getSubscribeNumber(StudentInf student, String timeString) {
        SubscribeTitle tempTitle = new SubscribeTitle(student, timeString);
        Num tempNum = subscribeTimeMap.get(tempTitle);
        if (tempNum != null) {
            return tempNum.value;
        }
        return 0;
    }

    public boolean addSubscribeRecord(StudentInf student, Book book) {
        Record tempRecord = new Record(student, book, LibrarySystem.getClock());
        if (subscribeMap.containsKey(tempRecord)) {
            return false;
        }
        subscribeMap.put(tempRecord, LibrarySystem.getClock());
        SubscribeTitle tempTitle = new SubscribeTitle(student, LibrarySystem.getClock());
        Num tempNum = subscribeTimeMap.get(tempTitle);
        if (tempNum == null) {
            tempNum = new Num(1);
            subscribeTimeMap.put(tempTitle, tempNum);
        } else {
            tempNum.value++;
        }
        return true;
    }

    private void removeWishBook(Book book) {
        Num tempNum = wishMap.get(book);
        if (tempNum != null) {
            if (tempNum.value == 1) {
                wishMap.remove(book);
            } else {
                tempNum.value--;
            }
        }
    }

    public boolean removeSubscribeRecord(StudentInf student, Book book) {
        boolean feedback = false;
        if (book.getType().equals("B")) {
            Iterator<Record> iterator = subscribeMap.keySet().iterator();
            Record record;
            Num num;
            SubscribeTitle subscribeTitle;
            while (iterator.hasNext()) {
                record = iterator.next();
                if (record.getBook().getType().equals("B") && record.getStudent().equals(student)) {
                    iterator.remove();
                    removeWishBook(record.getBook());
                    feedback = true;
                    subscribeTitle = new SubscribeTitle(student, record.getTimeString());
                    num = subscribeTimeMap.get(subscribeTitle);
                    if (num.value == 1) {
                        subscribeTimeMap.remove(subscribeTitle);
                    } else {
                        num.value--;
                    }
                }
            }
        } else {
            String timeString = subscribeMap.remove(new Record(student, book, null));
            SubscribeTitle subscribeTitle;
            Num num;
            if (timeString != null) {
                feedback = true;
                subscribeTitle = new SubscribeTitle(student, timeString);
                num = subscribeTimeMap.get(subscribeTitle);
                removeWishBook(book);
                if (num.value == 1) {
                    subscribeTimeMap.remove(subscribeTitle);
                } else {
                    num.value--;
                }
            }
        }
        return feedback;
    }

    public void addWishBook(Book book) {
        Num tempNum = wishMap.get(book);
        if (tempNum == null) {
            tempNum = new Num(1);
            wishMap.put(book, tempNum);
        } else {
            tempNum.value++;
        }
    }

    public LinkedHashMap<Book, Num> getWishBooks() {
        return this.wishMap;
    }

    public boolean addInterSchoolSendRecord(StudentInf student, Book book) {
        return interSchoolBorrowSendSet.add(new Record(student, book, LibrarySystem.getClock()));
    }

    public boolean addInterSchoolRecvRecord(Record record) {
        return interSchoolBorrowRecvSet.add(record);
    }

    public LinkedHashSet<Record> getInterSchoolBorrowSendSet() {
        return interSchoolBorrowSendSet;
    }

    public LinkedHashSet<Record> getInterSchoolBorrowRecvSet() {
        return interSchoolBorrowRecvSet;
    }
}
