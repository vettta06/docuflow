package docuflow.document;

import java.util.ArrayList;
import java.util.List;

public class ListBasedDocumentCompleteness
        implements DocumentCompletenessStrategy {

    @Override
    public List<String> findMissingDocuments(
            List<String> requiredDocuments,
            List<String> uploadedDocumentTypes
    ) {
        List<String> uploadedTypes = new ArrayList<>();

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