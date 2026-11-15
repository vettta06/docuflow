def determine_document_type(text: str) -> str:
    """Determine the document type based on recognized text."""
    text = text.lower()

    keywords = {
        "INVOICE": ["счет", "счёт", "счет-фактура"],
        "ACT": ["акт", "выполненных работ", "оказанных услуг"],
        "CONTRACT": ["договор", "контракт", "соглашение"],
        "PAYMENT_CONFIRMATION": [
            "платежное поручение",
            "подтверждение оплаты",
            "оплачено",
        ],
        "PAYROLL": ["ведомость", "заработная плата", "зарплата"],
        "RECEIPT": ["квитанция", "кассовый чек", "чек"],
    }

    for document_type, words in keywords.items():
        if any(word in text for word in words):
            return document_type

    return "OTHER"


if __name__ == "__main__":
    test_cases = [
        ("Счёт № 15", "INVOICE"),
        ("Акт выполненных работ", "ACT"),
        ("Договор оказания услуг", "CONTRACT"),
        ("Платежное поручение", "PAYMENT_CONFIRMATION"),
        ("Ведомость заработной платы", "PAYROLL"),
        ("Кассовый чек", "RECEIPT"),
        ("Неизвестный документ", "OTHER"),
    ]

    for text, expected in test_cases:
        result = determine_document_type(text)
        print(f"{text}: {result} (expected: {expected})")
