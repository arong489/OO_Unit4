package asset.currency;

import asset.facility.Library;

public class Book {
    private String type;
    private String serial;
    private Library originLibrary;

    public Book(String serialString) {
        String[] strings = serialString.trim().split("-");;
        this.type = strings[0];
        this.serial = strings[1];
        this.originLibrary = null;
    }

    public Book(String serialString, Library library) {
        String[] strings = serialString.trim().split("-");;
        this.type = strings[0];
        this.serial = strings[1];
        this.originLibrary = library;
    }
    
    public Library getOriginLibrary() {
        return originLibrary;
    }

    public void setOriginLibrary(Library originLibrary) {
        this.originLibrary = originLibrary;
    }

    @Override
    public String toString() {
        return this.type + "-" + this.serial;
    }

    public String getType() {
        return type;
    }

    public String getSerial() {
        return serial;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((serial == null) ? 0 : serial.hashCode());
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
        Book other = (Book) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (serial == null) {
            if (other.serial != null) {
                return false;
            }
        } else if (!serial.equals(other.serial)) {
            return false;
        }
        return true;
    }
}