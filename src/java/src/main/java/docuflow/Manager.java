package docuflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Manager extends User {

    public Manager(int id, String fullName) {
        super(id, fullName);
    }

    @Override
    public String getRole() {
        return "MANAGER";
    }

    public Map<Integer, Integer> calculateAccountantsLoad(
            List<Accountant> accountants,
            List<AccountantClient> relations
    ) {
        Map<Integer, Integer> loads = new LinkedHashMap<>();

        for (Accountant accountant : accountants) {
            int load = accountant.calculateLoad(relations);
            loads.put(accountant.getId(), load);
        }

        return loads;
    }

    public Accountant chooseAccountant(
            List<Accountant> accountants,
            List<AccountantClient> relations
    ) {
        Map<Integer, Integer> loads =
                calculateAccountantsLoad(accountants, relations);

        Accountant candidate = null;
        int minLoad = Integer.MAX_VALUE;

        for (Accountant accountant : accountants) {
            int load = loads.get(accountant.getId());

            if (load < minLoad) {
                minLoad = load;
                candidate = accountant;
            }
        }

        return candidate;
    }
}