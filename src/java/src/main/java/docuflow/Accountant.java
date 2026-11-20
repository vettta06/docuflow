package docuflow;

import java.util.List;

public class Accountant extends User {

    public Accountant(int id, String fullName) {
        super(id, fullName);
    }

    @Override
    public String getRole() {
        return "ACCOUNTANT";
    }

    public int calculateLoad(List<AccountantClient> relations) {
        int load = 0;

        for (AccountantClient relation : relations) {
            if (relation.getAccountantId() == getId()) {
                load++;
            }
        }

        return load;
    }
}