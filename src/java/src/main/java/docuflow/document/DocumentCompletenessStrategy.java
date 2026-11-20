package docuflow.document;

import java.util.List;

public interface DocumentCompletenessStrategy {
    List<String> findMissingDocuments(
            List<String> requiredDocuments,
            List<String> uploadedDocumentTypes
    );
}
