package docuflow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import docuflow.document.DocumentCompletenessStrategy;
import docuflow.document.SetBasedDocumentCompleteness;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String inputPath = args.length > 0
                ? args[0]
                : "../test_data.json";

        JsonNode root = mapper.readTree(new File(inputPath));

        List<String> requiredDocuments = new ArrayList<>();

        for (JsonNode document : root.get("required_documents")) {
            requiredDocuments.add(document.asText());
        }

        List<String> uploadedDocumentTypes = new ArrayList<>();

        for (JsonNode document : root.get("uploaded_documents")) {
            JsonNode type = document.get("document_type");

            if (type != null && !type.isNull()) {
                uploadedDocumentTypes.add(type.asText());
            }
        }

        DocumentCompletenessStrategy strategy =
                new SetBasedDocumentCompleteness();

        List<String> missingDocuments =
                strategy.findMissingDocuments(
                        requiredDocuments,
                        uploadedDocumentTypes
                );

        System.out.println("Missing documents: " + missingDocuments);

        List<Accountant> accountants = new ArrayList<>();

        for (JsonNode accountant : root.get("accountants")) {
            accountants.add(
                    new Accountant(
                            accountant.get("id").asInt(),
                            accountant.get("full_name").asText()
                    )
            );
        }

        List<AccountantClient> relations = new ArrayList<>();

        for (JsonNode relation : root.get("accountant_clients")) {
            relations.add(
                    new AccountantClient(
                            relation.get("id").asInt(),
                            relation.get("accountant_id").asInt(),
                            relation.get("organization_id").asInt()
                    )
            );
        }

        Manager manager = new Manager(0, "Менеджер");

        Accountant selectedAccountant =
                manager.chooseAccountant(accountants, relations);

        String accountantName = selectedAccountant == null
                ? "None"
                : selectedAccountant.getFullName();

        System.out.println(
                "Accountant with the smallest workload: " + accountantName
        );
    }
}