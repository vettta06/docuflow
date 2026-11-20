package docuflow;

public class AccountantClient {
    private final int id;
    private final int accountantId;
    private final int organizationId;

    public AccountantClient(int id, int accountantId, int organizationId) {
        this.id = id;
        this.accountantId = accountantId;
        this.organizationId = organizationId;
    }

    public int getId() {
        return id;
    }

    public int getAccountantId() {
        return accountantId;
    }

    public int getOrganizationId() {
        return organizationId;
    }
}