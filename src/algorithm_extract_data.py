import json
import re
from pathlib import Path
from typing import Any


def extract_document_data(text: str) -> dict[str, Any]:
    """Extract document details from recognized text."""
    patterns = {
        "document_number": r"№\s*([\w/-]+)",
        "document_date": r"\b(\d{2}\.\d{2}\.\d{4})\b",
        "amount": r"(?:сумма|итого)\s*[:\-]?\s*(\d+(?:[.,]\d{2})?)",
    }

    result = {}

    for field, pattern in patterns.items():
        match = re.search(pattern, text, re.IGNORECASE)
        result[field] = match.group(1) if match else None

    return result


if __name__ == "__main__":
    data_path = Path(__file__).with_name("test_data.json")

    with data_path.open("r", encoding="utf-8") as file:
        data = json.load(file)

    text = data["sample_document_text"]
    document_data = extract_document_data(text)

    print("Recognized text:", text)
    print("Extracted document data:")

    for field, value in document_data.items():
        print(f"{field}: {value}")
