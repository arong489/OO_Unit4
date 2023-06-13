package asset.currency;

import asset.facility.Library;
import asset.facility.LibrarySystem;

public class StudentInf {
    private Library library;
    private String id;

    public StudentInf(String input) {
        String[] inputs = input.trim().split("-");
        this.library = LibrarySystem.getLibrary(inputs[0]);
        this.id = inputs[1];
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return this.library + "-" + this.id;
    }

    public Library getLibrary() {
        return library;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((library == null) ? 0 : library.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        StudentInf other = (StudentInf) obj;
        if (library == null) {
            if (other.library != null) {
                return false;
            }
        } else if (!library.equals(other.library)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}