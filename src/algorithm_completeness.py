import json
from pathlib import Path
from typing import Any


def check_completeness(
    required_documents: list[str],
    uploaded_documents: list[dict[str, Any]],
) -> list[str]:
    """Return the list of required document types that are missing."""
    uploaded_types = {
        document["document_type"]
        for document in uploaded_documents
        if document.get("document_type")
    }

    missing_documents = []

    for required_type in required_documents:
        if required_type not in uploaded_types:
            missing_documents.append(required_type)

    return missing_documents


if __name__ == "__main__":
    data_path = Path(__file__).with_name("test_data.json")

    with data_path.open("r", encoding="utf-8") as file:
        data = json.load(file)

    result = check_completeness(
        data["required_documents"],
        data["uploaded_documents"],
    )

    print("Missing documents:", result if result else "none")
