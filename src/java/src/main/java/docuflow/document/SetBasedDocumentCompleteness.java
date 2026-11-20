package docuflow.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetBasedDocumentCompleteness
        implements DocumentCompletenessStrategy {

    @Override
    public List<String> findMissingDocuments(
            List<String> requiredDocuments,
            List<String> uploadedDocumentTypes
    ) {
        Set<String> uploadedTypes = new HashSet<>();

        for (String type : uploadedDocumentTypes) {
            if (type != null && !type.isEmpty()) {
                uploadedTypes.add(type);
            }
        }

        List<String> missingDocuments = new ArrayList<>();

        for (String requiredType : requiredDocuments) {
            if (!uploadedTypes.contains(requiredType)) {
                missingDocuments.add(requiredType);
            }
        }

        return missingDocuments;
    }
}