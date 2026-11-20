package docuflow;

public abstract class User {
    private final int id;
    private final String fullName;

    public User(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public abstract String getRole();
}