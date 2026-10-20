import re


def parse_invoice_data(text: str) -> dict:
    data = {
        "document_type": "UNKNOWN",
        "number": None,
        "date": None,
        "inn": None,
        "amount": None,
        "errors": [],
    }

    text_lower = text.lower()
    if "счет" in text_lower or "счёт" in text_lower:
        data["document_type"] = "INVOICE"
    elif "акт" in text_lower:
        data["document_type"] = "ACT"
    elif "чек" in text_lower:
        data["document_type"] = "RECEIPT"
    else:
        data["errors"].append("Не удалось определить тип документа")

    match = re.search(r"(?:[Сс]ч[её]т|[Аа]кт|[Чч]ек)\D*?№\s*(\d+)", text)
    if match:
        data["number"] = match.group(1)
    else:
        data["errors"].append("Не удалось определить номер")

    match = re.search(r"(\d{2})\.(\d{2})\.(\d{4})", text)
    if match:
        data["date"] = f"{match.group(3)}-{match.group(2)}-{match.group(1)}"
    else:
        data["errors"].append("Не удалось определить дату")

    match = re.search(r"ИНН\s*(\d{10,12})", text)
    if match:
        data["inn"] = match.group(1)
    else:
        data["errors"].append("Не удалось определить ИНН")

    match = re.search(r"(?:Итого|Всего к оплате)[:\s]*([\d\s]+[.,]\d{2})", text, re.IGNORECASE)
    if match:
        raw_sum = match.group(1).replace(" ", "").replace(",", ".")
        try:
            data["amount"] = float(raw_sum)
        except ValueError:
            data["errors"].append("Не удалось преобразовать сумму")
    else:
        data["errors"].append("Не удалось определить сумму")

    if data["document_type"] == "UNKNOWN":
        data["document_type"] = "INVOICE"

    return data
